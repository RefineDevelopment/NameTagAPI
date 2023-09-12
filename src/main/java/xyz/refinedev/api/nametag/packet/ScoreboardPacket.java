package xyz.refinedev.api.nametag.packet;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.util.adventure.AdventureSerializer;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This Project is property of Refine Development © 2021 - 2023
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/12/2023
 * Project: NameTagAPI
 */
public class ScoreboardPacket {

    // 0 - create
    // 1 - remove
    // 2 - update
    // 3 - add entities
    // 4 - remove entities

    private final WrapperPlayServerTeams packet;

    public ScoreboardPacket(String name, String prefix, String suffix, Collection<String> players, int paramInt) {
        WrapperPlayServerTeams.ScoreBoardTeamInfo info = this.createInfo(name, prefix, suffix);
        info.setOptionData(WrapperPlayServerTeams.OptionData.FRIENDLY_FIRE);

        this.packet = new WrapperPlayServerTeams(name, this.getByParam(paramInt), info, players);
    }

    public ScoreboardPacket(String name, Collection<String> players, int paramInt) {
        WrapperPlayServerTeams.ScoreBoardTeamInfo info = this.createInfo(name);
        if (players == null) players = new ArrayList<>();

        this.packet = new WrapperPlayServerTeams(name, this.getByParam(paramInt), info, players);
    }

    public void sendToPlayer(Player target) {
        PacketEvents.getAPI().getPlayerManager().sendPacket(target, this.packet);
    }

    private WrapperPlayServerTeams.ScoreBoardTeamInfo createInfo(String teamName) {
        return this.createInfo(teamName, null, null);
    }

    private WrapperPlayServerTeams.ScoreBoardTeamInfo createInfo(String teamName, @Nullable String prefix, @Nullable String suffix) {
        return new WrapperPlayServerTeams.ScoreBoardTeamInfo(
                AdventureSerializer.fromLegacyFormat(teamName),
                prefix == null ? Component.empty() : AdventureSerializer.fromLegacyFormat(prefix),
                suffix == null ? Component.empty() : AdventureSerializer.fromLegacyFormat(suffix),
                WrapperPlayServerTeams.NameTagVisibility.ALWAYS,
                WrapperPlayServerTeams.CollisionRule.NEVER,
                NamedTextColor.WHITE,
                WrapperPlayServerTeams.OptionData.NONE
        );
    }

    private WrapperPlayServerTeams.TeamMode getByParam(int paramInt) {
        return WrapperPlayServerTeams.TeamMode.values()[paramInt];
    }

}
