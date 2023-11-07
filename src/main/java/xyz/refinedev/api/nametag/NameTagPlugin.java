package xyz.refinedev.api.nametag;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
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
    private PacketEventsAPI<?> packetEventsAPI;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));

        this.packetEventsAPI = PacketEvents.getAPI();
        this.packetEventsAPI.getSettings().bStats(false).checkForUpdates(false);

        this.packetEventsAPI.load();
    }

    @Override
    public void onEnable() {
        this.packetEventsAPI.init();

        this.nameTagHandler = new NameTagHandler(this);
        this.nameTagHandler.init(this.packetEventsAPI);
        this.nameTagHandler.registerAdapter(new DefaultNameTagAdapter(), 2L);
    }

    @Override
    public void onDisable() {
        this.nameTagHandler.unload();
    }

}
