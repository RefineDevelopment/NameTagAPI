package xyz.refinedev.api.nametag.util;

import org.bukkit.Bukkit;

/**
 * This Project is property of Refine Development.
 * Copyright © 2023, All Rights Reserved.
 * Redistribution of this Project is not allowed.
 *
 * @author Drizzy
 * @version NameTagAPI
 * @since 9/14/2023
 */
public class VersionUtil {

    public static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    public static final int MINOR_VERSION = Integer.parseInt(VERSION.split("_")[1]);

    public static boolean canHex() {
        return MINOR_VERSION >= 16;
    }
}
