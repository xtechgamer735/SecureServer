package com.xtechgamer735.SecureServer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Created by xtechgamer735 on 05/06/2014.
 */
public class Main extends JavaPlugin
{
    String prefix = ChatColor.DARK_GREEN + "[" + ChatColor.BLUE + "SecureServer" + ChatColor.DARK_GREEN + "] ";

    ArrayList<String> online = new ArrayList<String>();

    MyConfigManager manager;
    MyConfig playerDatabase;

    public void onEnable()
    {
        //Metrics
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            // Failed to submit the stats :-(
        }
        //Messages
        getLogger().info(this.getDescription().getName() + " v" + this.getDescription().getVersion() + " has been enabled!");
        //Send to the right place
        Bukkit.getServer().getPluginManager().registerEvents(new Events(this), this);
        this.getCommand("secureserver").setExecutor(new secureserverCommand(this));
        //Copy Config
        this.getConfig().options().copyDefaults(true);
        saveConfig();
        //Config Manager
        manager = new MyConfigManager(this);
        playerDatabase = manager.getNewConfig("playerDatabase.yml", new String[]{
                "SecureServer Player Database",
                "You do NOT need to edit this file."
        });

    }

    public void onDisable()
    {
        getLogger().info(this.getDescription().getName() + " v" + this.getDescription().getVersion() + " has been disabled!");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        Player p = (Player) sender;
        String uuid = p.getPlayer().getUniqueId().toString();


        if (cmd.getLabel().equalsIgnoreCase("setpassword"))
        {
            if (!(sender.hasPermission("secureserver.user") || sender.isOp()))
            {
                sender.sendMessage(prefix +  ChatColor.DARK_RED + "Error!" + ChatColor.RED + "No permission.");
                return true;
            }

            if (args.length < 1) {
                sender.sendMessage(prefix + ChatColor.RED + "/setpassword " + ChatColor.GOLD + "<password>");
                return true;
            }
            if (playerDatabase.contains(uuid)) {
                sender.sendMessage(prefix + ChatColor.RED + "Error! You have already set your password. To change it use" + ChatColor.GOLD + " /changepassword.");
                return true;
            }
            String plaintext = args[0];
            MessageDigest m = null;
            try {
                m = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            m.reset();
            m.update(plaintext.getBytes());
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            String hashtext = bigInt.toString(16);

            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            playerDatabase.set(uuid + ".password", hashtext);
            playerDatabase.saveConfig();
            sender.sendMessage(prefix + ChatColor.GREEN + "Your password has been set successfully! You will need this to log on so do not forget it! If you want to change it use" + ChatColor.GOLD + " /changepassword");
            return true;
        }

        if (cmd.getLabel().equalsIgnoreCase("changepassword"))
        {
            if (!(sender.hasPermission("secureserver.user") || sender.isOp()))
            {
                sender.sendMessage(prefix +  ChatColor.DARK_RED + "Error!" + ChatColor.RED + "No permission.");
                return true;
            }
            if (args.length < 2)
            {
                sender.sendMessage(prefix + ChatColor.RED + "/changepassword " + ChatColor.GOLD + "<old password> <new password>");
                return true;
            }
            if (!online.contains(p.getName()))
            {
                sender.sendMessage(prefix + ChatColor.RED + "Please login first!" + ChatColor.GOLD + " /login");
                return true;
            }

            String plaintext = args[0];
            MessageDigest m = null;
            try {
                m = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            m.reset();
            m.update(plaintext.getBytes());
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            String hashtext = bigInt.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            if (!(playerDatabase.get(uuid + ".password").equals(hashtext)))
            {
                sender.sendMessage(prefix + ChatColor.DARK_RED + "Error!" + ChatColor.RED + "Your current password is incorrect. Please try again.");
                return true;
            }
            String plaintext2 = args[1];
            MessageDigest m2 = null;
            try {
                m2 = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            m2.reset();
            m2.update(plaintext.getBytes());
            byte[] digest2 = m2.digest();
            BigInteger bigInt2 = new BigInteger(1, digest);
            String hashtext2 = bigInt.toString(16);
            while (hashtext2.length() < 32) {
                hashtext2 = "0" + hashtext2;
            }
            if (hashtext == hashtext2)
            {
                sender.sendMessage(prefix = ChatColor.DARK_RED + "Error!" + ChatColor.RED + "You cannot set your new password to your old password.");
                return true;
            }
            playerDatabase.set(uuid + ".password", hashtext2);
            playerDatabase.saveConfig();
            sender.sendMessage(prefix + ChatColor.GREEN + "Your password has been changed successfully! You will need this to log on so do not forget it! If you want to change it again use" + ChatColor.GOLD + " /changepassword");
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("setroom"))
        {
            if (!(sender.hasPermission("secureserver.admin") || sender.isOp()))
            {
                sender.sendMessage(prefix +  ChatColor.DARK_RED + "Error!" + ChatColor.RED + "No permission.");
                return true;
            }

            getConfig().set("room.world", p.getLocation().getWorld().getName());
            getConfig().set("room.x", p.getLocation().getX());
            getConfig().set("room.y", p.getLocation().getY());
            getConfig().set("room.z", p.getLocation().getZ());
            getConfig().set("room.pitch", p.getEyeLocation().getPitch());
            getConfig().set("room.yaw", p.getEyeLocation().getYaw());

            saveConfig();
            p.sendMessage(prefix + ChatColor.GREEN + "The secure room has been set to your current location!");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("setout"))
        {
            if (!(sender.hasPermission("secureserver.admin") || sender.isOp()))
            {
                sender.sendMessage(prefix +  ChatColor.DARK_RED + "Error!" + ChatColor.RED + "No permission.");
                return true;
            }
            getConfig().set("out.world", p.getLocation().getWorld().getName());
            getConfig().set("out.x", p.getLocation().getX());
            getConfig().set("out.y", p.getLocation().getY());
            getConfig().set("out.z", p.getLocation().getZ());
            getConfig().set("out.pitch", p.getEyeLocation().getPitch());
            getConfig().set("out.yaw", p.getEyeLocation().getYaw());

            saveConfig();
            p.sendMessage(prefix + ChatColor.GREEN + "The out location has been set to your current location!");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("login"))
        {
            if (!(sender.hasPermission("secureserver.user") || sender.isOp()))
            {
                sender.sendMessage(prefix +  ChatColor.DARK_RED + "Error!" + ChatColor.RED + "No permission.");
                return true;
            }
            if (online.contains(p.getName()))
            {
                p.sendMessage(prefix + ChatColor.DARK_RED + "Error!" + ChatColor.RED + " You are already logged in.");
                return true;
            }

            if ((args.length < 1) || (args.length > 2))
            {
                sender.sendMessage(prefix + ChatColor.RED + "/login " + ChatColor.GOLD + "<password>");
                return true;
            }

            String plaintext = args[0];
            MessageDigest m = null;
            try {
                m = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            m.reset();
            m.update(plaintext.getBytes());
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            String hashtext = bigInt.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }


            if (playerDatabase.get(uuid + ".password").equals(hashtext))
            {
                if (getConfig().getConfigurationSection("out") == null)
                {
                    p.sendMessage(prefix + ChatColor.DARK_RED + "Error!" + ChatColor.RED + " The out location has not been set!");
                    return true;
                }

                World w = Bukkit.getServer().getWorld(getConfig().getString("out.world"));
                double x = getConfig().getDouble("out.x");
                double y = getConfig().getDouble("out.y");
                double z = getConfig().getDouble("out.z");
                double pitch = getConfig().getDouble("out.pitch");
                double yaw = getConfig().getDouble("out.yaw");

                final Location location = new Location(w, x, y, z, (float)yaw, (float)pitch);

                p.getPlayer().teleport(location);
                online.add(p.getName());

                p.sendMessage(prefix + ChatColor.GREEN + "You have logged in successfully.");
                return true;

            }

            sender.sendMessage(prefix + ChatColor.DARK_RED + "Error!" + ChatColor.RED + " That password is incorrect. Please try again.");
            return true;


        }
        if (cmd.getName().equalsIgnoreCase("removepassword"))
        {
            if (!(sender.hasPermission("secureserver.user") || sender.isOp()))
            {
                sender.sendMessage(prefix +  ChatColor.DARK_RED + "Error!" + ChatColor.RED + "No permission.");
                return true;
            }
            if (!(online.contains(p.getName())))
            {
                p.sendMessage(prefix + ChatColor.DARK_RED + "Error!" + ChatColor.RED + " You must log in to remove your password.");
                return true;
            }

            if ((args.length < 1) || (args.length > 2))
            {
                sender.sendMessage(prefix + ChatColor.RED + "/removepassword " + ChatColor.GOLD + "<password>");
                return true;
            }

            String plaintext = args[0];
            MessageDigest m = null;
            try {
                m = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            m.reset();
            m.update(plaintext.getBytes());
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            String hashtext = bigInt.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }


            if (playerDatabase.get(uuid + ".password").equals(hashtext))
            {
                playerDatabase.removeKey(uuid);
                playerDatabase.saveConfig();
                online.remove(p.getName());
                sender.sendMessage(prefix + "Your password has been removed and you will no longer need to login when you join. You can set a new password at any time.");
                return true;
            }

            sender.sendMessage(prefix + ChatColor.DARK_RED + "ERROR!" + ChatColor.RED + " That password is incorrect. Please try again.");
            return true;


        }

        return true;
    }
}
