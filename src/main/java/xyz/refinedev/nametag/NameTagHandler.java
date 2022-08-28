package xyz.refinedev.nametag;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.refinedev.nametag.adapter.DefaultNameTagAdapter;
import xyz.refinedev.nametag.adapter.NameTagAdapter;
import xyz.refinedev.nametag.listener.NameTagListener;
import xyz.refinedev.nametag.protocol.ScoreboardTeamPacketMod;
import xyz.refinedev.nametag.setup.NameTagComparator;
import xyz.refinedev.nametag.setup.NameTagInfo;
import xyz.refinedev.nametag.setup.NameTagThread;
import xyz.refinedev.nametag.setup.NameTagUpdate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class NameTagHandler {

    @Getter
    private static NameTagHandler instance;

    private final Map<UUID, Map<UUID, NameTagInfo>> teamMap = new ConcurrentHashMap<>();
    private final List<NameTagInfo> registeredTeams = Collections.synchronizedList(new ArrayList<>());
    private final List<NameTagAdapter> providers = new ArrayList<>();

    private NameTagThread thread;
    private final JavaPlugin plugin;

    private boolean initiated;
    private static int teamCreateIndex = 1;

    public NameTagHandler(JavaPlugin plugin) {
        instance = this;

        this.plugin = plugin;
        this.initiated = true;

        this.thread = new NameTagThread(plugin);
        this.thread.start();

        this.plugin.getServer().getPluginManager().registerEvents(new NameTagListener(this), this.plugin);
    }

    public void registerAdapter(NameTagAdapter newAdapter) {
        this.providers.add(newAdapter);
        this.providers.sort(new NameTagComparator());
    }

    /**
     * Refresh the specified target for a specific viewer
     *
     * @param toRefresh  {@link Player} target
     * @param refreshFor {@link Player} viewer
     */
    public void reloadPlayer(Player toRefresh, Player refreshFor) {
        NameTagUpdate update = new NameTagUpdate(toRefresh, refreshFor);
        thread.getPendingUpdates().put(update, true);
    }

    /**
     * Refresh the specified target for all viewers
     *
     * @param toRefresh {@link Player} target
     */
    public void reloadPlayer(Player toRefresh) {
        NameTagUpdate update = new NameTagUpdate(toRefresh);
        thread.getPendingUpdates().put(update, true);
    }

    /**
     * Refresh the all players for a specified viewer
     *
     * @param refreshFor {@link Player} viewer
     */
    public void reloadOthersFor(Player refreshFor) {
        for (Player toRefresh : this.plugin.getServer().getOnlinePlayers()) {
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
        Player toRefreshPlayer = this.plugin.getServer().getPlayer(nameTagUpdate.getToRefresh());

        if (toRefreshPlayer == null) return;

        if (nameTagUpdate.getRefreshFor() == null) {
            this.plugin.getServer().getOnlinePlayers().forEach(refreshFor -> this.reloadPlayerInternal(toRefreshPlayer, refreshFor));
        } else {
            Player refreshForPlayer = this.plugin.getServer().getPlayer(nameTagUpdate.getRefreshFor());

            if (refreshForPlayer != null) {
                this.reloadPlayerInternal(toRefreshPlayer, refreshForPlayer);
            }
        }
    }

    public void reloadPlayerInternal(Player toRefresh, Player refreshFor) {
        if (!refreshFor.hasMetadata("name-LoggedIn")) return;

        NameTagInfo provided = null;

        for (NameTagAdapter nametagAdapter : providers) {
            provided = nametagAdapter.fetchNameTag(toRefresh, refreshFor);
            if (provided != null) break;
        }

        if (provided == null) return;

        Map<UUID, NameTagInfo> teamInfoMap = new HashMap<>();

        if (teamMap.containsKey(refreshFor.getUniqueId())) {
            teamInfoMap = teamMap.get(refreshFor.getUniqueId());
        }

        ScoreboardTeamPacketMod packet = new ScoreboardTeamPacketMod(provided.getName(), Collections.singletonList(toRefresh.getName()), 3);
        packet.sendToPlayer(refreshFor);

        teamInfoMap.put(toRefresh.getUniqueId(), provided);
        teamMap.put(refreshFor.getUniqueId(), teamInfoMap);
    }

    public void initiatePlayer(Player player) {
        if (this.providers.size() == 0) {
            this.registerAdapter(new DefaultNameTagAdapter());
        }
        registeredTeams.forEach(teamInfo -> teamInfo.getTeamAddPacket().sendToPlayer(player));
    }

    public NameTagInfo getOrCreate(String prefix, String suffix) {
        for (NameTagInfo teamInfo : registeredTeams) {
            if (teamInfo.getPrefix().equals(prefix) && teamInfo.getSuffix().equals(suffix)) {
                return (teamInfo);
            }
        }

        NameTagInfo newTeam = new NameTagInfo(String.valueOf(teamCreateIndex++), prefix, suffix);
        registeredTeams.add(newTeam);

        ScoreboardTeamPacketMod addPacket = newTeam.getTeamAddPacket();
        this.plugin.getServer().getOnlinePlayers().forEach(addPacket::sendToPlayer);

        return (newTeam);
    }
}