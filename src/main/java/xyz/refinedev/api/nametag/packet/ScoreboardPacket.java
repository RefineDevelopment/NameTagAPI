package xyz.refinedev.api.nametag.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

import lombok.Getter;

import xyz.refinedev.api.nametag.NameTagHandler;
import xyz.refinedev.api.nametag.util.ColorUtil;
import xyz.refinedev.api.nametag.util.VersionUtil;

import java.util.Collection;

/**
 * This Project is property of Refine Development.
 * Copyright © 2023, All Rights Reserved.
 * Redistribution of this Project is not allowed.
 *
 * @author Drizzy
 * @since 9/12/2023
 * @version NameTagAPI
 */

@Getter
public class ScoreboardPacket {

    private final NameTagHandler nameTagHandler = NameTagHandler.getInstance();

    // 0 - create
    // 1 - remove
    // 2 - update
    // 3 - add entities
    // 4 - remove entities

    private final PacketContainer container;

    public ScoreboardPacket(String name, String prefix, String suffix) {
        this.container = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
        this.setCreateFields(name, prefix, suffix);
    }

    public ScoreboardPacket(String name, Collection<String> players) {
        this.container = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
        this.setAddFields(name, players);
    }

    private void setCreateFields(String name, String prefix, String suffix) {
        String collisionRule = nameTagHandler.isCollisionEnabled() ? "always" : "never";

        container.getModifier().writeDefaults();
        switch (VersionUtil.MINOR_VERSION) {
            case 8: {
                container.getStrings()
                        .writeSafely(0, name) // Team Name
                        .writeSafely(1, name) // Display name
                        .writeSafely(2, ColorUtil.color(prefix)) // Prefix
                        .writeSafely(3, ColorUtil.color(suffix)) // Suffix
                        .writeSafely(4, "always");

                container.getIntegers()
                        .writeSafely(1, 0) // Team Mode
                        .writeSafely(2, 1); // Packet Option Data
                break;
            }
            case 12: {
                container.getStrings()
                        .writeSafely(0, name) // Team Name
                        .writeSafely(1, name) // Display name
                        .writeSafely(2, ColorUtil.color(prefix)) // Prefix
                        .writeSafely(3, ColorUtil.color(prefix)) // Suffix
                        .writeSafely(4, "always") // NameTag Visibility
                        .writeSafely(5, collisionRule); // Collision Rule
                container.getIntegers()
                        .writeSafely(0, -1) // Color
                        .writeSafely(1, 0) // Team Mode
                        .writeSafely(2, 1); // Packet Option Data
                break;
            }
            default: {
                if (!VersionUtil.canHex()) {
                    throw new IllegalArgumentException("[NameTagAPI] Version not supported!");
                }

                throw new IllegalStateException("[NameTagAPI] How did you get here?!");
            }
        }
    }

    private void setAddFields(String name, Collection<String> players) {
        String collisionRule = nameTagHandler.isCollisionEnabled() ? "always" : "never";

        container.getModifier().writeDefaults();
        switch (VersionUtil.MINOR_VERSION) {
            case 8: {
                container.getStrings().writeSafely(0, name); // Team Name
                container.getIntegers().writeSafely(1, 3); // Team Mode
                container.getSpecificModifier(Collection.class).writeSafely(0, players);
                break;
            }
            case 12: {
                container.getStrings()
                        .writeSafely(0, name) // Team Name
                        .writeSafely(4, "always") // NameTag Visibility
                        .writeSafely(5, collisionRule); // Collision Rule
                container.getIntegers()
                        .writeSafely(0, -1) // Color
                        .writeSafely(1, 3); // Team Mode

                container.getSpecificModifier(Collection.class).writeSafely(0, players);
                break;
            }
            default: {
                if (!VersionUtil.canHex()) {
                    throw new IllegalArgumentException("[NameTagAPI] Version not supported!");
                }
//                container.getStrings().writeSafely(0, name); // Team Name
//                final StructureModifier<Integer> integers = container.getIntegers();
//                if (integers.size() > 1) {
//                    integers.writeSafely(1, 3); // mode
//                } else if (integers.size() > 0) {
//                    integers.writeSafely(0, 3); // mode
//                }
//                container.getSpecificModifier(Collection.class).writeSafely(0, players);

                throw new IllegalStateException("[NameTagAPI] How did you get here?!");
            }
        }
    }
}
