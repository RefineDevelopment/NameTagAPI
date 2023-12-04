package xyz.refinedev.api.nametag;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.refinedev.api.nametag.adapter.NameTagAdapter;
import xyz.refinedev.api.nametag.setup.NameTagTeam;

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
        saveDefaultConfig();

        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));

        this.packetEventsAPI = PacketEvents.getAPI();
        this.packetEventsAPI.getSettings().bStats(false).checkForUpdates(false);

        if (!this.packetEventsAPI.isLoaded()) this.packetEventsAPI.load();
    }

    @Override
    public void onEnable() {
        if (!this.packetEventsAPI.isInitialized()) this.packetEventsAPI.init();

        this.nameTagHandler = new NameTagHandler(this);
        this.nameTagHandler.init(this.packetEventsAPI);
        this.nameTagHandler.registerAdapter(new NameTagAdapter() {
            @Override
            public NameTagTeam fetchNameTag(Player toRefresh, Player refreshFor) {
                return createNameTag(getConfig().getString("NAME-TAGS.PREFIX"), getConfig().getString("NAME-TAGS.SUFFIX"));
            }
        }, 2L);
        this.nameTagHandler.setDebugMode(true);
    }

    @Override
    public void onDisable() {
        this.nameTagHandler.unload();
    }

}
