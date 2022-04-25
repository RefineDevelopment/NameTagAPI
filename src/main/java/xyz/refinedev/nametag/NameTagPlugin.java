package xyz.refinedev.nametag;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.refinedev.nametag.adapter.DefaultNameTagProvider;

import java.util.logging.Logger;

/**
 * This Project is property of Refine Development © 2021 - 2022
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 4/25/2022
 * Project: NameTagAPI
 */

public class NameTagPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        NameTagHandler nameTagHandler = new NameTagHandler(this);
        nameTagHandler.registerAdapter(new DefaultNameTagProvider());
        Logger.getGlobal().info("Loaded RefineNameTagAPI as a plugin!");
    }
}
