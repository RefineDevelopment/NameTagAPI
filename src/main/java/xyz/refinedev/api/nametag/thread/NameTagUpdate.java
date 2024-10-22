package xyz.refinedev.api.nametag.thread;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
 * @since 10/22/2024
 */

@Getter
@RequiredArgsConstructor
public class NameTagUpdate {

    private final UUID toRefresh;
    private final UUID refreshFor;
    private final boolean global;

}
