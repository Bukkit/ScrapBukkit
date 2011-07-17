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
        if (args.length == 1) {
            if (plugin.anonymousCheck(sender)) return false;
            Player player = (Player)sender;
            String dest = args[0];

            if (dest.equalsIgnoreCase("*")) {
                sender.sendMessage(ChatColor.RED + "Incorrect usage of wildchar *");
                return false;
            } else {
                if (!sender.hasPermission("scrapbukkit.tp.self")) {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to teleport yourself to other players");
                    return true;
                }

                if (!plugin.teleport(player, dest)) {
                    sender.sendMessage(ChatColor.RED + "Could not teleport to " + dest
                            + " (Is the name spelt correctly?)");
                    return false;
                }
            }
            return true;
        } else if (args.length == 2) {
            String vict = args[0];
            Player victim = plugin.getServer().getPlayer(vict);
            String dest = args[1];
            Player target = plugin.getServer().getPlayer(dest);

            if (dest.equalsIgnoreCase("*")) {
                sender.sendMessage(ChatColor.RED + "Incorrect usage of wildchar *");
                return false;
            } else {
                if ((target == sender) && (!sender.hasPermission("scrapbukkit.tp.here"))) {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to teleport other players to yourself");
                    return true;
                } else if ((victim == sender) && (!sender.hasPermission("scrapbukkit.tp.self"))) {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to teleport yourself to other players");
                    return true;
                } else if (!sender.hasPermission("scrapbukkit.tp.other")) {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to teleport other players");
                    return true;
                }

                if (!plugin.teleport(victim, dest)) {
                    sender.sendMessage(ChatColor.RED + "Could not teleport "
                            + vict + " to " + dest + " (Are the names spelt correctly?)");
                    return false;
                }
            }
            return true;
        } else if (args.length == 3) {
            Player player;
            if (plugin.anonymousCheck(sender)) return false;

            player = (Player) sender;
            Double tx;
            Double ty;
            Double tz;
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
            Player player;
            if (plugin.anonymousCheck(sender)) return false;

            player = (Player) sender;
            Double tx;
            Double ty;
            Double tz;
            try {
                tx = Double.valueOf(args[1]);
                ty = Double.valueOf(args[2]);
                tz = Double.valueOf(args[3]);
            } catch (NumberFormatException ex) {
                sender.sendMessage(ChatColor.RED + "Not valid coordinates!");
                return false;
            }
            plugin.teleport(args[0], player, tx, ty, tz);
            return true;
        }
        return false;
    }
}
