package xyz.refinedev.api.nametag.setup;

import static com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams.*;

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import lombok.Getter;
import lombok.Setter;

import xyz.refinedev.api.nametag.util.ColorUtil;

@Getter @Setter
public class NameTagTeam {

    private final String name;
    private final String prefix;
    private final String suffix;
    private final WrapperPlayServerTeams createPacket;

    public NameTagTeam(String name, String prefix, String suffix, boolean collide) {
        this.name = name;
        this.prefix = prefix;
        this.suffix = suffix;

        ScoreBoardTeamInfo info = new ScoreBoardTeamInfo(
                ColorUtil.translate(name),
                ColorUtil.translate(prefix),
                ColorUtil.translate(suffix),
                NameTagVisibility.ALWAYS,
                collide ? CollisionRule.ALWAYS : CollisionRule.NEVER,
                ColorUtil.getLastColor(prefix),
                OptionData.NONE);

        this.createPacket = new WrapperPlayServerTeams(name, TeamMode.CREATE, info);
    }
}