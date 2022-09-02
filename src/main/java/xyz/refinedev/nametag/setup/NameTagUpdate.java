package xyz.refinedev.nametag.setup;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.UUID;

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