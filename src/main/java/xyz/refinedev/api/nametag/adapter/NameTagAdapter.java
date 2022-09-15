package xyz.refinedev.api.nametag.adapter;

import xyz.refinedev.api.nametag.setup.NameTagInfo;
import xyz.refinedev.api.nametag.util.CC;
import xyz.refinedev.api.nametag.NameTagHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
@AllArgsConstructor
public abstract class NameTagAdapter {

    private final String name;
    private final int weight;

    public abstract NameTagInfo fetchNameTag(Player toRefresh, Player refreshFor);

    public NameTagInfo createNameTag(String prefix, String suffix) {
        return (NameTagHandler.getInstance().getOrCreate(CC.translate(prefix), CC.translate(suffix)));
    }
}