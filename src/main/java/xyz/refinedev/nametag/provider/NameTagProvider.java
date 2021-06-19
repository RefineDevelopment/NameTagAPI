package xyz.refinedev.nametag.provider;

import xyz.refinedev.nametag.NameTagHandler;
import xyz.refinedev.nametag.construct.NameTagInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import xyz.refinedev.nametag.util.CC;

@Getter
@AllArgsConstructor
public abstract class NameTagProvider {

    private final String name;
    private final int weight;

    public abstract NameTagInfo fetchNameTag(Player toRefresh, Player refreshFor);

    public static NameTagInfo createNameTag(String prefix, String suffix) {
        return (NameTagHandler.getOrCreate(CC.translate(prefix), CC.translate(suffix)));
    }
}