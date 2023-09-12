package xyz.refinedev.api.nametag.adapter;

import org.bukkit.ChatColor;
import xyz.refinedev.api.nametag.setup.NameTagInfo;
import org.bukkit.entity.Player;

public class DefaultNameTagAdapter extends NameTagAdapter {

    /**
     * Fetch a Player's NameTag update information
     *
     * @param toRefresh  {@link Player Target} the player getting their nameTag Refreshed
     * @param refreshFor {@link Player Viewer} the player that will be receiving the update
     * @return           {@link NameTagInfo} The NameTag Entry used for updates
     */
    public NameTagInfo fetchNameTag(Player toRefresh, Player refreshFor) {
        return (createNameTag(ChatColor.GREEN.toString(), ""));
    }

}
