package com.dinnerbone.bukkit.scrap;

import com.dinnerbone.bukkit.scrap.commands.ClearPluginCommand;
import com.dinnerbone.bukkit.scrap.commands.GivePluginCommand;
import com.dinnerbone.bukkit.scrap.commands.TakePluginCommand;
import com.dinnerbone.bukkit.scrap.commands.TeleportHereCommand;
import com.dinnerbone.bukkit.scrap.commands.TeleportPluginCommand;
import com.dinnerbone.bukkit.scrap.commands.TimePluginCommand;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * Miscellaneous administrative commands
 *
 * @author Dinnerbone
 */
public class ScrapBukkit extends JavaPlugin {
    public void onEnable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );

        getCommand("time").setExecutor(new TimePluginCommand(this));
        getCommand("tp").setExecutor(new TeleportPluginCommand(this));
        getCommand("give").setExecutor(new GivePluginCommand(this));
        getCommand("take").setExecutor(new TakePluginCommand(this));
        getCommand("tphere").setExecutor(new TeleportHereCommand(this));
        getCommand("clear").setExecutor(new ClearPluginCommand(this));
    }

    public boolean teleport(final Player victim, final String destName) {
        Player destination = getServer().getPlayer(destName);
        return teleport(victim, destination);
    }

    public boolean teleport(final String victimName, final Player destination) {
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

    public boolean teleport(final String victimName, final String destName) {
        Player destination = getServer().getPlayer(destName);
        return teleport(victimName, destination);
    }

    public boolean teleport(final Player victim, final Player destination) {
        if ((victim == null) || (destination == null))
            return false;

        victim.teleport(destination);
        return true;
    }

    public boolean teleport(final Player victim, Double x, Double y, Double z) {
        World world = victim != null ? victim.getWorld() : getServer().getWorlds().get(0);
        if (victim == null)
            return false;

        Player player = victim;
        player.teleport(new Location(world, x, y, z));
        player.sendMessage("teleported to x:" + x + " y:" + y + " z:" + z);
        return true;
    }

    public boolean teleport(final String victim, final Player sender, Double x, Double y, Double z) {
        World world = sender != null ? sender.getWorld() : getServer().getWorlds().get(0);
        if (getServer().getPlayer(victim) == null)
            return false;

        Player player = getServer().getPlayer(victim);
        player.teleport(new Location(world, x, y, z));
        if (sender != null) {
            player.sendMessage(sender.getName() + " has teleported you to x:" + x + " y:" + y + " z:" + z);
        }
        return true;
    }

    public boolean anonymousCheck(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Cannot execute that command, I don't know who you are!");
            return true;
        } else {
            return false;
        }
    }

    public Player matchPlayer(String[] split, CommandSender sender) {
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
}