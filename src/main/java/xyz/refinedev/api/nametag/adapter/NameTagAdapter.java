package xyz.refinedev.api.nametag.adapter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import xyz.refinedev.api.nametag.NameTagHandler;
import xyz.refinedev.api.nametag.setup.NameTagTeam;

/**
 * This Project is property of Refine Development.
 * Copyright © 2023, All Rights Reserved.
 * Redistribution of this Project is not allowed.
 *
 * @author Drizzy
 * @version NameTagAPI
 * @since 9/12/2023
 */
@Getter
@AllArgsConstructor
public abstract class NameTagAdapter {


    /**
     * Fetch a Player's NameTag update information
     *
     * @param toRefresh  {@link Player Target} the player getting their nameTag Refreshed
     * @param refreshFor {@link Player Viewer} the player that will be receiving the update
     * @return {@link NameTagTeam} The NameTag Entry used for updates
     */
    public abstract NameTagTeam fetchNameTag(Player toRefresh, Player refreshFor);

    /**
     * Create a NameTagInfo from raw prefix and suffix
     *
     * @param name   {@link String name}
     * @param prefix {@link String prefix}
     * @param suffix {@link String suffix}
     * @return {@link NameTagTeam Name Tag info}
     */
    public NameTagTeam createNameTag(String name, String prefix, String suffix) {
        return NameTagHandler.getInstance().getOrCreate(name, prefix, suffix);
    }
}