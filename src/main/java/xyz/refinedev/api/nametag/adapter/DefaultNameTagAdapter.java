package xyz.refinedev.api.nametag.adapter;

import net.megavex.scoreboardlibrary.api.team.ScoreboardTeam;
import xyz.refinedev.api.nametag.setup.NameTagTeam;

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
public class DefaultNameTagAdapter extends NameTagAdapter {

    /**
     * Fetch a Player's NameTag update information
     *
     * @param toRefresh  {@link Player Target} the player getting their nameTag Refreshed
     * @param refreshFor {@link Player Viewer} the player that will be receiving the update
     * @return           {@link ScoreboardTeam} The NameTag Entry used for updates
     */
    public ScoreboardTeam fetchNameTag(Player toRefresh, Player refreshFor) {
        return (createNameTag("&c", "&9 [Refine]"));
    }

}
