package xyz.refinedev.api.nametag;

import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.manager.server.ServerManager;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.util.adventure.AdventureSerializer;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.refinedev.api.nametag.adapter.DefaultNameTagAdapter;
import xyz.refinedev.api.nametag.adapter.NameTagAdapter;
import xyz.refinedev.api.nametag.listener.DisguiseListener;
import xyz.refinedev.api.nametag.listener.GlitchFixListener;
import xyz.refinedev.api.nametag.listener.NameTagListener;
import xyz.refinedev.api.nametag.packet.ScoreboardPacket;
import xyz.refinedev.api.nametag.setup.NameTagTeam;
import xyz.refinedev.api.nametag.setup.NameTagUpdate;
import xyz.refinedev.api.nametag.thread.NameTagThread;
import xyz.refinedev.api.nametag.util.ColorUtil;
import xyz.refinedev.api.nametag.util.PacketUtil;
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
     *           <center>Viewer -> Target -> {@link NameTagTeam}</center>
     * </p>
     */
    private final Map<UUID, Map<UUID, NameTagTeam>> teamMap = new ConcurrentHashMap<>();
    /**
     * All registered teams are stored here
     */
    private final List<NameTagTeam> registeredTeams = ObjectLists.synchronize(new ObjectArrayList<>());
    /**
     * The plugin registering this NameTag Handler
     */
    private final JavaPlugin plugin;
    /**
     * NameTag Adapter of this instance
     */
    private NameTagAdapter adapter;
    /**
     * This thread handles all the operations surrounding
     * ticking and updating the NameTags
     */
    private NameTagThread thread;
    private PacketEventsAPI<?> packetEvents;
    private boolean collisionEnabled, debugMode;

    public NameTagHandler(JavaPlugin plugin) {
        instance = this;
        this.plugin = plugin;
        this.debugMode = Boolean.getBoolean("BDebug");
    }

    /**
     * Set up the PacketEvents instance of this NameTagHandler Handler.
     * We let the plugin initialize and handle the PacketEvents instance.
     */
    public void init(PacketEventsAPI<?> packetEventsAPI) {
        this.packetEvents = packetEventsAPI;
        this.adapter = new DefaultNameTagAdapter();

        this.packetEvents.getEventManager().registerListener(new DisguiseListener());
        Bukkit.getPluginManager().registerEvents(new NameTagListener(this), this.plugin);
        Bukkit.getPluginManager().registerEvents(new GlitchFixListener(this), this.plugin);
    }

    /**
     * Shutdown Logic of this NameTag Handler
     */
    public void unload() {
        this.thread.stopExecuting();
    }

    public void registerAdapter(NameTagAdapter adapter, long ticks) {
        this.adapter = adapter == null ? new DefaultNameTagAdapter() : adapter;

        if (ticks < 1L) {
            log.info("[{}] Provided refresh tick rate for NameTag is too low, reverting to 2 ticks!", plugin.getName());
            ticks = 2L;
        }

        this.thread = new NameTagThread(this, ticks);
        this.thread.start();
    }

    /**
     * This method is only used once on join to create all
     * the teams for our cache AND the player. It's only used once for
     * the first player joining the server.
     *
     * @param player {@link Player} Target
     */
    public void createTeams(Player player) {
        this.adapter.fetchNameTag(player, player);
    }

    /**
     * Send all {@link NameTagTeam} teams to this player
     *
     * @param player {@link Player} Target
     */
    public void initiatePlayer(Player player) {
        for ( NameTagTeam teamInfo : this.registeredTeams ) {
            if (VersionUtil.MINOR_VERSION > 12) {
                PacketUtil.sendPacket(player, teamInfo.getPECreatePacket());
            } else {
                PacketUtil.sendPacket(player, teamInfo.getNormalCreatePacket());
            }
        }
    }

    /**
     * Unload the player's cached data
     *
     * @param player {@link Player} Target
     */
    public void unloadPlayer(Player player) {
        for ( NameTagTeam team : registeredTeams ) {
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

    public void reloadPlayerInternal(Player toRefresh, Player refreshFor) {
        NameTagTeam provided = this.adapter.fetchNameTag(toRefresh, refreshFor);
        if (provided == null) return;

        //TODO: Sort Priority system, by sending remove packets!!
        if (VersionUtil.MINOR_VERSION > 12) {
            WrapperPlayServerTeams packet = new WrapperPlayServerTeams(provided.getName(), WrapperPlayServerTeams.TeamMode.ADD_ENTITIES, (WrapperPlayServerTeams.ScoreBoardTeamInfo) null, toRefresh.getName());
            PacketUtil.sendPacket(refreshFor, packet);
        } else {
            ScoreboardPacket packet = new ScoreboardPacket(provided.getName(), Collections.singletonList(toRefresh.getName()));
            PacketUtil.sendPacket(refreshFor, packet);
        }

        // In 1.16, the issue arises that hex color does not apply to the name of the player.
        // This is due to the ScoreboardTeam color being applied to the name, which is a plain enum with normal colors.
        // It does not support Hex Colors, for Teams, I get the nearest ChatColor to the hex color but for displaying in tablist,
        // we can just use display name packet which will apply HexColor.
        if (VersionUtil.canHex()) {
            ServerManager manager = this.packetEvents.getServerManager();

            UserProfile profile = new UserProfile(toRefresh.getUniqueId(), toRefresh.getName());
            String text = ColorUtil.color(provided.getPrefix() + toRefresh.getName() + provided.getSuffix());

            PacketWrapper<?> display;
            if (manager.getVersion().isNewerThanOrEquals(ServerVersion.V_1_19_3)) {
                WrapperPlayServerPlayerInfoUpdate.PlayerInfo data = new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(
                        profile,
                        true,
                        0,
                        null,
                        AdventureSerializer.fromLegacyFormat(text),
                        null
                );
                display = new WrapperPlayServerPlayerInfoUpdate(WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_DISPLAY_NAME, data);
            } else {
                WrapperPlayServerPlayerInfo.PlayerData data = new WrapperPlayServerPlayerInfo.PlayerData(
                        AdventureSerializer.fromLegacyFormat(text),
                        profile,
                        null,
                        0
                );
                display = new WrapperPlayServerPlayerInfo(WrapperPlayServerPlayerInfo.Action.UPDATE_DISPLAY_NAME, data);
            }

            PacketUtil.sendPacket(refreshFor, display);
        }

        // Update and store the new team for this target according to the viewer
        Map<UUID, NameTagTeam> teamInfoMap = new HashMap<>();
        if (this.teamMap.containsKey(refreshFor.getUniqueId())) {
            teamInfoMap = this.teamMap.get(refreshFor.getUniqueId());
        }

        teamInfoMap.put(toRefresh.getUniqueId(), provided);
        this.teamMap.put(refreshFor.getUniqueId(), teamInfoMap);
    }

    private NameTagTeam cachedTeam;

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

        if (cachedTeam != null && cachedTeam.getPrefix().equals(prefix) && cachedTeam.getSuffix().equals(suffix)) {
            return (cachedTeam);
        }

        for ( NameTagTeam teamInfo : registeredTeams) {
            if (teamInfo.getPrefix().equals(prefix) && teamInfo.getSuffix().equals(suffix)) {
                return (teamInfo);
            }
        }
        TEAM_INDEX++;

        NameTagTeam newTeam = new NameTagTeam("boltNT" + TEAM_INDEX, prefix, suffix, collisionEnabled);
        cachedTeam = newTeam;

        if (debugMode) {
            log.info("[NameTagAPI-Debug] Creating Team with Name: {} with Prefix {} and Suffix {}", newTeam.getName(), ColorUtil.getRaw(newTeam.getPrefix()), ColorUtil.getRaw(newTeam.getSuffix()));
        }
        this.registeredTeams.add(newTeam);

        if (VersionUtil.MINOR_VERSION > 12) {
            PacketUtil.broadcast(newTeam.getPECreatePacket());
        } else {
            PacketUtil.broadcast(newTeam.getNormalCreatePacket());
        }
        return newTeam;
    }
}