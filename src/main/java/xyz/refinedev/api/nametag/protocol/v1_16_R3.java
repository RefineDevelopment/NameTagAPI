package xyz.refinedev.api.nametag.protocol;

import net.minecraft.server.v1_16_R3.PacketPlayOutScoreboardTeam;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This Project is the property of Phoenix Development © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Creaxx
 * Created At: 10/22/2022
 * Project: NameTagAPI
 */

public class v1_16_R3 extends SBTeamNMS {
    PacketPlayOutScoreboardTeam packet;

    @Override
    public SBTeamNMS create(String name, String prefix, String suffix, Collection<String> players, int paramInt) {
        packet = new PacketPlayOutScoreboardTeam();

        setField("a", name);
        setField("h", paramInt);

        if (paramInt == 0 || paramInt == 2) {
            setField("b", name);
            setField("c", prefix);
            setField("d", suffix);
            setField("i", 1);
        }

        if (paramInt == 0) addAll(players);
        return this;
    }

    @Override
    public SBTeamNMS create(String name, Collection<String> players, int paramInt) {
        packet = new PacketPlayOutScoreboardTeam();

        if (players == null) players = new ArrayList<>();

        setField("a", name);
        setField("h", paramInt);
        addAll(players);
        return this;
    }

    @Override
    public void sendToPlayer(Player bukkitPlayer) {
        ((CraftPlayer) bukkitPlayer).getHandle().playerConnection.sendPacket(packet);
    }

    @Override
    public void setField(String field, Object value) {
        try {
            Field fieldObject = packet.getClass().getDeclaredField(field);

            fieldObject.setAccessible(true);
            fieldObject.set(packet, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addAll(Collection col) {
        try {
            Field fieldObject = packet.getClass().getDeclaredField("g");

            fieldObject.setAccessible(true);
            ((Collection) fieldObject.get(packet)).addAll(col);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
