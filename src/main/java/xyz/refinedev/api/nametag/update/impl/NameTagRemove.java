package xyz.refinedev.api.nametag.update.impl;

import lombok.RequiredArgsConstructor;
import xyz.refinedev.api.nametag.NameTagHandler;
import xyz.refinedev.api.nametag.update.NameTagUpdate;

import java.util.UUID;

/**
 * <p>
 * This Project is property of Refine Development.<br>
 * Copyright © 2024, All Rights Reserved.<br>
 * Redistribution of this Project is not allowed.<br>
 * </p>
 *
 * @author Drizzy
 * @version NameTagAPI
 * @since 3/19/2024
 */

@RequiredArgsConstructor
public class NameTagRemove implements NameTagUpdate {

    private final UUID toRemove;

    /**
     * Apply this name-tag update.
     */
    public void update(NameTagHandler nameTagHandler) {
        nameTagHandler.getTeamMap().remove(toRemove);
    }
}
