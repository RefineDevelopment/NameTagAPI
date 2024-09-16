package xyz.refinedev.api.nametag.update.impl;

import lombok.Getter;
import org.bukkit.entity.Player;
import xyz.refinedev.api.nametag.NameTagHandler;
import xyz.refinedev.api.nametag.update.NameTagUpdate;

import java.util.UUID;

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
public class NameTagRefresh implements NameTagUpdate {

    private UUID toRefresh, refreshFor;
    private boolean global;

    public NameTagRefresh(Player refreshFor, boolean global) {
        this.global = global;
        this.refreshFor = refreshFor.getUniqueId();
    }

    public NameTagRefresh(Player toRefresh) {
        if (toRefresh == null) return;

        this.toRefresh = toRefresh.getUniqueId();
    }

    public NameTagRefresh(Player toRefresh, Player refreshFor) {
        this.toRefresh = toRefresh.getUniqueId();
        this.refreshFor = refreshFor.getUniqueId();
    }

    /**
     * Apply this name-tag update.
     */
    public void update(NameTagHandler nameTagHandler) {
        nameTagHandler.applyUpdate(this);
    }

    public int getPriority() {
        return 5;
    }
}