/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.wgblockphysicsflags;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Lectern;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.player.PlayerTakeLecternBookEvent;

/**
 *
 * @author crash
 */
public class WGBlockPhysicsPlugin extends JavaPlugin implements Listener {
    WorldGuardPlugin wgp = null;
    WorldGuard wg = null;
    
    
    
    public static final StateFlag FLAG_BLOCK_PHYSICS = new StateFlag("block-physics", true);
    //public static final StateFlag FLAG_BLOCK_PHYSICS_WARNING = new StateFlag("block-physics-warning", true);
    
    
    
    public WorldGuard getWorldGuard(){ return wg; }
    public WorldGuardPlugin getWorldGuardPlugin(){ return wgp; }
    
    private WorldGuardPlugin findWorldGuardPlugin() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");

        // WorldGuard may not be loaded
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null; // Maybe you want throw an exception instead
        }

        return (WorldGuardPlugin) plugin;
    }
    
    
    private boolean wgInit(){
        wgp = findWorldGuardPlugin();
        wg = WorldGuard.getInstance();
        if(wgp==null || wg==null){
            return false; 
        }
        
        FlagRegistry registry = wg.getFlagRegistry();
        try {
            // register our flag with the registry
            registry.register(FLAG_BLOCK_PHYSICS);
            //registry.register(FLAG_BLOCK_PHYSICS_WARNING);
            return true;
        } catch (FlagConflictException e) {
            // some other plugin registered a flag by the same name already.
            // you may want to re-register with a different name, but this
            // could cause issues with saved flags in region files. it's better
            // to print a message to let the server admin know of the conflict
            getLogger().severe("Could not register WG flags due to a conflict with another plugin");
            return false;
        }
    }
    
    private boolean pluginInit(){
        return true;
    }
    
    @Override
    public void onLoad(){
        if(!wgInit()) return;
    }
    
    @Override
    public void onEnable(){
        getLogger().info("Enabling...");
        if(!pluginInit()) return;
        this.getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("Enabled.");
    }
    
    @Override
    public void onDisable(){
        getLogger().info("Disabling...");
        getLogger().info("Disabled."); 
   }



        @EventHandler(priority=EventPriority.HIGHEST)
	public void onBlockPhysicsEvent(BlockPhysicsEvent event){
            Location loc = event.getBlock().getLocation();

            LocalPlayer wgPlayer = null;

            com.sk89q.worldedit.util.Location wgLoc = BukkitAdapter.adapt(loc);
            RegionQuery query = getWorldGuard().getPlatform().getRegionContainer().createQuery();
            StateFlag.State state = query.queryState(wgLoc, wgPlayer, FLAG_BLOCK_PHYSICS);
            //StateFlag.State warningstate = query.queryState(wgLoc, wgPlayer, FLAG_BLOCK_PHYSICS_WARNING);
            if(state==StateFlag.State.DENY){
               //if(warningstate!=StateFlag.State.DENY) player.sendMessage(ChatColor.RED+"Hey! "+ChatColor.GRAY+"Sorry, but you can't take books from lecterns here.");
               event.setCancelled(true);
            }
	}

}
