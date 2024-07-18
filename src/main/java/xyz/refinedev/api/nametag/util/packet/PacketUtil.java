package xyz.refinedev.api.nametag.util.packet;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import lombok.experimental.UtilityClass;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.stream.IntStream;

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
     * @param packetWrapper {@link PacketWrapper} Packet
     */
    public void sendPacket(Player target, PacketWrapper<?> packetWrapper) {
        PlayerManager protocolManager = PacketEvents.getAPI().getPlayerManager();
        protocolManager.sendPacket(target, packetWrapper);
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

    public static String MAX_TEAM_NAME_VALUE = "999999";

    public static String convertNumberToTeamName(int number) {
        String textNumber = String.valueOf(number);
        if (number < 10) {
            return textNumber;
        }

        int nines = textNumber.length() - 1;

        StringBuilder builder = new StringBuilder();
        IntStream.range(0, nines).forEach(nine -> builder.append('9'));

        char[] chars = textNumber.toCharArray();
        builder.append(chars[chars.length - 1]);

        return builder.toString();
    }
}
