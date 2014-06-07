package com.xtechgamer735.SecureServer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by xtechgamer735 on 05/06/2014.
 */
public class Events implements Listener {
    Main plugin;

    //ArrayList<String> online;

    public Events(Main passedPlugin) {
        this.plugin = passedPlugin;

    }


    @EventHandler
    public void playerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        String uuid = player.getPlayer().getUniqueId().toString();

        if (plugin.getConfig().getConfigurationSection("room") == null) {
            e.getPlayer().sendMessage(plugin.prefix + ChatColor.RED + "The secure room has not been set!");
            return;
        }

        if (plugin.playerDatabase.contains(uuid)) {
            World w = Bukkit.getServer().getWorld(plugin.getConfig().getString("room.world"));
            double x = plugin.getConfig().getDouble("room.x");
            double y = plugin.getConfig().getDouble("room.y");
            double z = plugin.getConfig().getDouble("room.z");
            double pitch = plugin.getConfig().getDouble("room.pitch");
            double yaw = plugin.getConfig().getDouble("room.yaw");

            final Location location = new Location(w, x, y, z, (float) yaw, (float) pitch);

            e.getPlayer().teleport(location);
            e.getPlayer().sendMessage("Please login with /login!");


            return;
        }
        return;
    }


    @EventHandler
    public void playerLeave(PlayerQuitEvent e) {
        if (plugin.online.contains(e.getPlayer().getName())) {
            plugin.online.remove(e.getPlayer().getName());
            return;
        }
        return;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e)
    {
        if (plugin.getConfig().get("disableChat").equals(true))
        {
            if (plugin.online.contains(e.getPlayer()))
            {
                return;
            }
            e.setCancelled(true);
            e.getPlayer().sendMessage(plugin.prefix + ChatColor.RED + "Please" + ChatColor.GOLD + " /login " + ChatColor.RED + "to chat!");
        }
        return;

    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent e)
    {
        if (plugin.getConfig().get("disableCommands").equals(true))
        {
            String uuid =  e.getPlayer().getUniqueId().toString();

            if (plugin.online.contains(e.getPlayer()))
            {
                return;
            }

            if (plugin.playerDatabase.contains(uuid))
            {
                String cmd = e.getMessage().toLowerCase(Locale.ENGLISH).split(" ")[0].replace("/", "").toLowerCase(Locale.ENGLISH);

                if (cmd.equals("login") || (cmd.equals("secureserver")) || (cmd.equals("ss") || (cmd.equals("secures"))))
                {
                    return;
                }
                e.getPlayer().sendMessage(plugin.prefix + ChatColor.RED + "Please" + ChatColor.GOLD + " /login " + ChatColor.RED + "to use commands!");
                e.setCancelled(true);
            }
        }

        return;
    }

    @EventHandler
    public void onPlayerBuild(BlockPlaceEvent e)
    {
        if (plugin.getConfig().get("disableBuild").equals(true))
        {
            String uuid =  e.getPlayer().getUniqueId().toString();

            if (plugin.online.contains(e.getPlayer()))
            {
                return;
            }

            if (plugin.playerDatabase.contains(uuid))
            {
                e.getPlayer().sendMessage(plugin.prefix + ChatColor.RED + "Please" + ChatColor.GOLD + " /login " + ChatColor.RED + "build!");
                e.setCancelled(true);
            }
        }

        return;
    }

    @EventHandler
    public void onPlayerBuildDestroy(BlockBreakEvent e)
    {
        if (plugin.getConfig().get("disableBuild").equals(true))
        {
            String uuid =  e.getPlayer().getUniqueId().toString();

            if (plugin.online.contains(e.getPlayer()))
            {
                return;
            }

            if (plugin.playerDatabase.contains(uuid))
            {
                e.getPlayer().sendMessage(plugin.prefix + ChatColor.RED + "Please" + ChatColor.GOLD + " /login " + ChatColor.RED + "build!");
                e.setCancelled(true);
            }
        }

        return;
    }
}
