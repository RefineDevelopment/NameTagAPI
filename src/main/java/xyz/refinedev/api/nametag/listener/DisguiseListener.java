package xyz.refinedev.api.nametag.listener;

import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketPlaySendEvent;

import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.api.nametag.NameTagHandler;
import xyz.refinedev.api.nametag.util.ColorUtil;
import xyz.refinedev.api.nametag.util.VersionUtil;

import java.util.EnumSet;

import static com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Server.*;

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

            //TODO: Confirm if this works
        } else if (nameTagHandler.isDebugMode() && VersionUtil.canHex() && (type == PLAYER_INFO || type == PLAYER_INFO_UPDATE)) {
            // Only do this for 1.16+ players because for older players, they're always going to get a chat color
            // and this trick is only to make hex colors work. So not only is it pointless, it will also crash their game lol
            PlayerManager playerManager = nameTagHandler.getPacketEvents().getPlayerManager();
            if (!playerManager.getClientVersion(event.getPlayer()).isNewerThanOrEquals(ClientVersion.V_1_16)) return;

            if (type == PLAYER_INFO) {
                WrapperPlayServerPlayerInfo info = new WrapperPlayServerPlayerInfo(event);
                if (info.getAction() != WrapperPlayServerPlayerInfo.Action.ADD_PLAYER) {
                    return;
                }
                WrapperPlayServerPlayerInfo.PlayerData playerData = info.getPlayerDataList().get(0);
                Player player = Bukkit.getPlayer(playerData.getUser().getUUID());
                if (player == null) return;

                String prefix = nameTagHandler.getAdapter().fetchNameTag(player, (Player) event.getPlayer()).getPrefix();
                ChatColor color = ColorUtil.getLastColors(prefix);

                System.out.println("Setting colors for viewer " + ((Player) event.getPlayer()).getName() + " as target " + player.getName() + " to " + color.toString().replace("§", "&"));

                playerData.setUser(new UserProfile(player.getUniqueId(), color + player.getName()));
            } else {
                WrapperPlayServerPlayerInfoUpdate info = new WrapperPlayServerPlayerInfoUpdate(event);
                WrapperPlayServerPlayerInfoUpdate infoUpdate = new WrapperPlayServerPlayerInfoUpdate(event);
                EnumSet<WrapperPlayServerPlayerInfoUpdate.Action> action = infoUpdate.getActions();
                if (!action.contains(WrapperPlayServerPlayerInfoUpdate.Action.ADD_PLAYER)) return;

                WrapperPlayServerPlayerInfoUpdate.PlayerInfo playerData = info.getEntries().get(0);
                Player player = Bukkit.getPlayer(playerData.getGameProfile().getUUID());
                if (player == null) return;

                String prefix = nameTagHandler.getAdapter().fetchNameTag(player, (Player) event.getPlayer()).getPrefix();
                ChatColor color = ColorUtil.getLastColors(prefix);

                System.out.println("Setting colors for viewer " + ((Player) event.getPlayer()).getName() + " as target " + player.getName() + " to " + color.toString().replace("§", "&"));

                playerData.setGameProfile(new UserProfile(player.getUniqueId(), color + player.getName()));
            }
        }
    }
}
