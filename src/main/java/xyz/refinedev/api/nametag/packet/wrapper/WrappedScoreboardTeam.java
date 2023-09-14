package xyz.refinedev.api.nametag.packet.wrapper;

import com.comphenix.protocol.reflect.ExactReflection;
import org.bukkit.ChatColor;

import com.comphenix.protocol.reflect.accessors.Accessors;
import com.comphenix.protocol.reflect.accessors.FieldAccessor;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.AbstractWrapper;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.IndexedEnumConverter;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

/**
 * Taken from GitHub: <a href="https://github.com/dmulloy2/ProtocolLib/issues/1388">Click Here</a>
 */
public class WrappedScoreboardTeam extends AbstractWrapper {

    private static final FieldAccessor DISPLAY_NAME = Accessors
            .getFieldAccessor(ExactReflection.fromClass(MinecraftReflection.getMinecraftClass("network.protocol.game.PacketPlayOutScoreboardTeam$b"), true).findField("a"));
    private static final FieldAccessor PREFIX = Accessors
        .getFieldAccessor(ExactReflection.fromClass(MinecraftReflection.getMinecraftClass("network.protocol.game.PacketPlayOutScoreboardTeam$b"), true).findField("b"));
    private static final FieldAccessor SUFFIX = Accessors
            .getFieldAccessor(ExactReflection.fromClass(MinecraftReflection.getMinecraftClass("network.protocol.game.PacketPlayOutScoreboardTeam$b"), true).findField("c"));

    private static final FieldAccessor NAME_TAG_VISIBILITY = Accessors
            .getFieldAccessor(ExactReflection.fromClass(MinecraftReflection.getMinecraftClass("network.protocol.game.PacketPlayOutScoreboardTeam$b"), true).findField("d"));
    private static final FieldAccessor COLLISION_RULE = Accessors
            .getFieldAccessor(ExactReflection.fromClass(MinecraftReflection.getMinecraftClass("network.protocol.game.PacketPlayOutScoreboardTeam$b"), true).findField("e"));
    private static final FieldAccessor TEAM_COLOR = Accessors
            .getFieldAccessor(ExactReflection.fromClass(MinecraftReflection.getMinecraftClass("network.protocol.game.PacketPlayOutScoreboardTeam$b"), true).findField("f"));
    private static final FieldAccessor FRIENDLY_FLAGS = Accessors
            .getFieldAccessor(ExactReflection.fromClass(MinecraftReflection.getMinecraftClass("network.protocol.game.PacketPlayOutScoreboardTeam$b"), true).findField("g"));

    private static final Class<?> ENUM_CHAT_FORMAT_CLASS = MinecraftReflection.getMinecraftClass("EnumChatFormat");
    private static final IndexedEnumConverter<ChatColor> CHATCOLOR_CONVERTER = new EnumWrappers.IndexedEnumConverter<>(ChatColor.class, ENUM_CHAT_FORMAT_CLASS);

    private WrappedScoreboardTeam(Object handle) {
        super(MinecraftReflection.getMinecraftClass("network.protocol.game.PacketPlayOutScoreboardTeam$b"));
        setHandle(handle);
    }

    public static WrappedScoreboardTeam fromHandle(Object handle) {
        return new WrappedScoreboardTeam(handle);
    }

    public WrappedChatComponent getDisplayName() {
        return WrappedChatComponent.fromHandle(DISPLAY_NAME.get(this.handle));
    }

    public void setDisplayName(WrappedChatComponent displayName) {
        DISPLAY_NAME.set(this.handle, displayName.getHandle());
    }

    public WrappedChatComponent getPrefix() {
        return WrappedChatComponent.fromHandle(PREFIX.get(this.handle));
    }

    public void setPrefix(WrappedChatComponent prefix) {
        PREFIX.set(this.handle, prefix.getHandle());
    }

    public WrappedChatComponent getSuffix() {
        return WrappedChatComponent.fromHandle(SUFFIX.get(this.handle));
    }

    public void setSuffix(WrappedChatComponent suffix) {
        SUFFIX.set(this.handle, suffix.getHandle());
    }

    public String getNameTagVisibility() {
        return (String) NAME_TAG_VISIBILITY.get(this.handle);
    }

    public void setNameTagVisibility(String value) {
        NAME_TAG_VISIBILITY.set(this.handle, value);
    }

    public String getCollisionRule() {
        return (String) COLLISION_RULE.get(this.handle);
    }

    public void setCollisionRule(String value) {
        COLLISION_RULE.set(this.handle, value);
    }

    public ChatColor getTeamColor() {
        return ChatColor.getByChar(TEAM_COLOR.get(this.handle).toString().charAt(1));
    }

    public void setTeamColor(ChatColor value) {
        if (value.isColor() || value == ChatColor.RESET) {
            TEAM_COLOR.set(this.handle, CHATCOLOR_CONVERTER.getGeneric(value));
        }
    }

    public boolean getFriendlyFire() {
        int intValue = ((Integer) FRIENDLY_FLAGS.get(this.handle)).intValue();
        return (intValue & 0x01) == 1;
    }

    public void setFriendlyFire(boolean value) {
        int currentValue = ((Integer) FRIENDLY_FLAGS.get(this.handle)).intValue();
        if (getFriendlyFire() && !value) {
            // is already friendly fire but should not be
            FRIENDLY_FLAGS.set(this.handle, Integer.valueOf(currentValue ^ 0x01));
        }
        else if (value) {
            // is already friendly fire but should not be
            FRIENDLY_FLAGS.set(this.handle, Integer.valueOf(currentValue | 0x01));
        }
    }

    public boolean getFriendlySeeInvisible() {
        int intValue = ((Integer) FRIENDLY_FLAGS.get(this.handle)).intValue();
        return (intValue & 0x02) == 2;
    }

    public void setFriendlySeeInvisible(boolean value) {
        int currentValue = ((Integer) FRIENDLY_FLAGS.get(this.handle)).intValue();
        if (getFriendlyFire() && !value) {
            // is already friendly fire but should not be
            FRIENDLY_FLAGS.set(this.handle, Integer.valueOf(currentValue ^ 0x02));
        }
        else if (value) {
            // is already friendly fire but should not be
            FRIENDLY_FLAGS.set(this.handle, Integer.valueOf(currentValue | 0x02));
        }
    }
}