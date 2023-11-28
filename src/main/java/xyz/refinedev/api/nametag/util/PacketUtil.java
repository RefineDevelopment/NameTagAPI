package xyz.refinedev.api.nametag.util;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import lombok.experimental.UtilityClass;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.api.nametag.packet.ScoreboardPacket;

/**
 * <p>
 * This Project is property of Refine Development.<br>
 * Copyright © 2023, All Rights Reserved.<br>
 * Redistribution of this Project is not allowed.<br>
 * </p>
 *
 * @author Drizzy
 * @version NameTagAPI
 * @since 11/1/2023
 */

@UtilityClass
public class PacketUtil {

    /**
     * Send the given packet to the given player
     *
     * @param target        {@link Player} Target
     * @param packetWrapper {@link ScoreboardPacket} Packet
     */
    public void sendPacket(Player target, ScoreboardPacket packetWrapper) {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.sendServerPacket(target, packetWrapper.getContainer());
    }

    /**
     * Send the given packet to the given player
     *
     * @param target        {@link Player} Target
     * @param packetWrapper {@link PacketWrapper} Packet
     */
    public void sendPacket(Player target, PacketWrapper<?> packetWrapper) {
        PlayerManager protocolManager = PacketEvents.getAPI().getPlayerManager();
        protocolManager.sendPacket(target, packetWrapper);
    }


    /**
     * Send the given packet to all players
     *
     * @param packetWrapper {@link ScoreboardPacket} Packet
     */
    public void broadcast(ScoreboardPacket packetWrapper) {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        for ( Player target : Bukkit.getOnlinePlayers() ) {
            protocolManager.sendServerPacket(target, packetWrapper.getContainer());
        }
    }

    /**
     * Send the given packet to all players
     *
     * @param packetWrapper {@link PacketWrapper} Packet
     */
    public void broadcast(PacketWrapper<?> packetWrapper) {
        PlayerManager playerManager = PacketEvents.getAPI().getPlayerManager();
        for ( Player target : Bukkit.getOnlinePlayers() ) {
            playerManager.sendPacket(target, packetWrapper);
        }
    }
}
