package xyz.refinedev.api.nametag;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.refinedev.api.nametag.adapter.NameTagAdapter;
import xyz.refinedev.api.nametag.listener.NameTagListener;
import xyz.refinedev.api.nametag.packet.ScoreboardPacket;
import xyz.refinedev.api.nametag.setup.NameTagInfo;
import xyz.refinedev.api.nametag.thread.NameTagThread;
import xyz.refinedev.api.nametag.setup.NameTagUpdate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This Project is property of Refine Development.
 * Copyright © 2023, All Rights Reserved.
 * Redistribution of this Project is not allowed.
 *
 * @author Drizzy
 * @since 9/12/2023
 * @version NameTagAPI
 */
@Getter @Setter @Log4j2
public class NameTagHandler {

    /**
     * Static instance of this handler
     */
    @Getter private static NameTagHandler instance;

    /**
     * This map stores all the current NameTag info of all targets per viewer.
     * <p>
     *           <center>Viewer -> Target -> {@link NameTagInfo}</center>
     * </p>
     */
    private final Map<UUID, Map<UUID, NameTagInfo>> teamMap = new ConcurrentHashMap<>();
    /**
     * All registered teams are stored here
     */
    private final List<NameTagInfo> registeredTeams = ObjectLists.synchronize(new ObjectArrayList<>());
    /**
     * NameTag Adapter of this instance
     */
    private NameTagAdapter adapter;
    /**
     * This thread handles all the operations surrounding
     * ticking and updating the NameTags
     */
    private NameTagThread thread;
    /**
     * The plugin registering this Tablist Handler
     */
    private final JavaPlugin plugin;

    private static int teamCreateIndex = 1;

    public NameTagHandler(JavaPlugin plugin) {
        instance = this;
        this.plugin = plugin;
    }

    /**
     * Initializing logic of this NameTag Handler
     */
    public void init() {
        Bukkit.getPluginManager().registerEvents(new NameTagListener(this), this.plugin);
    }

    /**
     * Shutdown Logic of this NameTag Handler
     */
    public void unload() {
        this.thread.stopExecuting();
    }

    public void registerAdapter(NameTagAdapter adapter, long ticks) {
        this.adapter = adapter;

        if (ticks < 1L) {
            log.info("[{}] Provided refresh tick rate for NameTag is too low, reverting to 2 ticks!", plugin.getName());
            ticks = 2L;
        }

        this.thread = new NameTagThread(this, ticks);
        this.thread.start();
    }

    /**
     * Refresh the specified target for a specific viewer
     *
     * @param toRefresh  {@link Player} target
     * @param refreshFor {@link Player} viewer
     */
    public void reloadPlayer(Player toRefresh, Player refreshFor) {
        NameTagUpdate update = new NameTagUpdate(toRefresh, refreshFor);
        thread.getUpdatesQueue().add(update);
    }

    /**
     * Refresh the specified target for all viewers
     *
     * @param toRefresh {@link Player} target
     */
    public void reloadPlayer(Player toRefresh) {
        NameTagUpdate update = new NameTagUpdate(toRefresh);
        thread.getUpdatesQueue().add(update);
    }

    /**
     * Refresh the all players for a specified viewer
     *
     * @param refreshFor {@link Player} viewer
     */
    public void reloadOthersFor(Player refreshFor) {
        for (Player toRefresh : Bukkit.getOnlinePlayers()) {
            if (refreshFor == toRefresh) continue;
            this.reloadPlayer(toRefresh, refreshFor);
        }
    }

    /**
     * Apply the {@link NameTagUpdate} according to
     * the specified conditions to the viewer/target
     *
     * @param nameTagUpdate {@link NameTagUpdate}  update
     */
    public void applyUpdate(NameTagUpdate nameTagUpdate) {
        if (nameTagUpdate.getToRefresh() == null) return;

        Player toRefreshPlayer = Bukkit.getPlayer(nameTagUpdate.getToRefresh());

        if (toRefreshPlayer == null) {
            return;
        }

        if (nameTagUpdate.getRefreshFor() == null) {
            Bukkit.getOnlinePlayers().forEach(refreshFor -> this.reloadPlayerInternal(toRefreshPlayer, refreshFor));
        } else {
            Player refreshForPlayer = Bukkit.getPlayer(nameTagUpdate.getRefreshFor());

            if (refreshForPlayer != null) {
                this.reloadPlayerInternal(toRefreshPlayer, refreshForPlayer);
            }
        }
    }

    public void createTeams(Player player) {
        this.adapter.fetchNameTag(player, player);
    }

    public void reloadPlayerInternal(Player toRefresh, Player refreshFor) {
        NameTagInfo provided = this.adapter.fetchNameTag(toRefresh, refreshFor);
        if (provided == null) return;

        Map<UUID, NameTagInfo> teamInfoMap = new HashMap<>();

        if (this.teamMap.containsKey(refreshFor.getUniqueId())) {
            teamInfoMap = this.teamMap.get(refreshFor.getUniqueId());
        }

        ScoreboardPacket packet = new ScoreboardPacket(
                provided.getName(),
                Collections.singletonList(toRefresh.getName()),
                3
        );
        packet.sendToPlayer(refreshFor);

        teamInfoMap.put(toRefresh.getUniqueId(), provided);
        this.teamMap.put(refreshFor.getUniqueId(), teamInfoMap);
    }

    public NameTagInfo getOrCreate(String name, String prefix, String suffix) {
        for (NameTagInfo teamInfo : registeredTeams) {
            if (teamInfo.getName().equalsIgnoreCase(name)) {
                return (teamInfo);
            }
        }

        NameTagInfo newTeam = new NameTagInfo(name, prefix, suffix);
        this.registeredTeams.add(newTeam);

        ScoreboardPacket addPacket = newTeam.getTeamAddPacket();
        Bukkit.getOnlinePlayers().forEach(addPacket::sendToPlayer);

        return (newTeam);
    }

    public void initiatePlayer(Player player) {
        this.registeredTeams.forEach(teamInfo -> teamInfo.getTeamAddPacket().sendToPlayer(player));
    }

    public void unloadPlayer(Player player) {
        this.teamMap.remove(player.getUniqueId());
    }
}