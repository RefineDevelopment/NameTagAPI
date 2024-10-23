package xyz.refinedev.api.nametag;

import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.manager.server.ServerManager;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.refinedev.api.nametag.adapter.DefaultNameTagAdapter;
import xyz.refinedev.api.nametag.adapter.NameTagAdapter;
import xyz.refinedev.api.nametag.listener.DisguiseListener;
import xyz.refinedev.api.nametag.listener.GlitchFixListener;
import xyz.refinedev.api.nametag.listener.NameTagListener;
import xyz.refinedev.api.nametag.setup.NameTagTeam;
import xyz.refinedev.api.nametag.thread.NameTagThread;
import xyz.refinedev.api.nametag.thread.NameTagUpdate;
import xyz.refinedev.api.nametag.util.chat.ColorUtil;
import xyz.refinedev.api.nametag.util.packet.PacketUtil;
import xyz.refinedev.api.nametag.util.VersionUtil;

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

    private static int TEAM_INDEX = 0;

    /**
     * Static instance of this handler
     */
    @Getter private static NameTagHandler instance;

    /**
     * This map stores all the current NameTag info of all targets per viewer.
     * <p>
     *           <center>Viewer -> Target -> {@link String}</center>
     * </p>
     */
    private final Map<UUID, Map<UUID, String>> teamMap = new ConcurrentHashMap<>();
    /**
     * All registered teams are stored here
     */
    private final Map<String, NameTagTeam> teamCache = new ConcurrentHashMap<>();
    private static final String SEPARATOR = "::"; // Separator for prefix-suffix keys
    /**
     * The plugin registering this NameTag Handler
     */
    private final JavaPlugin plugin;
    /**
     * NameTag Adapter of this instance
     */
    private NameTagAdapter adapter;
    private NameTagThread nameTagThread;
    private PacketEventsAPI<?> packetEvents;
    private boolean collisionEnabled, debugMode, initialized;

    public NameTagHandler(JavaPlugin plugin) {
        instance = this;
        this.plugin = plugin;
    }

    public void init(PacketEventsAPI<?> packetEvents, boolean registerListeners) {
        this.adapter = new DefaultNameTagAdapter();
        this.packetEvents = packetEvents;
        this.nameTagThread = new NameTagThread(this, 20L);

        packetEvents.getEventManager().registerListener(new DisguiseListener(this));

        if (registerListeners) {
            Bukkit.getPluginManager().registerEvents(new NameTagListener(this), this.plugin);
        }

        try {
            Class.forName("xyz.refinedev.api.tablist.util.GlitchFixEvent");
            Bukkit.getPluginManager().registerEvents(new GlitchFixListener(this), this.plugin);
        } catch (Exception e) {
            //
        }

        this.initialized = true;
    }

    /**
     * Shutdown Logic of this NameTag Handler
     */
    public void unload() {
        if (!this.initialized) return;

        this.nameTagThread.stopExecuting();
        this.teamMap.clear();

        for ( Player player : Bukkit.getOnlinePlayers() ) {
            for ( NameTagTeam team : this.teamCache.values() ) {
                team.destroyFor(player);
            }
        }

        this.teamCache.clear();
    }

    public void registerAdapter(NameTagAdapter adapter) {
        this.adapter = adapter == null ? new DefaultNameTagAdapter() : adapter;
    }

    /**
     * Send all {@link NameTagTeam} teams to this player
     *
     * @param player {@link Player} Target
     */
    public void initiatePlayer(Player player) {
        if (!this.initialized) return;

        if (this.teamCache.isEmpty()) {
            this.adapter.fetchNameTag(player, player);
            return;
        }

        for ( NameTagTeam teamInfo : this.teamCache.values() ) {
            PacketUtil.sendPacket(player, teamInfo.getCreatePacket());
        }
    }

    /**
     * Unload the player's cached data
     *
     * @param player {@link Player} Target
     */
    public void unloadPlayer(Player player) {
        if (!this.initialized) return;

        for ( NameTagTeam team : this.teamCache.values() ) {
            team.destroyFor(player);
        }
        this.teamMap.remove(player.getUniqueId());
    }

    /**
     * Refresh the specified target for a specific viewer
     *
     * @param toRefresh  {@link Player} target
     * @param refreshFor {@link Player} viewer
     */
    public void reloadPlayer(Player toRefresh, Player refreshFor) {
        if (!this.initialized) return;

        if (!Bukkit.isPrimaryThread()) {
            this.applyUpdate(toRefresh, refreshFor, false);
            return;
        }

        this.nameTagThread.addUpdate(new NameTagUpdate(toRefresh.getUniqueId(), refreshFor.getUniqueId(), false));
    }

    /**
     * Refresh the specified target for all viewers
     *
     * @param toRefresh {@link Player} target
     */
    public void reloadPlayer(Player toRefresh) {
        if (!this.initialized) return;

        if (!Bukkit.isPrimaryThread()) {
            this.applyUpdate(null, toRefresh, true);
            return;
        }

        this.nameTagThread.addUpdate(new NameTagUpdate(null, toRefresh.getUniqueId(), true));
    }

    /**
     * Refresh the all players for a specified viewer
     *
     * @param refreshFor {@link Player} viewer
     */
    public void reloadOthersFor(Player refreshFor) {
        if (!this.initialized) return;

        if (!Bukkit.isPrimaryThread()) {
            this.applyUpdate(refreshFor, null, true);
            return;
        }

        this.nameTagThread.addUpdate(new NameTagUpdate(refreshFor.getUniqueId(), null, true));
    }

    /**
     * Apply a nametag update according to
     * the specified conditions to the viewer/target
     *
     * @param refreshFor {@link UUID} Viewer
     * @param toRefresh {@link UUID} Target
     * @param global {@link Boolean} Viewers are all players?
     */
    public void applyUpdate(UUID refreshFor, UUID toRefresh, boolean global) {
        Player viewerPlayer = Bukkit.getPlayer(refreshFor);
        Player targetPlayer = Bukkit.getPlayer(toRefresh);
        this.applyUpdate(viewerPlayer, targetPlayer, global);
    }

    /**
     * Apply a nametag update according to
     * the specified conditions to the viewer/target
     *
     * @param refreshFor {@link UUID} Viewer
     * @param toRefresh {@link UUID} Target
     * @param global {@link Boolean} Viewers are all players?
     */
    public void applyUpdate(Player refreshFor, Player toRefresh, boolean global) {
        if (!this.initialized) return;

        if (global) {
            if (refreshFor == null) {
                for ( Player player : Bukkit.getOnlinePlayers() ) {
                    this.reloadPlayerInternal(toRefresh, player);
                }
            } else if (toRefresh == null) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    this.reloadPlayerInternal(player, refreshFor);
                }
            }
            return;
        }

        if (toRefresh == null) {
            return;
        }

        this.reloadPlayerInternal(toRefresh, refreshFor);
    }

    public void reloadPlayerInternal(Player toRefresh, Player refreshFor) {
        if (!this.initialized) return;

        NameTagTeam provided = this.adapter.fetchNameTag(toRefresh, refreshFor);
        if (provided == null) return;

        Map<UUID, String> teamInfoMap = this.teamMap.computeIfAbsent(refreshFor.getUniqueId(), (t) -> new HashMap<>());

        // Don't spam repeat the same NameTag to the client
        // Netty wakeup calls are expensive!!
        String previousName = teamInfoMap.get(toRefresh.getUniqueId());
        NameTagTeam previous = previousName == null ? null : this.getByName(previousName);
        if (provided.equals(previous)) {
            return;
        }

        WrapperPlayServerTeams packet = new WrapperPlayServerTeams(ColorUtil.color(provided.getName()), WrapperPlayServerTeams.TeamMode.ADD_ENTITIES, (WrapperPlayServerTeams.ScoreBoardTeamInfo) null, toRefresh.getName());
        PacketUtil.sendPacket(refreshFor, packet);

        // In 1.16, the issue arises that hex color does not apply to the name of the player.
        // This is due to the ScoreboardTeam color being applied to the name, which is a plain enum with normal colors.
        // It does not support Hex Colors, for Teams, I get the nearest ChatColor to the hex color but for displaying in tablist,
        // we can just use display name packet which will apply HexColor.
        if (VersionUtil.canHex()) {
            ServerManager manager = this.packetEvents.getServerManager();

            UserProfile profile = new UserProfile(toRefresh.getUniqueId(), toRefresh.getName());
            String text = ColorUtil.color(provided.getPrefix() + toRefresh.getName() + provided.getSuffix());
            Component displayName = Component.text(text);
            PacketWrapper<?> display;
            if (manager.getVersion().isNewerThanOrEquals(ServerVersion.V_1_19_3)) {
                WrapperPlayServerPlayerInfoUpdate.PlayerInfo data = new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(
                        profile,
                        true,
                        0,
                        null,
                        displayName,
                        null
                );
                display = new WrapperPlayServerPlayerInfoUpdate(WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_DISPLAY_NAME, data);
            } else {
                WrapperPlayServerPlayerInfo.PlayerData data = new WrapperPlayServerPlayerInfo.PlayerData(
                        displayName,
                        profile,
                        null,
                        0
                );
                display = new WrapperPlayServerPlayerInfo(WrapperPlayServerPlayerInfo.Action.UPDATE_DISPLAY_NAME, data);
            }

            PacketUtil.sendPacket(refreshFor, display);
        }

        // Update and store the new team for this target according to the viewer
        teamInfoMap.put(toRefresh.getUniqueId(), provided.getName());
        this.teamMap.put(refreshFor.getUniqueId(), teamInfoMap);
    }

    /**
     * Get a {@link NameTagTeam} associated with the given prefix and suffix.
     * If we don't have one for these prefixes and suffixes, then we make one and send it to everyone.
     *
     * @param prefix {@link String} Raw Prefix
     * @param suffix {@link String} Raw Suffix
     * @return       {@link NameTagTeam} Associated Team
     */
    public NameTagTeam getOrCreate(String prefix, String suffix) {
        if (debugMode) {
            log.info("[NameTagAPI-Debug] Trying to fetch a team with prefix {} and suffix {}", ColorUtil.getRaw(prefix), ColorUtil.getRaw(suffix));
        }

        // Create the unique key for caching
        String key = prefix + SEPARATOR + suffix;

        // Attempt to get the team from the cache
        NameTagTeam team = teamCache.get(key);
        if (team != null) {
            return team;
        }

        // Team doesn't exist; create a new one
        TEAM_INDEX++;
        NameTagTeam newTeam = new NameTagTeam("boltNT" + TEAM_INDEX, prefix, suffix, collisionEnabled);

        // Cache the newly created team
        teamCache.put(key, newTeam);

        if (debugMode) {
            log.info("[NameTagAPI-Debug] Creating Team with Name: {} with Prefix {} and Suffix {}", newTeam.getName(), ColorUtil.getRaw(newTeam.getPrefix()), ColorUtil.getRaw(newTeam.getSuffix()));
        }

        PacketUtil.broadcast(newTeam.getCreatePacket());

        return newTeam;
    }

    /**
     * Get a {@link NameTagTeam} by its name.
     *
     * @param name The name of the team to retrieve.
     * @return The {@link NameTagTeam} with the specified name, or null if not found.
     */
    public NameTagTeam getByName(String name) {
        return teamCache.values().stream()
                .filter(team -> team.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}