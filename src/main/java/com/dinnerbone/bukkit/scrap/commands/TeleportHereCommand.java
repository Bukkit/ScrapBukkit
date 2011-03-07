package com.dinnerbone.bukkit.scrap.commands;

import com.dinnerbone.bukkit.scrap.ScrapBukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportHereCommand implements CommandExecutor {
    private final ScrapBukkit plugin;

    public TeleportHereCommand(ScrapBukkit plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage("You do not have permission to teleport players");
            return false;
        }
        if ((args.length == 1) && (!plugin.anonymousCheck(sender))) {
            String victim = args[0];

            if (plugin.teleport(victim, (Player)sender)) {
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
}
