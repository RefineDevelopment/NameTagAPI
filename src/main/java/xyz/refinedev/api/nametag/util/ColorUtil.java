package xyz.refinedev.api.nametag.util;

import com.github.retrooper.packetevents.util.adventure.AdventureSerializer;

import lombok.experimental.UtilityClass;
import lombok.val;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class ColorUtil {

    private static final Pattern hexPattern = Pattern.compile("&#[A-Fa-f0-9]{6}");

    /**
     * Get last valid {@link NamedTextColor} from a string
     *
     * @param input {@link String} The string from which we extract color
     * @return      {@link NamedTextColor} Last Color or WHITE if none is present.
     */
    public NamedTextColor getLastColor(String input) {
        String prefixColor = ChatColor.getLastColors(color(input));
        NamedTextColor textColor = NamedTextColor.WHITE;

        if (prefixColor.isEmpty()) return textColor;

        net.md_5.bungee.api.ChatColor color;

        // Hex Color Support
        if (VersionUtil.canHex()) {
            try {
                // Convert bukkit's &x based hex color to something bungee color can read (#FFFFFF)
                String hexColor = prefixColor.replace("§", "").replace("x", "#");
                color = net.md_5.bungee.api.ChatColor.of(hexColor); // Parse into a Bungee Color
            } catch (Exception e) {
                // If the color is not a hex color, then it's a normal color code
                val bukkitColor = ChatColor.getByChar(prefixColor.substring(prefixColor.length() - 1));
                if (bukkitColor == null) {
                    return textColor;
                }
                color = bukkitColor.asBungee();
            }
        } else {
            // Obviously in older versions, hex color does not exist, so we just parse it normally
            val bukkitColor = ChatColor.getByChar(prefixColor.substring(prefixColor.length() - 1));
            if (bukkitColor == null) {
                return textColor;
            }
            color = bukkitColor.asBungee();
        }

        // I couldn't really find a direct conversion, but in case of bungee's color, it provides java color which be used to convert to adventure's color
        TextColor parsed = TextColor.color(color.getColor().getRed(), color.getColor().getGreen(), color.getColor().getBlue());
        return NamedTextColor.nearestTo(parsed);
    }

    /**
     * Translate '&' based color codes into bukkit ones
     *
     * @param text {@link String} Input Text
     * @return     {@link String} Output Text (with HexColor Support)
     */
    public String color(String text) {
        if (text == null) return "";

        text = ChatColor.translateAlternateColorCodes('&', text);

        // Credits: Creaxx
        if (VersionUtil.canHex()) {
            Matcher matcher = hexPattern.matcher(text);
            while (matcher.find()) {
                try {
                    String color = matcher.group();
                    val bungeeColor = net.md_5.bungee.api.ChatColor.of(color.replace("&", ""));
                    text = text.replace(color, bungeeColor.toString());
                } catch (Exception ignored) {
                    // Errors about unknown group, can be safely ignored!
                }
            }
        }

        return text;
    }

    /**
     * Converts a simple string to a {@link Component}
     *
     * @param string {@link String}
     * @return       {@link Component}
     */
    public Component translate(String string) {
        return AdventureSerializer.fromLegacyFormat(color(string));
    }
}