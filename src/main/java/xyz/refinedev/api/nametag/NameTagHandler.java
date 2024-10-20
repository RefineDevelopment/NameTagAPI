package xyz.refinedev.api.nametag;

import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.manager.server.ServerManager;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;

import com.google.common.base.Preconditions;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import net.kyori.adventure.text.Component;

import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.team.ScoreboardTeam;
import net.megavex.scoreboardlibrary.api.team.TeamDisplay;
import net.megavex.scoreboardlibrary.api.team.TeamManager;
import net.megavex.scoreboardlibrary.api.team.enums.CollisionRule;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.refinedev.api.nametag.adapter.DefaultNameTagAdapter;
import xyz.refinedev.api.nametag.adapter.NameTagAdapter;
import xyz.refinedev.api.nametag.listener.DisguiseListener;
import xyz.refinedev.api.nametag.listener.GlitchFixListener;
import xyz.refinedev.api.nametag.listener.NameTagListener;
import xyz.refinedev.api.nametag.setup.NameTagTeam;
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
     *           <center>Viewer -> Target -> {@link ScoreboardTeam}</center>
     * </p>
     */
    private final Map<UUID, Map<UUID, String>> teamMap = new ConcurrentHashMap<>();
    /**
     * All registered teams are stored here
     */
    private final Map<String, String> teamCache = new ConcurrentHashMap<>();
    private static final String SEPARATOR = "::"; // Separator for prefix-suffix keys
    /**
     * The plugin registering this NameTag Handler
     */
    private final JavaPlugin plugin;
    /**
     * Scoreboard Library instance
     */
    private ScoreboardLibrary scoreboardLibrary;
    /**
     * Team Manager of this instance
     */
    private TeamManager teamManager;
    /**
     * NameTag Adapter of this instance
     */
    private NameTagAdapter adapter;
    private PacketEventsAPI<?> packetEvents;
    private boolean collisionEnabled, debugMode;

    public NameTagHandler(JavaPlugin plugin) {
        instance = this;
        this.plugin = plugin;
    }

    public void init(PacketEventsAPI<?> packetEvents, boolean registerListeners) {
        ScoreboardLibrary scoreboardLibrary = Bukkit.getServicesManager().load(ScoreboardLibrary.class);
        Preconditions.checkArgument(scoreboardLibrary != null, "ScoreboardLibrary is not registered!");

        this.adapter = new DefaultNameTagAdapter();
        this.scoreboardLibrary = scoreboardLibrary;
        this.teamManager = scoreboardLibrary.createTeamManager();
        this.packetEvents = packetEvents;
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
    }

    /**
     * Shutdown Logic of this NameTag Handler
     */
    public void unload() {
        this.teamMap.clear();
        this.teamManager.close();
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
        if (this.teamCache.isEmpty()) {
            this.teamManager.addPlayer(player); // Player will be added to the default TeamDisplay of each ScoreboardTeam
            this.adapter.fetchNameTag(player, player);
            return;
        }

        this.teamManager.addPlayer(player); // Player will be added to the default TeamDisplay of each ScoreboardTeam
    }

    /**
     * Unload the player's cached data
     *
     * @param player {@link Player} Target
     */
    public void unloadPlayer(Player player) {
        this.teamManager.removePlayer(player);
        this.teamMap.remove(player.getUniqueId());
    }

    /**
     * Refresh the specified target for a specific viewer
     *
     * @param toRefresh  {@link Player} target
     * @param refreshFor {@link Player} viewer
     */
    public void reloadPlayer(Player toRefresh, Player refreshFor) {
        this.reloadPlayerInternal(toRefresh, refreshFor);
    }

    /**
     * Refresh the specified target for all viewers
     *
     * @param toRefresh {@link Player} target
     */
    public void reloadPlayer(Player toRefresh) {
        this.applyUpdate(null, toRefresh.getUniqueId(), false);
    }

    /**
     * Refresh the all players for a specified viewer
     *
     * @param refreshFor {@link Player} viewer
     */
    public void reloadOthersFor(Player refreshFor) {
        for (Player toRefresh : Bukkit.getOnlinePlayers()) {
            if (refreshFor == toRefresh) continue;
            this.reloadPlayerInternal(toRefresh, refreshFor);
        }
    }

    /**
     * Apply a nametag update according to
     * the specified conditions to the viewer/target
     *
     * @param viewer {@link UUID} Viewer
     * @param target {@link UUID} Target
     * @param global {@link Boolean} Viewers are all players?
     */
    public void applyUpdate(UUID viewer, UUID target, boolean global) {
        if (global) {
            Player refreshFor = Bukkit.getPlayer(viewer);
            if (refreshFor == null) return;

            for (Player player : Bukkit.getOnlinePlayers()) {
                this.reloadPlayerInternal(player, refreshFor);
            }
            return;
        }

        Player toRefreshPlayer = Bukkit.getPlayer(target);
        if (toRefreshPlayer == null) {
            return;
        }

        if (viewer == null) {
            for ( Player player : Bukkit.getOnlinePlayers() ) {
                this.reloadPlayerInternal(toRefreshPlayer, player);
            }
        } else {
            Player refreshForPlayer = Bukkit.getPlayer(viewer);

            if (refreshForPlayer != null) {
                this.reloadPlayerInternal(toRefreshPlayer, refreshForPlayer);
            }
        }
    }

    public void reloadPlayerInternal(Player toRefresh, Player refreshFor) {
        ScoreboardTeam provided = this.adapter.fetchNameTag(toRefresh, refreshFor);
        if (provided == null) return;

        Map<UUID, String> teamInfoMap = this.teamMap.computeIfAbsent(refreshFor.getUniqueId(), (t) -> new HashMap<>());

        // Don't spam repeat the same NameTag to the client
        // Netty wakeup calls are expensive!!
        String previousName = teamInfoMap.get(toRefresh.getUniqueId());
        ScoreboardTeam previous = previousName == null ? null : this.teamManager.team(previousName);
        if (provided.equals(previous)) {
            return;
        }
        
        if (previous != null) {
            previous.defaultDisplay().removeEntry(toRefresh.getName());
        }
        
        TeamDisplay teamDisplay = provided.defaultDisplay();
        teamDisplay.addEntry(toRefresh.getName());

        // In 1.16, the issue arises that hex color does not apply to the name of the player.
        // This is due to the ScoreboardTeam color being applied to the name, which is a plain enum with normal colors.
        // It does not support Hex Colors, for Teams, I get the nearest ChatColor to the hex color but for displaying in tablist,
        // we can just use display name packet which will apply HexColor.
        if (VersionUtil.canHex()) {
            ServerManager manager = this.packetEvents.getServerManager();

            UserProfile profile = new UserProfile(toRefresh.getUniqueId(), toRefresh.getName());
            Component displayName = teamDisplay.prefix().append(Component.text(toRefresh.getName())).append(teamDisplay.suffix());

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
        teamInfoMap.put(toRefresh.getUniqueId(), provided.name());
        this.teamMap.put(refreshFor.getUniqueId(), teamInfoMap);
    }

    /**
     * Get a {@link ScoreboardTeam} associated with the given prefix and suffix.
     * If we don't have one for these prefixes and suffixes, then we make one and send it to everyone.
     *
     * @param prefix {@link String} Raw Prefix
     * @param suffix {@link String} Raw Suffix
     * @return       {@link ScoreboardTeam} Associated Team
     */
    public ScoreboardTeam getOrCreate(String prefix, String suffix) {
        if (debugMode) {
            log.info("[NameTagAPI-Debug] Trying to fetch a team with prefix {} and suffix {}", ColorUtil.getRaw(prefix), ColorUtil.getRaw(suffix));
        }

        // Create the unique key for caching
        String key = prefix + SEPARATOR + suffix;

        // Attempt to get the team from the cache
        String teamName = this.teamCache.get(key);
        ScoreboardTeam team = teamName == null ? null : this.teamManager.team(teamName);
        if (team != null) {
            return team;
        }

        // Team doesn't exist; create a new one
        TEAM_INDEX++;

        String name = "boltNT" + TEAM_INDEX;
        while (teamManager.teamExists(name)) {
            TEAM_INDEX++;
        }

        name = "boltNT" + TEAM_INDEX;
        ScoreboardTeam newTeam = this.teamManager.createIfAbsent(name);

        // Cache the newly created team
        teamCache.put(key, name);

        TeamDisplay display = newTeam.defaultDisplay();
        display.prefix(ColorUtil.translate(prefix));
        display.suffix(ColorUtil.translate(suffix));
        display.collisionRule(collisionEnabled ? CollisionRule.ALWAYS : CollisionRule.NEVER);

        for ( Player player : Bukkit.getOnlinePlayers() ) {
            newTeam.display(player);
        }

        if (debugMode) {
            log.info("[NameTagAPI-Debug] Creating Team with Name: {} with Prefix {} and Suffix {}", newTeam.name(), ColorUtil.getRaw(prefix), ColorUtil.getRaw(suffix));
        }
        return newTeam;
    }
}