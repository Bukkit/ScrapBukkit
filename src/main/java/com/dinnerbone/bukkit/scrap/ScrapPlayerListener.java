
package com.dinnerbone.bukkit.scrap;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

/**
 * Handle all Player related events
 * 
 * @author Dinnerbone
 */
public class ScrapPlayerListener extends PlayerListener {
    private final ScrapBukkit plugin;

    public ScrapPlayerListener(ScrapBukkit instance) {
        plugin = instance;
    }
    
    @Override
    public void onPlayerCommand(PlayerChatEvent event) {
        String[] split = event.getMessage().split(" ");
        Player player = event.getPlayer();

        if (split[0].equalsIgnoreCase("/tp")) {
            if (split.length == 2) {
                String dest = split[1];
                
                if (dest.equalsIgnoreCase("*")) {
                    player.sendMessage(Color.RED + "Incorrect usage of wildchar *");
                } else {
                    if (!plugin.teleport(player, dest)) {
                        player.sendMessage(Color.RED + "Could not teleport to " + dest
                                + " (Is the name spelt correctly?)");
                    }
                }

            } else if (split.length == 3) {
                String victim = split[1];
                String dest = split[2];

                if (dest.equalsIgnoreCase("*")) {
                    player.sendMessage(Color.RED + "Incorrect usage of wildchar *");
                } else {
                    if (!plugin.teleport(victim, dest)) {
                        player.sendMessage(Color.RED + "Could not teleport "
                                + victim + " to " + dest + " (Are the names spelt correctly?)");
                    }
                }
            } else {
                player.sendMessage("Incorrect usage of command /tp. Examples:");
                player.sendMessage("/tp Dinnerbone - teleports you to the player named Dinnerbone");
                player.sendMessage("/tp Bukkit Walrus - teleports a player named Bukkit to a player named Walrus");
                player.sendMessage("/tp * Monster - telports every online player to a player named Monster");
            }

            event.setCancelled(true);
        }
    }
}
