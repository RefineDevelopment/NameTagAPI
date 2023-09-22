package xyz.refinedev.api.nametag.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import org.bukkit.entity.Player;

import xyz.refinedev.api.nametag.packet.wrapper.WrappedScoreboardTeam;
import xyz.refinedev.api.nametag.util.ColorUtil;
import xyz.refinedev.api.nametag.util.VersionUtil;

import java.util.Collection;
import java.util.Optional;

/**
 * This Project is property of Refine Development.
 * Copyright © 2023, All Rights Reserved.
 * Redistribution of this Project is not allowed.
 *
 * @author Drizzy
 * @since 9/12/2023
 * @version NameTagAPI
 */
public class ScoreboardPacket {


    // 0 - create
    // 1 - remove
    // 2 - update
    // 3 - add entities
    // 4 - remove entities

    private final PacketContainer container;

    public ScoreboardPacket(String name, String prefix, String suffix, int paramInt) {
        this.container = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
        this.setCreateFields(name, prefix, suffix, paramInt);
    }

    public ScoreboardPacket(String name, Collection<String> players, int paramInt) {
        this.container = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
        this.setAddFields(name, players, paramInt);
    }

    private void setCreateFields(String name, String prefix, String suffix, int paramInt) {
        container.getModifier().writeDefaults();
        switch (VersionUtil.MINOR_VERSION) {
            case 8: {
                if (prefix.length() > 16) {
                    prefix = prefix.substring(0, 16);
                }
                if (suffix.length() > 16) {
                    suffix = suffix.substring(0, 16);
                }
                container.getStrings()
                        .writeSafely(0, name) // Team Name
                        .writeSafely(1, name) // Display name
                        .writeSafely(2, prefix) // Prefix
                        .writeSafely(3, suffix) // Suffix
                        .writeSafely(4, "always");

                container.getIntegers()
                        .writeSafely(1, paramInt) // Team Mode
                        .writeSafely(2, 1); // Packet Option Data
                break;
            }
            case 12: {
                if (prefix.length() > 16) {
                    prefix = prefix.substring(0, 16);
                }
                if (suffix.length() > 16) {
                    suffix = suffix.substring(0, 16);
                }
                container.getStrings()
                        .writeSafely(0, name) // Team Name
                        .writeSafely(1, name) // Display name
                        .writeSafely(2, prefix) // Prefix
                        .writeSafely(3, suffix) // Suffix
                        .writeSafely(4, "always") // NameTag Visibility
                        .writeSafely(5, "always"); // Collision Rule
                container.getIntegers()
                        .writeSafely(0, -1) // Color
                        .writeSafely(1, paramInt) // Team Mode
                        .writeSafely(2, 1); // Packet Option Data
                break;
            }
            case 16: {
                container.getStrings()
                        .writeSafely(0, name) // Team Name
                        .writeSafely(1, "always") // NameTag Visibility
                        .writeSafely(2, "always"); // Collision Rule
                container.getChatComponents()
                        .writeSafely(0, WrappedChatComponent.fromLegacyText(name))
                        .writeSafely(1, WrappedChatComponent.fromLegacyText(prefix))
                        .writeSafely(2, WrappedChatComponent.fromLegacyText(suffix));
                container.getIntegers()
                        .writeSafely(0, paramInt) // Team Mode
                        .writeSafely(1, 1); // Packet Option Data
                break;
            }
            default: {
                if (!VersionUtil.isModern()) {
                    throw new IllegalArgumentException("[NameTagAPI] Version not support!");
                }
                container.getStrings().writeSafely(0, name); // Team Name

                final StructureModifier<Optional<?>> optionals = container.getModifier().withType(Optional.class);
                optionals.writeDefaults();

                final StructureModifier<Integer> integers = container.getIntegers();
                if (integers.size() > 1) {
                    integers.writeSafely(7, paramInt); // mode
                } else if (integers.size() > 0) {
                    integers.writeSafely(0, paramInt); // mode
                }

                Optional<?> optional = optionals.read(0);
                if (optional.isPresent()) { // Make sure the structure exists (it always does)
                    final WrappedScoreboardTeam team = WrappedScoreboardTeam.fromHandle(optional.get());
                    team.setDisplayName(WrappedChatComponent.fromText(name)); // team display name
                    team.setPrefix(WrappedChatComponent.fromText(prefix)); // prefix
                    team.setSuffix(WrappedChatComponent.fromText(suffix)); // suffix
                    team.setCollisionRule("always");
                    team.setNameTagVisibility("always");
                    team.setTeamColor(ColorUtil.convertToColor(prefix));
                    optionals.write(0, Optional.of(team.getHandle()));
                }
                break;
            }
        }
    }

    private void setAddFields(String name, Collection<String> players, int paramInt) {
        container.getModifier().writeDefaults();
        switch (VersionUtil.MINOR_VERSION) {
            case 8: {
                container.getStrings().writeSafely(0, name); // Team Name
                container.getIntegers().writeSafely(1, paramInt); // Team Mode
                container.getSpecificModifier(Collection.class).writeSafely(0, players);
                break;
            }
            case 12: {
                container.getStrings()
                        .writeSafely(0, name) // Team Name
                        .writeSafely(4, "always") // NameTag Visibility
                        .writeSafely(5, "always"); // Collision Rule
                container.getIntegers()
                        .writeSafely(0, -1) // Color
                        .writeSafely(1, paramInt); // Team Mode

                container.getSpecificModifier(Collection.class).writeSafely(0, players);
                break;
            }
            case 16: {
                container.getStrings()
                        .writeSafely(0, name) // Team Name
                        .writeSafely(1, "always") // NameTag Visibility
                        .writeSafely(2, "always"); // Collision Rule
                container.getChatComponents()
                        .writeSafely(0, WrappedChatComponent.fromLegacyText(""))
                        .writeSafely(1, WrappedChatComponent.fromLegacyText(""))
                        .writeSafely(2, WrappedChatComponent.fromLegacyText(""));
                container.getIntegers()
                        .writeSafely(0, paramInt); // Team mode
                container.getSpecificModifier(Collection.class).writeSafely(0, players);
                break;
            }
            default: {
                if (!VersionUtil.isModern()) {
                    throw new IllegalArgumentException("[NameTagAPI] Version not supported!");
                }
                container.getStrings().writeSafely(0, name); // Team Name
                final StructureModifier<Integer> integers = container.getIntegers();
                if (integers.size() > 1) {
                    integers.writeSafely(1, paramInt); // mode
                } else if (integers.size() > 0) {
                    integers.writeSafely(0, paramInt); // mode
                }
                container.getSpecificModifier(Collection.class).writeSafely(0, players);
                break;
            }
        }
    }

    public void sendToPlayer(Player bukkitPlayer) {
        ProtocolLibrary.getProtocolManager().sendServerPacket(bukkitPlayer, this.container);
    }
}
