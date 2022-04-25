package xyz.refinedev.nametag.adapter;

import xyz.refinedev.nametag.NameTagHandler;
import xyz.refinedev.nametag.setup.NameTagInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import xyz.refinedev.nametag.util.CC;

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