package xyz.refinedev.api.nametag;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.refinedev.api.nametag.adapter.DefaultNameTagAdapter;

/**
 * This Project is property of Refine Development.
 * Copyright © 2023, All Rights Reserved.
 * Redistribution of this Project is not allowed.
 *
 * @author Drizzy
 * @version NameTagAPI
 * @since 9/14/2023
 */
public class NameTagPlugin extends JavaPlugin {

    private NameTagHandler nameTagHandler;

    public void onEnable() {
        this.nameTagHandler = new NameTagHandler(this);
        this.nameTagHandler.init();
        this.nameTagHandler.registerAdapter(new DefaultNameTagAdapter(), 2L);
    }

    public void onDisable() {
        this.nameTagHandler.unload();
    }

}
