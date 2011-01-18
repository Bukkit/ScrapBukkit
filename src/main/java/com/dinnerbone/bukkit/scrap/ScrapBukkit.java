
package com.dinnerbone.bukkit.scrap;

import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
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
    

    @Override
    public boolean onCommand(Player player, Command command, String commandLabel, String[] args) {
        String[] split = args;
        String commandName = command.getName().toLowerCase();

        if (commandName.equals("tp")) {
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
                return true;
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
                return true;
            }
        } else if (commandName.equals("clear")) {
            Player victim = player;
            if (split.length == 2) {
                victim = getServer().getPlayer(split[1]);
            }
            player.sendMessage( "Cleared " + (player == victim ? "your" : victim.getName() + "'s") + " inventory");
            if(player != victim) victim.sendMessage("Your inventory has been cleared by a player");
            victim.getInventory().clear();
            return true;
        } else if (commandName.equals("take")) {
            try {
            Player victim = player;
            if (split.length >= 2) {
                int itemId = Integer.parseInt(split[1]);
                int amount = 1;
                if (split.length >= 3) {
                    amount = Integer.parseInt(split[2]);
                    if (split.length >= 4) {
                        victim = getServer().getPlayer(split[3]);
                    }
                }

                player.sendMessage("Taking " + amount + " " + Material.getMaterial(itemId).name() + " from " + (player == victim ? "yourself" : player.getName()));
                victim.getInventory().removeItem(new ItemStack(itemId, amount));
                if(player != victim) victim.sendMessage("A player has removed some " + Material.getMaterial(itemId).name() + "from your inventory");
                return true;
            }
            } catch (Exception exc) {
                player.sendMessage("Correct usage is /take <ItemName | ItemId> [Amount]");
            }
        } else if (commandName.equals("give")) {
            try {
                if (split.length >= 2) {
                    boolean isInt = true;
                    for (int i = 0; i < split[1].length(); i++) {
                        if (!Character.isDigit(split[1].charAt(i))) {
                            isInt = false;
                        }
                    }
                    int itemId;
                    if (isInt) {
                        itemId = Integer.parseInt(split[1]);
                    }
                    else {
                        itemId = Material.getMaterial(split[1].toUpperCase()).getId();
                    }
                    int amount = 1;
                    Byte data = null;
                    Player victim = player;
                    if (split.length >= 3) {
                        amount = Integer.parseInt(split[2]);
                        if (split.length >= 4) {
                            if(split[3].startsWith("-d")) {
                                data = Byte.valueOf(split[3].substring(2));
                                if(split.length >= 5) {
                                    victim = getServer().getPlayer(split[4]);
                                }
                            }
                            else {
                                victim = getServer().getPlayer(split[3]);
                            }
                        }
                    }

                    player.sendMessage("Giving " + amount + " " + Material.getMaterial(itemId).name() + " to " + (player == victim ? "yourself" : player.getName()));
                    victim.getInventory().addItem(new ItemStack(itemId, amount, (byte)0, data));
                    if(player != victim) victim.sendMessage("A player has added some " + Material.getMaterial(itemId).name() + "to your inventory");
                    return true;
                }
            } catch (Exception exc) {
                player.sendMessage("Correct usage is /give <ItemName | ItemId> [Amount]");
            }
        } else if (commandName.equals("tphere")) {
            if (split.length == 2) {
                String victim = split[1];

                if (!teleport(victim, player)) {
                    player.sendMessage(ChatColor.RED + "Could not teleport " + victim
                            + " to you (Is the name spelt correctly?)");
                }
                return true;
            }
        } else if (commandName.equals("time")) {
            Server server = getServer();
            long time = server.getTime();
            long relativeTime = time % 24000;
            long startOfDay = time - relativeTime;
            if (split.length == 1) {
                int hours = (int)((time / 1000+8) % 24);
                int minutes = (int) (60 * (time % 1000) / 1000);
                player.sendMessage(String.format( "Time: %02d:%02d", hours, minutes));
                return true;
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
                return true;
            }
        }
        return false;
    }
}
