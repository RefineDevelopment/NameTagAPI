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

    public static String VERSION;
    public static int MINOR_VERSION;

    public static boolean canHex() {
        return MINOR_VERSION >= 16;
    }

    static {
        try {
            String versionName = Bukkit.getServer().getClass().getPackage().getName();
            VERSION = versionName.length() < 4 ? versionName.split("\\.")[2] : versionName.split("\\.")[3];
            MINOR_VERSION = Integer.parseInt(VERSION.split("_")[1]);
        } catch (Exception e) {
            VERSION = "v" + Bukkit.getServer().getBukkitVersion().replace("-SNAPSHOT", "").replace("-R0.", "_R").replace(".", "_");
            MINOR_VERSION = Integer.parseInt(VERSION.split("_")[1]);
        }
    }
}

