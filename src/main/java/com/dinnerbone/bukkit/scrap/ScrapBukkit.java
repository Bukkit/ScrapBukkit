
package com.dinnerbone.bukkit.scrap;

import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
        //PluginManager pm = getServer().getPluginManager();
    }

    public void onEnable() {       
        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
    }

    protected boolean teleport(final Player victim, final String destName) {
        Player destination = getServer().getPlayer(destName);
        return teleport(victim, destination);
    }

    protected boolean teleport(final String victimName, final Player destination) {
        Player victim = getServer().getPlayer(victimName);
        return teleport(victim, destination);
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
            return teleport(victim, destination);
        }
    }

    protected boolean teleport(final Player victim, final Player destination) {
        if ((victim == null) || (destination == null)) 
            return false;
        
        victim.teleportTo(destination.getLocation());
        return true;
    }
    

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (!sender.isOp()) {
            return true;
        }
        String[] trimmedArgs = args;
        String commandName = command.getName().toLowerCase();
        
        Player player = null;
        if (sender.isPlayer()) {
            player = (Player)sender;
        } else if (args.length > 0){
            // Assume first parameter of args is a player and remove it. null if player not found. 
            player = getServer().getPlayer(args[0]);
            trimmedArgs = new String[args.length-1];
            for(int i = 1; i < args.length; i++)
                trimmedArgs[i-1] = args[i];
        }

        if (commandName.equals("tp")) {
            return performTeleport(sender, trimmedArgs, player);
        } else if (commandName.equals("clear")) {
            return performInventoryClean(sender, trimmedArgs, player);
        } else if (commandName.equals("take")) {
            return performTake(sender, trimmedArgs, player);
        } else if (commandName.equals("give")) {
            return performGive(sender, trimmedArgs, player);
        } else if (commandName.equals("tphere")) {
            return performTPHere(sender, trimmedArgs, player);
        } else if (commandName.equals("time")) {
            return performTimeCheck(sender, trimmedArgs);
        }
        return false;
    }

    private boolean performGive(CommandSender sender, String[] split, Player player) {
        try {
            if (split.length >= 1) {
                boolean isInt = true;
                for (int i = 0; i < split[0].length(); i++) {
                    if (!Character.isDigit(split[0].charAt(i))) {
                        isInt = false;
                    }
                }
                int itemId;
                if (isInt) {
                    itemId = Integer.parseInt(split[0]);
                }
                else {
                    itemId = Material.getMaterial(split[0].toUpperCase()).getId();
                }
                int amount = 1;
                Byte data = null;
                Player victim = player;
                if (split.length >= 2) {
                    amount = Integer.parseInt(split[1]);
                    if (split.length >= 3) {
                        if(split[2].startsWith("-d")) {
                            data = Byte.valueOf(split[2].substring(2));
                            if(split.length >= 4) {
                                victim = getServer().getPlayer(split[3]);
                            }
                        }
                        else {
                            victim = getServer().getPlayer(split[2]);
                        }
                    }
                }

                if (victim == null) {
                    sender.sendMessage("Give failed. Player not found");
                    return false;
                }
                
                sender.sendMessage("Giving " + amount + " " + Material.getMaterial(itemId).name() + " to " + (player == victim ? "yourself" : player.getName()));
                victim.getInventory().addItem(new ItemStack(itemId, amount, (byte)0, data));
                if(player != victim) victim.sendMessage("A player has added some " + Material.getMaterial(itemId).name() + "to your inventory");
                return true;
            }
        } catch (Exception exc) {
            sender.sendMessage("Correct usage is /give <ItemName | ItemId> [Amount]");
        }
        return false;
    }

    private boolean performTake(CommandSender sender, String[] split, Player player) {
        try {
        Player victim = player;
        if (split.length >= 1) {
            int itemId = Integer.parseInt(split[0]);
            int amount = 1;
            if (split.length >= 2) {
                amount = Integer.parseInt(split[1]);
                if (split.length >= 3) {
                    victim = getServer().getPlayer(split[2]);
                }
            }

            sender.sendMessage("Taking " + amount + " " + Material.getMaterial(itemId).name() + " from " + (player == victim ? "yourself" : player.getName()));
            victim.getInventory().removeItem(new ItemStack(itemId, amount));
            if(player != victim) victim.sendMessage("A player has removed some " + Material.getMaterial(itemId).name() + "from your inventory");
            return true;
        }
        } catch (Exception exc) {
            player.sendMessage("Correct usage is /take <ItemName | ItemId> [Amount]");
        }
        return false;
    }

    private boolean performInventoryClean(CommandSender sender, String[] split, Player player) {
        Player victim = player;
        if (split.length == 1) {
            victim = getServer().getPlayer(split[0]);
        }
        sender.sendMessage( "Cleared " + (player == victim ? "your" : victim.getName() + "'s") + " inventory");
        if(player != victim) victim.sendMessage("Your inventory has been cleared by a player");
        victim.getInventory().clear();
        return true;
    }

    private boolean performTeleport(CommandSender sender, String[] split, Player player) {
        if (split.length == 1) {
            String dest = split[0];
            
            if (dest.equalsIgnoreCase("*")) {
                sender.sendMessage(ChatColor.RED + "Incorrect usage of wildchar *");
            } else {
                if (!teleport(player, dest)) {
                    sender.sendMessage(ChatColor.RED + "Could not teleport to " + dest
                            + " (Is the name spelt correctly?)");
                }
            }
            return true;
        } else if (split.length == 2) {
            String victim = split[0];
            String dest = split[1];

            if (dest.equalsIgnoreCase("*")) {
                sender.sendMessage(ChatColor.RED + "Incorrect usage of wildchar *");
            } else {
                if (!teleport(victim, dest)) {
                    sender.sendMessage(ChatColor.RED + "Could not teleport "
                            + victim + " to " + dest + " (Are the names spelt correctly?)");
                }
            }
            return true;
        }
        return false;
    }
    
    private boolean performTPHere(CommandSender sender, String[] split, Player player) {
        if (split.length == 1) {
            String victim = split[0];

            if (!teleport(victim, player)) {
                sender.sendMessage(ChatColor.RED + "Could not teleport " + victim + " to you (Is the name spelt correctly?)");
            }
            return true;
        }
        return false;
    }

    private boolean performTimeCheck(CommandSender sender, String[] split) {
        Server server = getServer();
        long time = server.getTime();
        long relativeTime = time % 24000;
        long startOfDay = time + (24000 - relativeTime); // start of the next day
        
        if (split.length == 0) {
            int hours = (int)((time / 1000+8) % 24);
            int minutes = (int) (60 * (time % 1000) / 1000);
            sender.sendMessage(String.format( "Time: %02d:%02d", hours, minutes));
            return true;
        } else if (split.length == 1) {
            String timeStr = split[0];
            if (timeStr.equalsIgnoreCase("help")) {
                // Gets handled later.
            } else if (timeStr.equalsIgnoreCase("raw")) {
                sender.sendMessage("Raw:  " + time);
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
        return false;
    }
}