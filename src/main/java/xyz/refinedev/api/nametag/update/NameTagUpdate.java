package xyz.refinedev.api.nametag.update;

import xyz.refinedev.api.nametag.NameTagHandler;

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
public interface NameTagUpdate {

    /**
     * Apply this name-tag update.
     */
    void update(NameTagHandler nameTagHandler);

    int getPriority();

}
