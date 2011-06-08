package com.dinnerbone.bukkit.scrap.commands;

import com.dinnerbone.bukkit.scrap.ScrapBukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportPluginCommand implements CommandExecutor {
    private final ScrapBukkit plugin;

    public TeleportPluginCommand(ScrapBukkit plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage("You do not have permission to teleport players");
            return false;
        }

        if (args.length == 1) {
            if (plugin.anonymousCheck(sender)) return false;
            Player player = (Player)sender;
            String dest = args[0];

            if (dest.equalsIgnoreCase("*")) {
                sender.sendMessage(ChatColor.RED + "Incorrect usage of wildchar *");
                return false;
            } else {
                if (!plugin.teleport(player, dest)) {
                    sender.sendMessage(ChatColor.RED + "Could not teleport to " + dest
                            + " (Is the name spelt correctly?)");
                    return false;
                }
            }
            return true;
        } else if (args.length == 2) {
            String victim = args[0];
            String dest = args[1];

            if (dest.equalsIgnoreCase("*")) {
                sender.sendMessage(ChatColor.RED + "Incorrect usage of wildchar *");
                return false;
            } else {
                if (!plugin.teleport(victim, dest)) {
                    sender.sendMessage(ChatColor.RED + "Could not teleport "
                            + victim + " to " + dest + " (Are the names spelt correctly?)");
                    return false;
                }
            }
            return true;
        } else if (args.length == 3) {
            Player player = null;
            if (plugin.anonymousCheck(sender)) return false;

            player = (Player) sender;
            Double tx = null;
            Double ty = null;
            Double tz = null;
            try {
                tx = Double.valueOf(args[0]);
                ty = Double.valueOf(args[1]);
                tz = Double.valueOf(args[2]);
            } catch (NumberFormatException ex) {
                sender.sendMessage(ChatColor.RED + "Not valid coordinates!");
                return false;
            }
            plugin.teleport(player, tx, ty, tz);
            return true;
        } else if (args.length == 4) {
            Player player = null;
            if (plugin.anonymousCheck(sender)) return false;

            player = (Player) sender;
            Double tx = null;
            Double ty = null;
            Double tz = null;
            try {
                tx = Double.valueOf(args[1]);
                ty = Double.valueOf(args[2]);
                tz = Double.valueOf(args[3]);
            } catch (NumberFormatException ex) {
                sender.sendMessage(ChatColor.RED + "Not valid coordinates!");
                return false;
            }
            plugin.teleport(args[0], player, tx, ty, tz);
            plugin.announceCheat(
                sender, 
                "Teleported " + player.getDisplayName() 
                    + " to " + tx + " " + ty + " " + tz, 
                false);
            return true;
        }
        return false;
    }
}
