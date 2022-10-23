package xyz.refinedev.api.nametag.setup;

import lombok.Getter;
import xyz.refinedev.api.nametag.NameTagHandler;
import xyz.refinedev.api.nametag.protocol.SBTeamNMS;

import java.util.ArrayList;

@Getter
public class NameTagInfo {

    private final String name;
    private final String prefix;
    private final String suffix;
    private final SBTeamNMS teamAddPacket;

    public NameTagInfo(String name, String prefix, String suffix) {
        this.name = name;
        this.prefix = prefix;
        this.suffix = suffix;

        this.teamAddPacket = NameTagHandler.getInstance().getNMS().create(name, prefix, suffix, new ArrayList<>(), 0);
    }
}