package xyz.refinedev.api.nametag.util;

import org.bukkit.ChatColor;

public class CC {

    public static String translate(String in) {
        return ChatColor.translateAlternateColorCodes('&', in);
    }

}
