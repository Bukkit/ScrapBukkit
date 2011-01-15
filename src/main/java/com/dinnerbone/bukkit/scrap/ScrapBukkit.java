
package com.dinnerbone.bukkit.scrap;

import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;
import org.bukkit.entity.Player;
import org.bukkit.Server;
import org.bukkit.Location;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
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
    private final ScrapPlayerListener playerListener = new ScrapPlayerListener(this);

    public ScrapBukkit(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
        super(pluginLoader, instance, desc, folder, plugin, cLoader);
    }

    public void onDisable() {
        PluginManager pm = getServer().getPluginManager();
    }

    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_COMMAND, playerListener, Priority.Normal, this);
        
        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
    }

    protected boolean teleport(final Player victim, final String destName) {
        Player destination = getServer().getPlayer(destName);

        if ((victim == null) || (destination == null)) {
            return false;
        } else {
            victim.teleportTo(destination.getLocation());
            return true;
        }
    }

    protected boolean teleport(final String victimName, final Player destination) {
        Player victim = getServer().getPlayer(victimName);

        if ((victim == null) || (destination == null)) {
            return false;
        } else {
            victim.teleportTo(destination.getLocation());
            return true;
        }
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

            if ((victim == null) || (destination == null)) {
                return false;
            } else {
                victim.teleportTo(destination.getLocation());
                return true;
            }
        }
    }

    protected void teleport(final Player victim, final Player destination) {
        victim.teleportTo(destination.getLocation());
    }
    protected void tpHome(final Player player) {
        Scanner scanner;
        try {
            scanner = new Scanner(new FileInputStream("homes.txt"), "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
        String playerName = player.getName();
        Location currentLoc = player.getLocation();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.startsWith(playerName)) {
                String[] rest = line.substring(playerName.length() + 1).split(":");
                Location newLoc = new Location(currentLoc.getWorld(),
                  Double.parseDouble(rest[0]),
                  Double.parseDouble(rest[1]),
                  Double.parseDouble(rest[2]),
                  Float.parseFloat(rest[3]),
                  Float.parseFloat(rest[4])
                );
                player.teleportTo(newLoc);
                return;
            }
        }
    }
}
