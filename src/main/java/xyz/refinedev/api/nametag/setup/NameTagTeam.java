package xyz.refinedev.api.nametag.setup;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import lombok.Getter;
import lombok.Setter;

import xyz.refinedev.api.nametag.packet.ScoreboardPacket;
import xyz.refinedev.api.nametag.util.ColorUtil;
import xyz.refinedev.api.nametag.util.VersionUtil;

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

        if (VersionUtil.MINOR_VERSION > 16) {
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
            this.createPacket = new ScoreboardPacket(name, prefix, suffix);
        }
    }

    public PacketWrapper<?> getPECreatePacket() {
       return (PacketWrapper<?>) createPacket;
    }

    public ScoreboardPacket getNormalCreatePacket() {
        return (ScoreboardPacket) createPacket;
    }
}