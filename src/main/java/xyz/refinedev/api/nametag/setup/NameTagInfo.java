package xyz.refinedev.api.nametag.setup;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import xyz.refinedev.api.nametag.packet.ScoreboardPacket;

import java.util.ArrayList;

@Getter @Setter
@RequiredArgsConstructor
public class NameTagInfo {

    private final String name;
    private final String prefix;
    private final String suffix;
    private final ScoreboardPacket teamAddPacket = new ScoreboardPacket(name, prefix, suffix, new ArrayList<>(), 0);
}