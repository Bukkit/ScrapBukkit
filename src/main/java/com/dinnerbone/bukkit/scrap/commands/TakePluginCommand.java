package com.dinnerbone.bukkit.scrap.commands;

import com.dinnerbone.bukkit.scrap.ScrapBukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TakePluginCommand implements CommandExecutor {
    private final ScrapBukkit plugin;

    public TakePluginCommand(ScrapBukkit plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((args.length > 3) || (args.length == 0)) {
            return false;
        }

        Player player;
        Material material;
        int count = -1;

        if (args.length >= 2) {
            player = plugin.matchPlayer(args, sender);
            if (player == null) return false;
            material = Material.matchMaterial(args[1]);
        } else {
            if (plugin.anonymousCheck(sender)) return false;
            player = (Player)sender;
            material = Material.matchMaterial(args[0]);
        }

        if (args.length >= 3) {
            try {
                count = Integer.parseInt(args[2]);
            } catch (NumberFormatException ex) {
                sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a number!");
                return false;
            }
        }

        if ((player == sender) && (!sender.hasPermission("scrapbukkit.take.self"))) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to remove your own items");
            return true;
        } else if (!sender.hasPermission("scrapbukkit.take.other")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to remove items from " + player.getDisplayName());
            return true;
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
}
