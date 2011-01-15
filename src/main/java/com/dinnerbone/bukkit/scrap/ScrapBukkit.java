
package com.dinnerbone.bukkit.scrap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.Scanner;
import org.bukkit.entity.Player;
import org.bukkit.Server;
import org.bukkit.Location;
import org.bukkit.World;
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
        Location newLoc = null;
        World world = currentLoc.getWorld();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.startsWith(playerName + ":")) {
                String[] rest = line.substring(playerName.length() + 1).split(":");
                newLoc = new Location(world,
                  Double.parseDouble(rest[0]),
                  Double.parseDouble(rest[1]),
                  Double.parseDouble(rest[2]),
                  Float.parseFloat(rest[3]),
                  Float.parseFloat(rest[4])
                );
                break;
            }
        }
        if (null == newLoc) {
            newLoc = world.getSpawnLocation();
        }
        player.teleportTo (newLoc);
    }
    protected void setAsHome(final Player player) {
        String playerName = player.getName();
        Location currentLoc = player.getLocation();
        Scanner readscan;
        String newLine = playerName
            + ":" + currentLoc.getX()
            + ":" + currentLoc.getY()
            + ":" + currentLoc.getZ()
            + ":" + currentLoc.getYaw()
            + ":" + currentLoc.getPitch();
        File dest = null; BufferedWriter writestream;
        try {
            readscan = new Scanner(new FileInputStream("homes.txt"), "UTF-8");
            dest = new File(".homes.txt");
            writestream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dest)));
            boolean updated = false;
            while (readscan.hasNextLine()) {
                String line = readscan.nextLine();
                if (line.startsWith(playerName + ":")) {
                    writestream.write(newLine);
                    updated = true;
                } else {
                    writestream.write(line);
                }
                writestream.newLine();
            }
            if (!updated) {
                writestream.write(newLine);
                writestream.newLine();
            }
            writestream.close();
            dest.renameTo(new File("homes.txt")); // overwrite
        } catch (Exception ex) {
            if (null != dest) dest.delete();
            ex.printStackTrace();
            return;
        }
    }
}
