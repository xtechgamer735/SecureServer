package com.xtechgamer735.SecureServer;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by xtechgamer735 on 06/06/2014.
 */
public class secureserverCommand implements CommandExecutor
{
    Main plugin;

    public secureserverCommand(Main passedPlugin)
    {
        this.plugin = passedPlugin;
    }


    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        if ( (args.length == 1) && (args[0].equalsIgnoreCase("reload")) )
        {
            if (!(sender.hasPermission("secureserver.admin") || sender.isOp()))
            {
                sender.sendMessage(plugin.prefix +  ChatColor.DARK_RED + "Error!" + ChatColor.RED + "No permission.");
                return true;
            }
            plugin.playerDatabase.reloadConfig();
            plugin.reloadConfig();
            sender.sendMessage(plugin.prefix + ChatColor.GREEN + "Config Reloaded.");
            return true;
        }

        if (args.length == 0)
        {
            if (!(sender instanceof Player))
            {
                sender.sendMessage(ChatColor.DARK_RED + "You cannot use this command from console!!");
                return true;
            }
            sender.sendMessage(ChatColor.DARK_GREEN + "--------=[" + ChatColor.BLUE + "Secure Server v" + plugin.getDescription().getVersion() + " by " + ChatColor.GRAY + "xtechgamer" + ChatColor.DARK_RED + "735" + ChatColor.DARK_GREEN + "]=--------");
            sender.sendMessage(ChatColor.RED + "/secureserver" + ChatColor.GOLD + " - " + ChatColor.WHITE + "Shows this help menu.");
            sender.sendMessage(ChatColor.RED + "/login <password>" + ChatColor.GOLD + " - " + ChatColor.WHITE + "Login with the password you have already set");
            sender.sendMessage(ChatColor.RED + "/setpassword <password>" + ChatColor.GOLD + " - " + ChatColor.WHITE + "Sets a password for you to login with.");
            sender.sendMessage(ChatColor.RED + "/changepassword <old password> <new password>" + ChatColor.GOLD + " - " + ChatColor.WHITE + "This will allow you to change your password.");
            sender.sendMessage(ChatColor.RED + "/removepassword <password>" + ChatColor.GOLD + " - " + ChatColor.WHITE + "Removes your password");


            if (sender.hasPermission("secureserver.admin") || sender.isOp())
            {
                sender.sendMessage(ChatColor.RED + "/setroom" + ChatColor.GOLD + " - " + ChatColor.WHITE + "Sets the location of the secure login room");
                sender.sendMessage(ChatColor.RED + "/setout" + ChatColor.GOLD + " - " + ChatColor.WHITE + "Sets the teleport location after login.");
                sender.sendMessage(ChatColor.RED + "/secureserver reload" + ChatColor.GOLD + " - " + ChatColor.WHITE + "Reloads the config.");
            }


            sender.sendMessage(ChatColor.DARK_AQUA + "http://dev.bukkit.org/bukkit-plugins/secure-server/");
            return true;
        }
        sender.sendMessage(plugin.prefix + ChatColor.RED + "/secureserver");



        return true;


    }
}