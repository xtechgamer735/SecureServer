package com.xtechgamer735.SecureServer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;

/**
 * Created by xtechgamer735 on 05/06/2014.
 */
public class Events implements Listener
{
    Main plugin;

    //ArrayList<String> online;

    public Events(Main passedPlugin)
    {
        this.plugin = passedPlugin;

    }



    @EventHandler
    public void playerJoin(PlayerJoinEvent e)
    {
        Player player = e.getPlayer();
        String uuid = player.getPlayer().getUniqueId().toString();

        if (plugin.getConfig().contains(uuid))
        {
            World w = Bukkit.getServer().getWorld(plugin.getConfig().getString("room.world"));
            double x = plugin.getConfig().getDouble("room.x");
            double y = plugin.getConfig().getDouble("room.y");
            double z = plugin.getConfig().getDouble("room.z");
            double pitch = plugin.getConfig().getDouble("room.pitch");
            double yaw = plugin.getConfig().getDouble("room.yaw");

            final Location location = new Location(w, x, y, z, (float)yaw, (float)pitch);

            e.getPlayer().teleport(location);
            e.getPlayer().sendMessage("Please login with /login!");



            return;
        }
        return;
    }


    @EventHandler
    public void playerLeave(PlayerQuitEvent e)
    {
        if (plugin.online.contains(e.getPlayer().getName()))
        {
            plugin.online.remove(e.getPlayer().getName());
            return;
        }
        return;
    }
}
