package com.dinnerbone.bukkit.scrap.commands;

import com.dinnerbone.bukkit.scrap.ScrapBukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearPluginCommand implements CommandExecutor {
    private final ScrapBukkit plugin;

    public ClearPluginCommand(ScrapBukkit plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 1) {
            return false;
        }

        Player player;

        if (args.length == 1) {
            player = plugin.matchPlayer(args, sender);
            if (player == null) return false;
        } else if (plugin.anonymousCheck(sender)) {
            return false;
        } else {
            player = (Player)sender;
        }

        if ((player == sender) && (!sender.hasPermission("scrapbukkit.clear.self"))) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to clear your own inventory");
            return true;
        } else if (!sender.hasPermission("scrapbukkit.clear.other")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to clear the inventory of " + player.getDisplayName());
            return true;
        }

        sender.sendMessage("Cleared inventory of " + player.getDisplayName());
        player.getInventory().clear();
        return true;
    }
}
