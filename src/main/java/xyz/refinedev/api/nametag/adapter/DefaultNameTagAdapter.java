package xyz.refinedev.api.nametag.adapter;

import org.bukkit.ChatColor;
import xyz.refinedev.api.nametag.setup.NameTagTeam;

import org.bukkit.entity.Player;

/**
 * This Project is property of Refine Development.
 * Copyright © 2023, All Rights Reserved.
 * Redistribution of this Project is not allowed.
 *
 * @author Drizzy
 * @version NameTagAPI
 * @since 9/12/2023
 */
public class DefaultNameTagAdapter extends NameTagAdapter {

    /**
     * Fetch a Player's NameTag update information
     *
     * @param toRefresh  {@link Player Target} the player getting their nameTag Refreshed
     * @param refreshFor {@link Player Viewer} the player that will be receiving the update
     * @return           {@link NameTagTeam} The NameTag Entry used for updates
     */
    public NameTagTeam fetchNameTag(Player toRefresh, Player refreshFor) {
        return (createNameTag("refine", ChatColor.RED + "[Refine] " + ChatColor.WHITE, ""));
    }

}
