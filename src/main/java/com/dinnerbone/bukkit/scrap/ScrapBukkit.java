
package com.dinnerbone.bukkit.scrap;

import java.io.File;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

/**
 * Miscellaneous administrative commands
 *
 * @author Dinnerbone
 */
public class ScrapBukkit extends JavaPlugin {

    public ScrapBukkit(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
        super(pluginLoader, instance, desc, folder, plugin, cLoader);
    }

    public void onDisable() {
        PluginManager pm = getServer().getPluginManager();
    }

    public void onEnable() {       
        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
    }

    protected boolean teleport(final Player victim, final String destName) {
        Player destination = getServer().getPlayer(destName);

        if ((victim == null) || (destination == null)) {
            return false;
        } else {
            victim.teleportTo(destination.getLocation());
            return true;
        }
    }

    protected boolean teleport(final String victimName, final Player destination) {
        Player victim = getServer().getPlayer(victimName);

        if ((victim == null) || (destination == null)) {
            return false;
        } else {
            victim.teleportTo(destination.getLocation());
            return true;
        }
    }

    protected boolean teleport(final String victimName, final String destName) {
        Player destination = getServer().getPlayer(destName);

        if (victimName.equalsIgnoreCase("*")) {
            Player[] players = getServer().getOnlinePlayers();

            for (Player victim : players) {
                if (!victim.equals(destination)) {
                    teleport(victim, destination);
                }
            }

            return true;
        } else {
            Player victim = getServer().getPlayer(victimName);

            if ((victim == null) || (destination == null)) {
                return false;
            } else {
                victim.teleportTo(destination.getLocation());
                return true;
            }
        }
    }

    protected void teleport(final Player victim, final Player destination) {
        victim.teleportTo(destination.getLocation());
    }
    
    public void onCommand(Player player, String command, String[] args) {
        String[] split = args;

        if (command.equalsIgnoreCase("/tp")) {
            if (split.length == 2) {
                String dest = split[1];
                
                if (dest.equalsIgnoreCase("*")) {
                    player.sendMessage(ChatColor.RED + "Incorrect usage of wildchar *");
                } else {
                    if (!teleport(player, dest)) {
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
                    if (!teleport(victim, dest)) {
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

        } else if (command.equalsIgnoreCase("/clear")) {
            player.sendMessage( "Cleared inventory" );
            player.getInventory().clear();
        } else if (command.equalsIgnoreCase("/take")) {
            if (split.length >= 2) {
                int itemId = Integer.parseInt(split[1]);
                int amount = 1;
                if (split.length >= 3) {
                    amount = Integer.parseInt(split[2]);
                }

                player.sendMessage( "Taking "+amount+" x "+ Material.getMaterial(itemId).name() );

                player.getInventory().removeItem(new ItemStack(itemId, amount));
            }
        } else if (command.equalsIgnoreCase("/givetest")) {
            if (split.length >= 2) {
                int itemId = Integer.parseInt(split[1]);
                int amount = 1;
                if (split.length >= 3) {
                    amount = Integer.parseInt(split[2]);
                }

                player.sendMessage( "Giving "+amount+" x "+ Material.getMaterial(itemId).name() );

                player.getInventory().addItem(new ItemStack(itemId, amount));
            }
        } else if (command.equalsIgnoreCase("/tphere")) {
            if (split.length == 2) {
                String victim = split[1];

                if (!teleport(victim, player)) {
                    player.sendMessage(ChatColor.RED + "Could not teleport " + victim
                            + " to you (Is the name spelt correctly?)");
                }
            } else {
                player.sendMessage(ChatColor.RED + "Incorrect usage of command /tphere. Examples:");
                player.sendMessage(ChatColor.RED + "/tphere Dinnerbone - teleports the player named Dinnerbone to you");
                player.sendMessage(ChatColor.RED + "/tphere * - teleports every online player to yourself");
            }

        } else if (command.equalsIgnoreCase("/time")) {
            Server server = getServer();
            long time = server.getTime();
            long relativeTime = time % 24000;
            long startOfDay = time - relativeTime;
            if (split.length == 1) {
                int hours = (int)((time / 1000+8) % 24);
                int minutes = (((int)(time % 1000)) / 1000) * 60;
                player.sendMessage("Time: "+hours+":"+minutes);
            } else if (split.length == 2) {
                String timeStr = split[1];
                if (timeStr.equalsIgnoreCase("help")) {
                    // Gets handled later.
                } else if (timeStr.equalsIgnoreCase("raw")) {
                    player.sendMessage("Raw:  " + time);
                } else if (timeStr.equalsIgnoreCase("day")) {
                    server.setTime(startOfDay);
                } else if (timeStr.equalsIgnoreCase("night")) {
                    server.setTime(startOfDay + 13000);
                } else if (timeStr.startsWith("=")) {
                    try {
                    server.setTime(Long.parseLong(timeStr.substring(1)));
                    } catch(NumberFormatException ex) { }
                } else if (timeStr.startsWith("+")) {
                    try {
                    server.setTime(time+Long.parseLong(timeStr.substring(1)));
                    } catch(NumberFormatException ex) { }
                } else if (timeStr.startsWith("-")) {
                    try {
                    server.setTime(time-Long.parseLong(timeStr.substring(1)));
                    } catch(NumberFormatException ex) { }
                } else {
                    try {
                    relativeTime = (Integer.parseInt(timeStr)*1000-8000+24000)%24000;
                    server.setTime(startOfDay + relativeTime);
                    } catch(NumberFormatException ex) { }
                }
            } else {
                player.sendMessage(ChatColor.RED + "Incorrect usage of command /time. Examples:");
                player.sendMessage(ChatColor.RED + "/time - results in current time (13.00)");
                player.sendMessage(ChatColor.RED + "/time 13 - sets the time to 13.00");
                player.sendMessage(ChatColor.RED + "/time day - makes it day");
                player.sendMessage(ChatColor.RED + "/time night - makes it night");
                player.sendMessage(ChatColor.RED + "/time raw - results in current raw time");
                player.sendMessage(ChatColor.RED + "/time =24000 - sets the current raw time (48000 is two days)");
                player.sendMessage(ChatColor.RED + "/time +1000 - adds raw time (1000 is one hour)");
                player.sendMessage(ChatColor.RED + "/time -1000 - substracts raw time");
            }
        }
    }
}
