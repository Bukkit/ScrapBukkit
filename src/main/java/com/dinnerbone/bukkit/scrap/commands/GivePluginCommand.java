package com.dinnerbone.bukkit.scrap.commands;

import com.dinnerbone.bukkit.scrap.ScrapBukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GivePluginCommand implements CommandExecutor {
    private final ScrapBukkit plugin;

    public GivePluginCommand(ScrapBukkit plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((args.length > 3) || (args.length == 0)) {
            return false;
        }
        if (!sender.isOp()) {
            sender.sendMessage("You do not have permission to give players items");
            return false;
        }

        Player player = null;
        Material material = null;
        int count = 1;
        String[] gData = null;
        Byte bytedata = null;
        if (args.length >= 1) {
            gData = args[0].split(":");
            material = Material.matchMaterial(gData[0]);
            if (gData.length == 2) {
                bytedata = Byte.valueOf(gData[1]);
            }
        }
        if (args.length >= 2) {
            try {
                count = Integer.parseInt(args[1]);
            } catch (NumberFormatException ex) {
                sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a number!");
                return false;
            }
        }
        if (args.length == 3) {
            player = plugin.getServer().getPlayer(args[2]);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a valid player!");
                return false;
            }
        } else {
            if (plugin.anonymousCheck(sender)) {
                return false;
            } else {
                player = (Player) sender;
            }
        }
        if (material == null) {
            sender.sendMessage(ChatColor.RED + "Unknown item");
            return false;
        }
        if (bytedata != null) {
            player.getInventory().addItem(new ItemStack(material, count, (short) 0, bytedata));
        } else {
            player.getInventory().addItem(new ItemStack(material, count));
        }
        plugin.announceCheat(sender, "Given " + player.getDisplayName() + " " + count + " " + material.toString());
        return true;
    }
}
