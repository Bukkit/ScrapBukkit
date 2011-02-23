package com.dinnerbone.bukkit.scrap;

import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;

public class ScrapBukkitPlayerListener extends PlayerListener {
    
    protected ScrapBukkit plugin;
    
    public ScrapBukkitPlayerListener(ScrapBukkit plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void onPlayerQuit(PlayerEvent event) {
        plugin.forgetPlayer(event.getPlayer());
    }
    
}
