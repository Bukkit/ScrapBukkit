package com.dinnerbone.bukkit.scrap.commands;

import com.dinnerbone.bukkit.scrap.ScrapBukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TimePluginCommand implements CommandExecutor {
    private final ScrapBukkit plugin;

    public TimePluginCommand(ScrapBukkit plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        World world = sender instanceof Player ? ((Player) sender).getWorld() : plugin.getServer().getWorlds().get(0);
        long time = world.getTime();

        if (args.length == 0) {
            int hours = (int) ((time / 1000+8) % 24);
            int minutes = (int) (60 * (time % 1000) / 1000);
            sender.sendMessage(String.format("Time: %02d:%02d", hours, minutes));
            return true;
        }

        if (args.length == 1) {
            if (!sender.isOp()) {
                sender.sendMessage("You do not have permission to alter the time");
                return false;
            }
            
            String subcommand = args[0];
            if (subcommand.equalsIgnoreCase("help")) {
                // Gets handled later.
            } else if (subcommand.equalsIgnoreCase("raw")) {
                sender.sendMessage("Raw: " + world.getFullTime());
            } else if (subcommand.equalsIgnoreCase("day")) {
                world.setTime(0);
            } else if (subcommand.equalsIgnoreCase("night")) {
                world.setTime(13000);
            } else if (subcommand.startsWith("=")) {
                try {
                    world.setTime(Long.parseLong(subcommand.substring(1)));
                } catch (NumberFormatException ex) {
                    sender.sendMessage("That is not a number");
                    return false;
                }
            } else if (subcommand.startsWith("+")) {
                try {
                    world.setTime(time + Long.parseLong(subcommand.substring(1)));
                } catch (NumberFormatException ex) {
                    sender.sendMessage("That is not a number");
                    return false;
                }
            } else if (subcommand.startsWith("-")) {
                try {
                    world.setTime(time - Long.parseLong(subcommand.substring(1)));
                } catch (NumberFormatException ex) {
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
