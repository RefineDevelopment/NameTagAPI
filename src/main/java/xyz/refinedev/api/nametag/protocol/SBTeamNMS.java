package xyz.refinedev.api.nametag.protocol;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Collection;

/**
 * This Project is the property of Phoenix Development © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Creaxx
 * Created At: 10/22/2022
 * Project: NameTagAPI
 */

@RequiredArgsConstructor
public abstract class SBTeamNMS {

    public abstract SBTeamNMS create(String name, String prefix, String suffix, Collection<String> players, int paramInt);

    public abstract SBTeamNMS create(String name, Collection<String> players, int paramInt);

    public abstract void sendToPlayer(Player bukkitPlayer);

    public abstract void setField(String field, Object value);

    public abstract void addAll(Collection col);
}
