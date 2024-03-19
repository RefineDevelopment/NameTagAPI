package xyz.refinedev.api.nametag.listener;

import lombok.RequiredArgsConstructor;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import xyz.refinedev.api.nametag.NameTagHandler;
import xyz.refinedev.api.nametag.util.VersionUtil;

/**
 * This Project is property of Refine Development.
 * Copyright © 2023, All Rights Reserved.
 * Redistribution of this Project is not allowed.
 *
 * @author Drizzy
 * @since 9/12/2023
 * @version NameTagAPI
 */
@RequiredArgsConstructor
public final class NameTagListener implements Listener {
    private static boolean firstJoin;
    private final NameTagHandler handler;

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Runnable wrapper = () -> {
            if (!NameTagListener.firstJoin) {
                this.handler.createTeams(player);
                NameTagListener.firstJoin = true;
            } else {
                this.handler.initiatePlayer(player);
            }
            this.handler.reloadPlayer(player);
            this.handler.reloadOthersFor(player);
        };

        if (VersionUtil.MINOR_VERSION < 16) {
            wrapper.run();
        } else {
            // PacketEvents or maybe even bukkit is making first join
            // miss the packets, they're getting sent before the player has logged in.
            // So to counter this, we simply send the packets 2 ticks later (which should be enough).
            Bukkit.getScheduler().runTaskLater(this.handler.getPlugin(), wrapper, 20L);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.handler.unloadPlayer(player);
    }
}