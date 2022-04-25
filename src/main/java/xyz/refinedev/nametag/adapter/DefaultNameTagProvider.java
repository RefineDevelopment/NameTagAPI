package xyz.refinedev.nametag.adapter;

import org.bukkit.ChatColor;
import xyz.refinedev.nametag.setup.NameTagInfo;
import org.bukkit.entity.Player;

public class DefaultNameTagProvider extends NameTagProvider {

    public DefaultNameTagProvider() {
        super("Default Provider", 0);
    }

    @Override
    public NameTagInfo fetchNameTag(Player toRefresh, Player refreshFor) {
        return (createNameTag(ChatColor.GREEN + toRefresh.getDisplayName(), ""));
    }

}
