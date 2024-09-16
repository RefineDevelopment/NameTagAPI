package xyz.refinedev.api.nametag.update.impl;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import xyz.refinedev.api.nametag.NameTagHandler;
import xyz.refinedev.api.nametag.update.NameTagUpdate;

/**
 * <p>
 * This Project is property of Refine Development.<br>
 * Copyright © 2024, All Rights Reserved.<br>
 * Redistribution of this Project is not allowed.<br>
 * </p>
 *
 * @author Drizzy
 * @version NameTagAPI
 * @since 9/16/2024
 */

@RequiredArgsConstructor
public class NameTagInitiate implements NameTagUpdate {

    private final Player target;

    /**
     * Apply this name-tag update.
     */
    public void update(NameTagHandler nameTagHandler) {
        nameTagHandler.initiatePlayer(target);
        nameTagHandler.reloadPlayer(target);
        nameTagHandler.reloadOthersFor(target);
    }

    @Override
    public int getPriority() {
        return 69;
    }
}
