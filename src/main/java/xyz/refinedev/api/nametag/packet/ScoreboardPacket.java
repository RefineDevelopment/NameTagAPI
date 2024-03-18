package xyz.refinedev.api.nametag.packet;

import lombok.Getter;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import xyz.refinedev.api.nametag.util.ColorUtil;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
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

    // 0 - create
    // 1 - remove
    // 2 - update
    // 3 - add entities
    // 4 - remove entities

    public static Object creationPacket(String name, String prefix, String suffix) {
        if (prefix.length() > 16) {
            prefix = prefix.substring(0, 16);
        }
        if (suffix.length() > 16) {
            suffix = suffix.substring(0, 16);
        }

        PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();

        try {
            protocolValue.invokeExact(packet, 0);

            teamName.invokeExact(packet, name);
            teamDisplayName.invokeExact(packet, name);
            teamPrefix.invokeExact(packet, ColorUtil.color(prefix));
            teamSuffix.invokeExact(packet, ColorUtil.color(suffix));
            nameTagRule.invokeExact(packet, "always");
            optionData.invokeExact(packet, 1);
        } catch (Throwable e) {
            Bukkit.getLogger().info("[NameTagAPI] Failed to create packet for removing!");
            e.printStackTrace();
        }

        return packet;
    }

    public static Object additionPacket(String name, Collection<String> players) {
        PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();

        try {
            teamName.invokeExact(packet, name);
            protocolValue.invokeExact(packet, 3);
            playerNames.invokeExact(packet, players);
        } catch (Throwable e) {
            Bukkit.getLogger().info("[NameTagAPI] Failed to create packet for removing!");
            e.printStackTrace();
        }

        return packet;
    }

    public static Object removalPacket(String name) {
        PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();

        try {
            teamName.invokeExact(packet, name);
            protocolValue.invoke(packet, 1);
        } catch (Throwable e) {
            Bukkit.getLogger().info("[NameTagAPI] Failed to create packet for removing!");
            e.printStackTrace();
        }

        return packet;
    }

    public static void deliverPacket(Player target, Object object) {
        if (!(object instanceof Packet)) {
            throw new IllegalStateException("[NameTagAPI] Tried to send an invalid object as packet!");
        }

        EntityPlayer entityPlayer = ((CraftPlayer)target).getHandle();
        entityPlayer.playerConnection.sendPacket((Packet<?>) object);
    }

    private static final boolean SUPPORTED;
    private static final Class<?> PACKET_CLASS;
    private static final MethodHandle protocolValue;
    private static final MethodHandle teamName, teamDisplayName, teamPrefix, teamSuffix;
    private static final MethodHandle nameTagRule, optionData;
    private static final MethodHandle playerNames;

    static {
        try {
            PACKET_CLASS = PacketPlayOutScoreboardTeam.class;

            MethodHandles.Lookup lookup = MethodHandles.publicLookup();

            Field name = PACKET_CLASS.getDeclaredField("a");
            name.setAccessible(true);

            Field displayName = PACKET_CLASS.getDeclaredField("b");
            displayName.setAccessible(true);

            Field prefix = PACKET_CLASS.getDeclaredField("c");
            prefix.setAccessible(true);

            Field suffix = PACKET_CLASS.getDeclaredField("d");
            suffix.setAccessible(true);

            Field nameTag = PACKET_CLASS.getDeclaredField("e");
            nameTag.setAccessible(true);

            Field protocol = PACKET_CLASS.getDeclaredField("h");
            protocol.setAccessible(true);

            Field list = PACKET_CLASS.getDeclaredField("g");
            list.setAccessible(true);

            Field data = PACKET_CLASS.getDeclaredField("i");
            data.setAccessible(true);

            teamName = lookup.unreflectSetter(name);
            teamDisplayName = lookup.unreflectSetter(displayName);
            teamPrefix = lookup.unreflectSetter(prefix);
            teamSuffix = lookup.unreflectSetter(suffix);
            nameTagRule = lookup.unreflectSetter(nameTag);
            protocolValue = lookup.unreflectSetter(protocol);
            playerNames = lookup.unreflectSetter(list);
            optionData = lookup.unreflectSetter(data);

            SUPPORTED = true;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
