package xyz.refinedev.nametag.setup;

import lombok.Getter;
import xyz.refinedev.nametag.protocol.ScoreboardTeamPacketMod;

import java.util.ArrayList;

@Getter
public class NameTagInfo {

    private final String name;
    private final String prefix;
    private final String suffix;
    private final ScoreboardTeamPacketMod teamAddPacket;

    public NameTagInfo(String name, String prefix, String suffix) {
        this.name = name;
        this.prefix = prefix;
        this.suffix = suffix;

        teamAddPacket = new ScoreboardTeamPacketMod(name, prefix, suffix, new ArrayList<>(), 0);
    }
}