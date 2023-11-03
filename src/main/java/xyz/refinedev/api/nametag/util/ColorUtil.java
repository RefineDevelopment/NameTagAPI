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
        TagColor color = getLastColors(input);
        NamedTextColor textColor = NamedTextColor.WHITE;

        if (color == null) return textColor;

        // I couldn't really find a direct conversion, but in case of bungee's color, it provides java color which be used to convert to adventure's color
        TextColor parsed = TextColor.color(color.getColor().getRed(), color.getColor().getGreen(), color.getColor().getBlue());
        return NamedTextColor.nearestTo(parsed);
    }

    /**
     * Get last colors from a string in form of {@link TagColor}
     *
     * @param input {@link String} The string from which we extract color
     * @return      {@link TagColor}
     */
    public TagColor getLastColors(String input) {
        String prefixColor = ChatColor.getLastColors(color(input));

        if (prefixColor.isEmpty()) return null;

        TagColor color;

        // Hex Color Support
        if (VersionUtil.canHex()) {
            try {
                // Convert bukkit's &x based hex color to something bungee color can read (#FFFFFF)
                String hexColor = prefixColor.replace("§", "").replace("x", "#");
                color = TagColor.of(hexColor); // Parse into a Bungee Color
            } catch (Exception e) {
                // If the color is not a hex color, then it's a normal color code
                val bukkitColor = TagColor.getByChar(prefixColor.substring(prefixColor.length() - 1));
                if (bukkitColor == null) {
                    return null;
                }
                color = bukkitColor;
            }
        } else {
            // Obviously in older versions, hex color does not exist, so we just parse it normally
            val bukkitColor = TagColor.getByChar(prefixColor.substring(prefixColor.length() - 1));
            if (bukkitColor == null) {
                return null;
            }
            color = bukkitColor;
        }
        return color;
    }

    /**
     * Translate '&' based color codes into bukkit ones
     *
     * @param text {@link String} Input Text
     * @return     {@link String} Output Text (with HexColor Support)
     */
    public String color(String text) {
        if (text == null) return "";

        text = TagColor.translateAlternateColorCodes('&', text);

        // Credits: Creaxx
        if (VersionUtil.canHex()) {
            Matcher matcher = hexPattern.matcher(text);
            while (matcher.find()) {
                try {
                    String color = matcher.group();
                    val bungeeColor = TagColor.of(color.replace("&", ""));
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