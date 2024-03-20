package xyz.refinedev.api.nametag.setup;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import lombok.Getter;
import lombok.Setter;

import org.bukkit.entity.Player;
import xyz.refinedev.api.nametag.util.packet.ScoreboardPacket;
import xyz.refinedev.api.nametag.util.chat.ColorUtil;
import xyz.refinedev.api.nametag.util.packet.PacketUtil;
import xyz.refinedev.api.nametag.util.VersionUtil;

import java.util.Objects;

@Getter @Setter
public class NameTagTeam {

    private final String name;
    private final String prefix;
    private final String suffix;
    private final Object createPacket;

    public NameTagTeam(String name, String prefix, String suffix, boolean collide) {
        this.name = name;
        this.prefix = prefix;
        this.suffix = suffix;

        if (VersionUtil.MINOR_VERSION > 8) {
            WrapperPlayServerTeams.ScoreBoardTeamInfo info = new WrapperPlayServerTeams.ScoreBoardTeamInfo(
                    ColorUtil.translate(name),
                    ColorUtil.translate(prefix),
                    ColorUtil.translate(suffix),
                    WrapperPlayServerTeams.NameTagVisibility.ALWAYS,
                    collide ? WrapperPlayServerTeams.CollisionRule.ALWAYS : WrapperPlayServerTeams.CollisionRule.NEVER,
                    ColorUtil.getLastColor(prefix),
                    WrapperPlayServerTeams.OptionData.NONE);

            this.createPacket = new WrapperPlayServerTeams(name, WrapperPlayServerTeams.TeamMode.CREATE, info);
        } else {
            this.createPacket = ScoreboardPacket.creationPacket(name, ColorUtil.color(prefix), ColorUtil.color(suffix));
        }
    }

    public PacketWrapper<?> getPECreatePacket() {
       return (PacketWrapper<?>) createPacket;
    }

    public void destroyFor(Player player) {
        WrapperPlayServerTeams packet = new WrapperPlayServerTeams(name, WrapperPlayServerTeams.TeamMode.REMOVE, new WrapperPlayServerTeams.ScoreBoardTeamInfo(
                ColorUtil.translate(name),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
        ));
        PacketUtil.sendPacket(player, packet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, prefix, suffix);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof NameTagTeam)) return false;

        NameTagTeam team = (NameTagTeam) object;
        return team.getName().equals(this.name) && team.getPrefix().equals(prefix) && team.getSuffix().equals(suffix);
    }
}