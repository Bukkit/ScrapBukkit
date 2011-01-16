
package com.dinnerbone.bukkit.scrap;

import org.bukkit.ChatColor;
import org.bukkit.ItemStack;
import org.bukkit.Material;
import org.bukkit.Player;
import org.bukkit.Server;
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
        final String command = split[0];

        if (event.isCancelled()) {
            return;
        }

        if (command.equalsIgnoreCase("/tp")) {
            if (split.length == 2) {
                String dest = split[1];
                
                if (dest.equalsIgnoreCase("*")) {
                    player.sendMessage(ChatColor.RED + "Incorrect usage of wildchar *");
                } else {
                    if (!plugin.teleport(player, dest)) {
                        player.sendMessage(ChatColor.RED + "Could not teleport to " + dest
                                + " (Is the name spelt correctly?)");
                    }
                }

            } else if (split.length == 3) {
                String victim = split[1];
                String dest = split[2];

                if (dest.equalsIgnoreCase("*")) {
                    player.sendMessage(ChatColor.RED + "Incorrect usage of wildchar *");
                } else {
                    if (!plugin.teleport(victim, dest)) {
                        player.sendMessage(ChatColor.RED + "Could not teleport "
                                + victim + " to " + dest + " (Are the names spelt correctly?)");
                    }
                }
            } else {
                player.sendMessage(ChatColor.RED + "Incorrect usage of command /tp. Examples:");
                player.sendMessage(ChatColor.RED + "/tp Dinnerbone - teleports you to the player named Dinnerbone");
                player.sendMessage(ChatColor.RED + "/tp Bukkit Walrus - teleports a player named Bukkit to a player named Walrus");
                player.sendMessage(ChatColor.RED + "/tp * Monster - telports every online player to a player named Monster");
            }

            event.setCancelled(true);
        } else if (command.equalsIgnoreCase("/givetest")) {
            if (split.length >= 2) {
                int itemId = Integer.parseInt(split[1]);
                int amount = 1;
                if (split.length >= 3) {
                    amount = Integer.parseInt(split[2]);
                }

                player.sendMessage( "Giving "+amount+" x "+ Material.getMaterial(itemId).name() );

                player.getInventory().addItem(new ItemStack(itemId, amount));
                event.setCancelled(true);
            }
        } else if (command.equalsIgnoreCase("/tphere")) {
            if (split.length == 2) {
                String victim = split[1];

                if (!plugin.teleport(victim, player)) {
                    player.sendMessage(ChatColor.RED + "Could not teleport " + victim
                            + " to you (Is the name spelt correctly?)");
                }
            } else {
                player.sendMessage(ChatColor.RED + "Incorrect usage of command /tphere. Examples:");
                player.sendMessage(ChatColor.RED + "/tphere Dinnerbone - teleports the player named Dinnerbone to you");
                player.sendMessage(ChatColor.RED + "/tphere * - teleports every online player to yourself");
            }

            event.setCancelled(true);
        } else if (command.equalsIgnoreCase("/time")) {
            Server server = plugin.getServer();
            long time = server.getTime();
            long relativeTime = time % 24000;
            long startOfDay = time - relativeTime;
            if (split.length == 1) {
            	int hours = (int)((time / 1000+8) % 24);
            	int minutes = (((int)(time % 1000)) / 1000) * 60;
            	player.sendMessage("Time: "+hours+":"+minutes);
            	event.setCancelled(true);
            } else if (split.length == 2) {
            	String timeStr = split[1];
            	if (timeStr.equalsIgnoreCase("help")) {
            		// Gets handled later.
            	} else if (timeStr.equalsIgnoreCase("raw")) {
            		player.sendMessage("Raw:  " + time);
            		event.setCancelled(true);
            	} else if (timeStr.equalsIgnoreCase("day")) {
            		server.setTime(startOfDay);
            		event.setCancelled(true);
                } else if (timeStr.equalsIgnoreCase("sunset")) {
                    server.setTime(startOfDay +  12000);
                    event.setCancelled(true);
                } else if (timeStr.equalsIgnoreCase("night")) {
                    server.setTime(startOfDay + 13800);
                    event.setCancelled(true);
                } else if (timeStr.equalsIgnoreCase("sunrise")) {
                    server.setTime(startOfDay + 22200);
                    event.setCancelled(true);
                } else if (timeStr.startsWith("=")) {
            		try {
            		server.setTime(Long.parseLong(timeStr.substring(1)));
            		event.setCancelled(true);
            		} catch(NumberFormatException ex) { }
            	} else if (timeStr.startsWith("+")) {
            		try {
            		server.setTime(time+Long.parseLong(timeStr.substring(1)));
            		event.setCancelled(true);
            		} catch(NumberFormatException ex) { }
            	} else if (timeStr.startsWith("-")) {
            		try {
            		server.setTime(time-Long.parseLong(timeStr.substring(1)));
            		event.setCancelled(true);
            		} catch(NumberFormatException ex) { }
            	} else {
            		try {
            		relativeTime = (Integer.parseInt(timeStr)*1000-8000+24000)%24000;
            		server.setTime(startOfDay + relativeTime);
            		event.setCancelled(true);
            		} catch(NumberFormatException ex) { }
            	}
            }
            
            if (!event.isCancelled()) {
            	player.sendMessage(ChatColor.RED + "Incorrect usage of command /time. Examples:");
            	player.sendMessage(ChatColor.RED + "/time - results in current time (13.00)");
            	player.sendMessage(ChatColor.RED + "/time 13 - sets the time to 13.00");
            	player.sendMessage(ChatColor.RED + "/time day - makes it day");
            	player.sendMessage(ChatColor.RED + "/time night - makes it night");
            	player.sendMessage(ChatColor.RED + "/time raw - results in current raw time");
            	player.sendMessage(ChatColor.RED + "/time =24000 - sets the current raw time (48000 is two days)");
            	player.sendMessage(ChatColor.RED + "/time +1000 - adds raw time (1000 is one hour)");
            	player.sendMessage(ChatColor.RED + "/time -1000 - substracts raw time");
            	event.setCancelled(true);
            }
        }
    }
}
