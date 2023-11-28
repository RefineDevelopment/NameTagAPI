package xyz.refinedev.api.nametag.listener;

import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketPlaySendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;

import org.bukkit.entity.Player;

import xyz.refinedev.api.nametag.NameTagHandler;

import static com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Server.RESPAWN;

/**
 * <p>
 * This Project is property of Refine Development.<br>
 * Copyright © 2023, All Rights Reserved.<br>
 * Redistribution of this Project is not allowed.<br>
 * </p>
 *
 * @author Drizzy
 * @version NameTagAPI
 * @since 10/26/2023
 */
public class DisguiseListener extends SimplePacketListenerAbstract {

    private final NameTagHandler nameTagHandler = NameTagHandler.getInstance();

    public void onPacketPlaySend(PacketPlaySendEvent event) {
        PacketType.Play.Server type = event.getPacketType();
        if (type == RESPAWN) {
            Player player = (Player) event.getPlayer();
            nameTagHandler.reloadPlayer(player);
        }
    }
}
