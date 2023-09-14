package xyz.refinedev.api.nametag.setup;

import lombok.Getter;
import lombok.Setter;

import xyz.refinedev.api.nametag.packet.ScoreboardPacket;

@Getter @Setter
public class NameTagInfo {

    private final String name;
    private final String prefix;
    private final String suffix;
    private final ScoreboardPacket teamAddPacket;

    public NameTagInfo(String name, String prefix, String suffix) {
        this.name = name;
        this.prefix = prefix;
        this.suffix = suffix;
        this.teamAddPacket = new ScoreboardPacket(name, prefix, suffix, 0);
    }
}