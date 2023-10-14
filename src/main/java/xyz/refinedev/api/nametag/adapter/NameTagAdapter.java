package xyz.refinedev.api.nametag.adapter;

import org.bukkit.ChatColor;
import xyz.refinedev.api.nametag.setup.NameTagInfo;
import xyz.refinedev.api.nametag.util.CC;
import xyz.refinedev.api.nametag.NameTagHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

/**
 * This Project is property of Refine Development.
 * Copyright © 2023, All Rights Reserved.
 * Redistribution of this Project is not allowed.
 *
 * @author Drizzy
 * @since 9/12/2023
 * @version NameTagAPI
 */
@Getter
@AllArgsConstructor
public abstract class NameTagAdapter {


    /**
     * Fetch a Player's NameTag update information
     *
     * @param toRefresh  {@link Player Target} the player getting their nameTag Refreshed
     * @param refreshFor {@link Player Viewer} the player that will be receiving the update
     * @return           {@link NameTagInfo} The NameTag Entry used for updates
     */
    public abstract NameTagInfo fetchNameTag(Player toRefresh, Player refreshFor);

    /**
     * Create a NameTagInfo from raw prefix and suffix
     *
     * @param prefix {@link String prefix}
     * @param suffix {@link String suffix}
     * @return       {@link NameTagInfo Name Tag info}
     */
    public NameTagInfo createNameTag(String prefix, String suffix) {
        if (prefix.length() > 16) {
            prefix = prefix.substring(0, 16);
        }
        if (suffix.length() > 16) {
            suffix = suffix.substring(0, 16);
        }
        return (NameTagHandler.getInstance().getOrCreate(CC.translate(prefix), CC.translate(suffix)));
    }
}