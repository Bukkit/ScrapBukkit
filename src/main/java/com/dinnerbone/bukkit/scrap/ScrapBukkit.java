
package com.dinnerbone.bukkit.scrap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Miscellaneous administrative commands
 *
 * @author Dinnerbone
 * @author sk89q
 */
public class ScrapBukkit extends JavaPlugin {

    private static Logger logger = Logger.getLogger("Minecraft.ScrapBukkit");
    
    private Map<String, String> messageTargets = new HashMap<String, String>();
    
    public void onDisable() {
        //PluginManager pm = getServer().getPluginManager();
        
        messageTargets.clear();
    }

    public void onEnable() {       
        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
        
        PlayerListener playerListener = new ScrapBukkitPlayerListener(this);
        
        registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Monitor);
    }
    
    public void forgetPlayer(Player player) {
        messageTargets.remove(player.getName());
    }
    
    private void registerEvent(Event.Type type, Listener listener, Priority priority) {
        getServer().getPluginManager().registerEvent(type, listener, priority, this);
    }

    private String getCardinalDirection(Player player) {
        double rot = (player.getLocation().getYaw() - 90) % 360;
        if (rot < 0) {
            rot += 360.0;
        }
        return getDirection(rot);
    }

    private String getDirection(double rot) {
        if (0 <= rot && rot < 22.5) {
            return "North";
        } else if (22.5 <= rot && rot < 67.5) {
            return "Northeast";
        } else if (67.5 <= rot && rot < 112.5) {
            return "East";
        } else if (112.5 <= rot && rot < 157.5) {
            return "Southeast";
        } else if (157.5 <= rot && rot < 202.5) {
            return "South";
        } else if (202.5 <= rot && rot < 247.5) {
            return "Southwest";
        } else if (247.5 <= rot && rot < 292.5) {
            return "West";
        } else if (292.5 <= rot && rot < 337.5) {
            return "Northwest";
        } else if (337.5 <= rot && rot < 360.0) {
            return "North";
        } else {
            return null;
        }
    }

    protected boolean teleport(final Player victim, final String destName) {
        Player destination = getServer().getPlayer(destName);
        return teleport(victim, destination);
    }

    protected boolean teleport(final String victimName, final Player destination) {
        Player victim = getServer().getPlayer(victimName);
        return teleport(victim, destination);
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
            return teleport(victim, destination);
        }
    }

    protected boolean teleport(final Player victim, final Player destination) {
        if ((victim == null) || (destination == null)) 
            return false;
        
        victim.teleportTo(destination.getLocation());
        return true;
    }

    protected boolean teleport(final Player victim, Double x, Double y, Double z) {
        World world = victim instanceof Player ? ((Player) victim).getWorld() : getServer().getWorlds().get(0);
        if (victim == null)
            return false;

        Player player = victim;
        player.teleportTo(new Location(world, x, y, z));
        player.sendMessage("teleported to x:" + x + " y:" + y + " z:" + z);
        return true;
    }

    protected boolean teleport(final String victim, final Player sender, Double x, Double y, Double z) {
        World world = sender instanceof Player ? ((Player) sender).getWorld() : getServer().getWorlds().get(0);
        if (getServer().getPlayer(victim) == null)
            return false;

        Player player = getServer().getPlayer(victim);
        player.teleportTo(new Location(world, x, y, z));
        player.sendMessage(sender.getName() + " has teleported you to x:" + x + " y:" + y + " z:" + z);
        return true;
    }
    
    protected boolean sendWhisper(CommandSender sender, String to, String message) {
        Player player = null;
        String fromName = sender instanceof Player ? ((Player) sender).getName() : "Console";
        String toName;

        if (to.equalsIgnoreCase("Console")) {
            toName = "Console";
            logger.info("(From " + fromName + "): " + message);
        } else {
            player = matchPlayer(to, sender);
            if (player == null) {
                return false;
            }

            toName = player.getName();
            player.sendMessage(ChatColor.GRAY + "(From " + fromName + "): " + ChatColor.WHITE + message);
        }
        
        sender.sendMessage(ChatColor.GRAY + "(To " + toName + "): " + ChatColor.WHITE + message);
        
        messageTargets.put(fromName, toName);
        
        // sk: This makes it easier to reply, but we're not going to
        // override an existing entry so that someone doesn't accidentally 
        // reply to someone that they don't want to reply to (due to timing)
        if (!messageTargets.containsKey(toName)) {
            messageTargets.put(toName, fromName);
        }
        
        return true;
    }

    private boolean anonymousCheck(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Cannot execute that command, I don't know who you are!");
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        String[] trimmedArgs = args;
        String commandName = command.getName().toLowerCase();

        try {
            if (commandName.equals("tp")) {
                return performTeleport(sender, trimmedArgs);
            } else if (commandName.equals("clear")) {
                return performInventoryClean(sender, trimmedArgs);
            } else if (commandName.equals("msg")) {
                return performMessage(sender, trimmedArgs);
            } else if (commandName.equals("reply")) {
                return performReply(sender, trimmedArgs);
            } else if (commandName.equals("take")) {
                return performTake(sender, trimmedArgs);
            } else if (commandName.equals("give")) {
                return performGive(sender, trimmedArgs);
            } else if (commandName.equals("tphere")) {
                return performTPHere(sender, trimmedArgs);
            } else if (commandName.equals("time")) {
                return performTimeCheck(sender, trimmedArgs);
            } else if (commandName.equals("where")) {
                return performPosition(sender, trimmedArgs);
            } else if (commandName.equals("compass")) {
                return performCompass(sender, trimmedArgs);
            }
        } catch (PermissionsCommandException e) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to do that");
            return true;
        } catch (CommandException e) {
            return false;
        }
        return false;
    }

    private boolean performGive(CommandSender sender, String[] split) throws CommandException {
        if ((split.length > 3) || (split.length == 0)) {
            return false;
        }

        checkPermissions(sender, "scrapbukkit.give");

        Player player = null;
        Material material = null;
        int count = 1;
        String[] gData = null;
        Byte bytedata = null;
        if (split.length >= 1) {
            gData = split[0].split(":");
            material = Material.matchMaterial(gData[0]);
            if (gData.length == 2) {
                bytedata = Byte.valueOf(gData[1]);
            }
        }
        if (split.length >= 2) {
            
            try {
                count = Integer.parseInt(split[1]);
            } catch (NumberFormatException ex) {
                sender.sendMessage(ChatColor.RED + "'" + split[1] + "' is not a number!");
                return true;
            }
        }
        if (split.length == 3) {
            checkPermissions(sender, "scrapbukkit.give.other");
            
            player = getServer().getPlayer(split[2]);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "'" + split[2] + "' is not a valid player!");
                return true;
            }
        } else {
            if (anonymousCheck(sender)) {
                return true;
            } else {
                player = (Player) sender;
            }
        }
        if (material == null) {
            sender.sendMessage(ChatColor.RED + "Unknown item");
            return true;
        }
        if (bytedata != null) {
            player.getInventory().addItem(new ItemStack(material, count, (short) 0, bytedata));
        } else {
            player.getInventory().addItem(new ItemStack(material, count));
        }
        sender.sendMessage("Given " + player.getDisplayName() + " " + count + " " + material.toString());
        return true;
    }

    private boolean performMessage(CommandSender sender, String[] split) throws CommandException {
        if (split.length < 2) {
            return false;
        }

        checkPermissions(sender, "scrapbukkit.msg");

        String message = StringUtil.joinString(split, " ", 1);
        
        return sendWhisper(sender, split[0], message);
    }

    private boolean performReply(CommandSender sender, String[] split) throws CommandException {
        if (split.length < 1) {
            return false;
        }

        String fromName = sender instanceof Player ? ((Player) sender).getName() : "Console";
        String message = StringUtil.joinString(split, " ");
        
        if (messageTargets.containsKey(fromName)) {
            return sendWhisper(sender, messageTargets.get(fromName), message);
        } else {
            sender.sendMessage("You haven't messaged anyone!");
            return true;
        }
    }

    private boolean performTake(CommandSender sender, String[] split) throws CommandException {
        if ((split.length > 3) || (split.length == 0)) {
            return false;
        }

        checkPermissions(sender, "scrapbukkit.take");

        Player player = null;
        Material material = null;
        int count = -1;

        if (split.length >= 2) {
            checkPermissions(sender, "scrapbukkit.take.other");
            
            player = matchPlayer(split, sender);
            if (player == null) {
                sender.sendMessage("Didn't find such a player!");
                return true;
            }
            material = Material.matchMaterial(split[1]);
        } else {
            if (anonymousCheck(sender)) return true;
            player = (Player)sender;
            material = Material.matchMaterial(split[0]);
        }

        if (split.length >= 3) {
            try {
                count = Integer.parseInt(split[2]);
            } catch (NumberFormatException ex) {
                sender.sendMessage(ChatColor.RED + "'" + split[2] + "' is not a number!");
                return true;
            }
        }

        if (material == null) {
            sender.sendMessage(ChatColor.RED + "Unknown item");
            return true;
        }

        if (count < 0) {
            player.getInventory().remove(material);
        } else {
            player.getInventory().remove(new ItemStack(material, count));
        }
        sender.sendMessage("Took " + count + " " + material.toString() + " from " + player.getDisplayName());
        return true;
    }

    private boolean performInventoryClean(CommandSender sender, String[] split) throws CommandException {
        checkPermissions(sender, "scrapbukkit.clean");
        
        if (split.length > 1) {
            return false;
        }

        Player player = null;

        if (split.length == 1) {
            checkPermissions(sender, "scrapbukkit.clean.other");
            
            player = matchPlayer(split, sender);
            if (player == null) {
                sender.sendMessage("Didn't find such a player!");
                return true;
            }
        } else if (anonymousCheck(sender)) {
            return true;
        } else {
            player = (Player)sender;
        }

        sender.sendMessage("Cleared inventory of " + player.getDisplayName());
        player.getInventory().clear();
        return true;
    }

    private boolean performTeleport(CommandSender sender, String[] split) throws CommandException {
        checkPermissions(sender, "scrapbukkit.teleport");
        
        if (split.length == 1) {
            if (anonymousCheck(sender)) return true;
            Player player = (Player)sender;
            String dest = split[0];
            
            if (dest.equalsIgnoreCase("*")) {
                sender.sendMessage(ChatColor.RED + "Incorrect usage of wildchar *");
                return true;
            } else {
                if (!teleport(player, dest)) {
                    sender.sendMessage(ChatColor.RED + "Could not teleport to " + dest
                            + " (Is the name spelt correctly?)");
                    return true;
                }
            }
            return true;
        } else if (split.length == 2) {
            checkPermissions(sender, "scrapbukkit.teleport.other");
            
            String victim = split[0];
            String dest = split[1];

            if (dest.equalsIgnoreCase("*")) {
                sender.sendMessage(ChatColor.RED + "Incorrect usage of wildchar *");
                return true;
            } else {
                if (victim.equalsIgnoreCase("*")) {
                    checkPermissions(sender, "scrapbukkit.teleport.wildcard");
                }
                if (!teleport(victim, dest)) {
                    sender.sendMessage(ChatColor.RED + "Could not teleport "
                            + victim + " to " + dest + " (Are the names spelt correctly?)");
                    return true;
                }
            }
            return true;
        } else if (split.length == 3) {
            checkPermissions(sender, "scrapbukkit.teleport.coords");
            
            Player player = null;
            if (anonymousCheck(sender)) return true;

            player = (Player) sender;
            Double tx = null;
            Double ty = null;
            Double tz = null;
            try {
                tx = Double.valueOf(split[0]);
                ty = Double.valueOf(split[1]);
                tz = Double.valueOf(split[2]);
            } catch (NumberFormatException ex) {
                sender.sendMessage(ChatColor.RED + "Not valid coordinates!");
                return true;
            }
            teleport(player, tx, ty, tz);
            return true;
        } else if (split.length == 4) {
            checkPermissions(sender, "scrapbukkit.teleport.other");
            checkPermissions(sender, "scrapbukkit.teleport.coords");
            
            Player player = null;
            if (anonymousCheck(sender)) return true;

            player = (Player) sender;
            Double tx = null;
            Double ty = null;
            Double tz = null;
            try {
                tx = Double.valueOf(split[1]);
                ty = Double.valueOf(split[2]);
                tz = Double.valueOf(split[3]);
            } catch (NumberFormatException ex) {
                sender.sendMessage(ChatColor.RED + "Not valid coordinates!");
                return true;
            }
            teleport(split[0], player, tx, ty, tz);
            return true;
        }
        return false;
    }
    
    private boolean performTPHere(CommandSender sender, String[] split) throws CommandException {
        checkPermissions(sender, "scrapbukkit.teleport.other");
        
        if ((split.length == 1)) {
            if (!anonymousCheck(sender)) {
                return true;
            }
            String victim = split[0];

            if (teleport(victim, (Player)sender)) {
                sender.sendMessage("Done.");
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "Could not teleport " + victim + " to you (Is the name spelt correctly?)");
                return true;
            }
        } else {
            return false;
        }
    }

    private boolean performTimeCheck(CommandSender sender, String[] split) throws CommandException {
        World world = sender instanceof Player ? ((Player)sender).getWorld() : getServer().getWorlds().get(0);
        long time = world.getTime();
        
        if (split.length == 0) {
            int hours = (int)((time / 1000+8) % 24);
            int minutes = (int) (60 * (time % 1000) / 1000);
            sender.sendMessage(String.format( "Time: %02d:%02d", hours, minutes));
            return true;
        } else if (split.length == 1) {
            checkPermissions(sender, "scrapbukkit.time");

            String timeStr = split[0];
            if (timeStr.equalsIgnoreCase("help")) {
                // Gets handled later.
            } else if (timeStr.equalsIgnoreCase("raw")) {
                sender.sendMessage("Raw: " + world.getFullTime());
            } else if (timeStr.equalsIgnoreCase("day")) {
                world.setTime(0);
            } else if (timeStr.equalsIgnoreCase("night")) {
                world.setTime(13000);
            } else if (timeStr.startsWith("=")) {
                try {
                    world.setTime(Long.parseLong(timeStr.substring(1)));
                } catch(NumberFormatException ex) {
                    sender.sendMessage("That is not a number");
                    return true;
                }
            } else if (timeStr.startsWith("+")) {
                try {
                    world.setTime(time + Long.parseLong(timeStr.substring(1)));
                } catch(NumberFormatException ex) {
                    sender.sendMessage("That is not a number");
                    return true;
                }
            } else if (timeStr.startsWith("-")) {
                try {
                    world.setTime(time-Long.parseLong(timeStr.substring(1)));
                } catch(NumberFormatException ex) {
                    sender.sendMessage("That is not a number");
                    return true;
                }
            } else {
                return false;
            }

            sender.sendMessage("Done.");
            return true;
        }
        return false;
    }

    private boolean performPosition(CommandSender sender, String[] split) throws CommandException {
        if (anonymousCheck(sender)) {
            return true;
        }
        
        checkPermissions(sender, "scrapbukkit.where");
        
        Player player = (Player)sender;     
        
        if (split.length == 0) {
            Location loc = player.getLocation();
            sender.sendMessage(String.format("Your location: %.2f, %.2f, %.2f",
                    loc.getX(), loc.getY(), loc.getZ()));
            return true;
        }
        return false;
    }

    private boolean performCompass(CommandSender sender, String[] split) throws CommandException {
        if (anonymousCheck(sender)) {
            return true;
        }
        
        checkPermissions(sender, "scrapbukkit.compass");
        
        Player player = (Player)sender;     
        
        if (split.length == 0) {
            Location loc = player.getLocation();
            sender.sendMessage("Your direction: " + getCardinalDirection(player));
            return true;
        }
        return false;
    }

    protected Player matchPlayer(String[] split, CommandSender sender) {
        return matchPlayer(split[0], sender);
    }

    protected Player matchPlayer(String name, CommandSender sender) {
        Player player = matchPlayerSilent(name, sender);
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Didn't find a player named by '" + name + "'");
        }
        return player;
    }

    protected Player matchPlayerSilent(String name, CommandSender sender) {
        Player player;
        List<Player> players = getServer().matchPlayer(name);
        if (players.isEmpty()) {
            player = null;
        } else {
            player = players.get(0);
        }
        return player;
    }
    
    protected void checkPermissions(CommandSender sender, String permission) throws PermissionsCommandException {
        if (!sender.isOp()) {
            throw new PermissionsCommandException();
        }
    }
}