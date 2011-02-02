package com.dinnerbone.bukkit.scrap;

import java.io.File;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;

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

    private boolean anonymousCheck(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Cannot execute that command, I don't know who you are!");
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        String[] trimmedArgs = args;
        String commandName = command.getName().toLowerCase();

        if (commandName.equals("/tp")) {
            return performTeleport(sender, trimmedArgs);
        } else if (commandName.equals("/clear")) {
            return performInventoryClean(sender, trimmedArgs);
        } else if (commandName.equals("/take")) {
            return performTake(sender, trimmedArgs);
        } else if (commandName.equals("/give")) {
            return performGive(sender, trimmedArgs);
        } else if (commandName.equals("/tphere")) {
            return performTPHere(sender, trimmedArgs);
        } else if (commandName.equals("/time")) {
            return performTimeCheck(sender, trimmedArgs);
        }
        return false;
    }

    private boolean performGive(CommandSender sender, String[] split) {
        if ((split.length > 3) || (split.length == 0)) {
            return false;
        }
        if (!sender.isOp()) {
            sender.sendMessage("You do not have permission to give players items");
            return false;
        }

        Player player = null;
        Material material = null;
        int count = 1;

        if (split.length >= 2) {
            player = matchPlayer(split, sender);
            if (player == null) return false;
            material = Material.matchMaterial(split[1]);
        } else {
            if (anonymousCheck(sender)) return false;
            player = (Player)sender;
            material = Material.matchMaterial(split[0]);
        }

        if (split.length >= 3) {
            try {
                count = Integer.parseInt(split[2]);
            } catch (NumberFormatException ex) {
                sender.sendMessage(ChatColor.RED + "'" + split[2] + "' is not a number!");
                return false;
            }
        }

        if (material == null) {
            sender.sendMessage(ChatColor.RED + "Unknown item");
            return false;
        }

        player.getInventory().addItem(new ItemStack(material, count));
        sender.sendMessage("Given " + player.getDisplayName() + " " + count + " " + material.toString());
        return true;
    }

    private Player matchPlayer(String[] split, CommandSender sender) {
        Player player;
        List<Player> players = getServer().matchPlayer(split[0]);
        if (players.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Unknown player");
            player = null;
        } else {
            player = players.get(0);
        }
        return player;
    }

    private boolean performTake(CommandSender sender, String[] split) {
        if ((split.length > 3) || (split.length == 0)) {
            return false;
        }
        if (!sender.isOp()) {
            sender.sendMessage("You do not have permission to take players' items");
            return false;
        }

        Player player = null;
        Material material = null;
        int count = -1;

        if (split.length >= 2) {
            player = matchPlayer(split, sender);
            if (player == null) return false;
            material = Material.matchMaterial(split[1]);
        } else {
            if (anonymousCheck(sender)) return false;
            player = (Player)sender;
            material = Material.matchMaterial(split[0]);
        }

        if (split.length >= 3) {
            try {
                count = Integer.parseInt(split[2]);
            } catch (NumberFormatException ex) {
                sender.sendMessage(ChatColor.RED + "'" + split[2] + "' is not a number!");
                return false;
            }
        }

        if (material == null) {
            sender.sendMessage(ChatColor.RED + "Unknown item");
            return false;
        }

        if (count < 0) {
            player.getInventory().remove(material);
        } else {
            player.getInventory().remove(new ItemStack(material, count));
        }
        sender.sendMessage("Took " + count + " " + material.toString() + " from " + player.getDisplayName());
        return true;
    }

    private boolean performInventoryClean(CommandSender sender, String[] split) {
        if (!sender.isOp()) {
            sender.sendMessage("You do not have permission to clean players' inventories");
            return false;
        }
        if (split.length > 1) {
            return false;
        }

        Player player = null;

        if (split.length == 1) {
            player = matchPlayer(split, sender);
            if (player == null) return false;
        } else if (anonymousCheck(sender)) {
            return false;
        } else {
            player = (Player)sender;
        }

        sender.sendMessage("Cleared inventory of " + player.getDisplayName());
        player.getInventory().clear();
        return true;
    }

    private boolean performTeleport(CommandSender sender, String[] split) {
        if (!sender.isOp()) {
            sender.sendMessage("You do not have permission to teleport players");
            return false;
        }
        
        if (split.length == 1) {
            if (anonymousCheck(sender)) return false;
            Player player = (Player)sender;
            String dest = split[0];
            
            if (dest.equalsIgnoreCase("*")) {
                sender.sendMessage(ChatColor.RED + "Incorrect usage of wildchar *");
                return false;
            } else {
                if (!teleport(player, dest)) {
                    sender.sendMessage(ChatColor.RED + "Could not teleport to " + dest
                            + " (Is the name spelt correctly?)");
                    return false;
                }
            }
            return true;
        } else if (split.length == 2) {
            String victim = split[0];
            String dest = split[1];

            if (dest.equalsIgnoreCase("*")) {
                sender.sendMessage(ChatColor.RED + "Incorrect usage of wildchar *");
                return false;
            } else {
                if (!teleport(victim, dest)) {
                    sender.sendMessage(ChatColor.RED + "Could not teleport "
                            + victim + " to " + dest + " (Are the names spelt correctly?)");
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    private boolean performTPHere(CommandSender sender, String[] split) {
        if ((split.length == 1) && (!anonymousCheck(sender))) {
            String victim = split[0];

            if (teleport(victim, (Player)sender)) {
                sender.sendMessage("Done.");
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "Could not teleport " + victim + " to you (Is the name spelt correctly?)");
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean performTimeCheck(CommandSender sender, String[] split) {
        World world = sender instanceof Player ? ((Player)sender).getWorld() : getServer().getWorlds()[0];
        long time = world.getTime();
        
        if (split.length == 0) {
            int hours = (int)((time / 1000+8) % 24);
            int minutes = (int) (60 * (time % 1000) / 1000);
            sender.sendMessage(String.format( "Time: %02d:%02d", hours, minutes));
            return true;
        } else if (split.length == 1) {
            if (!sender.isOp()) {
                sender.sendMessage("You do not have permission to alter the time");
            }

            String timeStr = split[0];
            if (timeStr.equalsIgnoreCase("help")) {
                // Gets handled later.
            } else if (timeStr.equalsIgnoreCase("raw")) {
                sender.sendMessage("Raw:  " + time);
            } else if (timeStr.equalsIgnoreCase("day")) {
                world.setTime(0);
            } else if (timeStr.equalsIgnoreCase("night")) {
                world.setTime(13000);
            } else if (timeStr.startsWith("=")) {
                try {
                    world.setTime(Long.parseLong(timeStr.substring(1)));
                } catch(NumberFormatException ex) {
                    sender.sendMessage("That is not a number");
                    return false;
                }
            } else if (timeStr.startsWith("+")) {
                try {
                    world.setTime(time + Long.parseLong(timeStr.substring(1)));
                } catch(NumberFormatException ex) {
                    sender.sendMessage("That is not a number");
                    return false;
                }
            } else if (timeStr.startsWith("-")) {
                try {
                    world.setTime(time-Long.parseLong(timeStr.substring(1)));
                } catch(NumberFormatException ex) {
                    sender.sendMessage("That is not a number");
                    return false;
                }
            } else {
                return false;
            }

            sender.sendMessage("Done.");
            return true;
        }
        return false;
    }
}