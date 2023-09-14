package xyz.refinedev.api.nametag.setup;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * This Project is property of Refine Development © 2021 - 2023
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * @since 9/12/2023
 * @version NameTagAPI
 */
@Getter
public class NameTagUpdate {

    private UUID toRefresh;
    private UUID refreshFor;

    public NameTagUpdate(Player toRefresh) {
        if (toRefresh == null) return;

        this.toRefresh = toRefresh.getUniqueId();
    }

    public NameTagUpdate(Player toRefresh, Player refreshFor) {
        this.toRefresh = toRefresh.getUniqueId();
        this.refreshFor = refreshFor.getUniqueId();
    }
}