package xyz.refinedev.api.nametag.util;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.ChatColor;

import java.awt.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * <p>
 * This Project is property of Refine Development.<br>
 * Copyright © 2023, All Rights Reserved.<br>
 * Redistribution of this Project is not allowed.<br>
 * </p>
 * <br>
 * One to One copy of {@link ChatColor} because in old versions,
 * it's missing the ChatColor#getColor method, and currently I rely on that to convert it into
 * Adventure's {@link TextColor}.
 * <br>
 *
 * @author Drizzy
 * @version NameTagAPI
 * @since 11/3/2023
 */

@SuppressWarnings("unused")
public class TagColor {

    public static final char COLOR_CHAR = '§';
    public static final String ALL_CODES = "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx";
    public static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + '§' + "[0-9A-FK-ORX]");
    public static final TagColor BLACK = new TagColor('0', "black", new Color(0));
    public static final TagColor DARK_BLUE = new TagColor('1', "dark_blue", new Color(170));
    public static final TagColor DARK_GREEN = new TagColor('2', "dark_green", new Color(43520));
    public static final TagColor DARK_AQUA = new TagColor('3', "dark_aqua", new Color(43690));
    public static final TagColor DARK_RED = new TagColor('4', "dark_red", new Color(11141120));
    public static final TagColor DARK_PURPLE = new TagColor('5', "dark_purple", new Color(11141290));
    public static final TagColor GOLD = new TagColor('6', "gold", new Color(16755200));
    public static final TagColor GRAY = new TagColor('7', "gray", new Color(11184810));
    public static final TagColor DARK_GRAY = new TagColor('8', "dark_gray", new Color(5592405));
    public static final TagColor BLUE = new TagColor('9', "blue", new Color(5592575));
    public static final TagColor GREEN = new TagColor('a', "green", new Color(5635925));
    public static final TagColor AQUA = new TagColor('b', "aqua", new Color(5636095));
    public static final TagColor RED = new TagColor('c', "red", new Color(16733525));
    public static final TagColor LIGHT_PURPLE = new TagColor('d', "light_purple", new Color(16733695));
    public static final TagColor YELLOW = new TagColor('e', "yellow", new Color(16777045));
    public static final TagColor WHITE = new TagColor('f', "white", new Color(16777215));
    public static final TagColor MAGIC = new TagColor('k', "obfuscated");
    public static final TagColor BOLD = new TagColor('l', "bold");
    public static final TagColor STRIKETHROUGH = new TagColor('m', "strikethrough");
    public static final TagColor UNDERLINE = new TagColor('n', "underline");
    public static final TagColor ITALIC = new TagColor('o', "italic");
    public static final TagColor RESET = new TagColor('r', "reset");
    private static final Map<Character, TagColor> BY_CHAR = new HashMap<>();
    private static final Map<String, TagColor> BY_NAME = new HashMap<>();
    private static int count = 0;
    private final String toString;
    private final String name;
    private final int ordinal;
    private final Color color;

    private TagColor(char code, String name) {
        this(code, name, null);
    }

    private TagColor(char code, String name, Color color) {
        this.name = name;
        this.toString = new String(new char[]{'§', code});
        this.ordinal = count++;
        this.color = color;
        BY_CHAR.put(code, this);
        BY_NAME.put(name.toUpperCase(Locale.ROOT), this);
    }

    private TagColor(String name, String toString, int rgb) {
        this.name = name;
        this.toString = toString;
        this.ordinal = -1;
        this.color = new Color(rgb);
    }

    public static String stripColor(String input) {
        return input == null ? null : STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
    }

    public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
        char[] b = textToTranslate.toCharArray();

        for ( int i = 0; i < b.length - 1; ++i ) {
            if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx".indexOf(b[i + 1]) > -1) {
                b[i] = 167;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }

        return new String(b);
    }

    public static TagColor getByChar(String code) {
        Preconditions.checkArgument(code != null, "Code cannot be null");
        Preconditions.checkArgument(code.length() > 0, "Code must have at least one char");
        return BY_CHAR.get(code.charAt(0));
    }

    public static TagColor getByChar(char code) {
        return BY_CHAR.get(code);
    }

    public static TagColor of(Color color) {
        return of("#" + String.format("%08x", color.getRGB()).substring(2));
    }

    public static TagColor of(String string) {
        Preconditions.checkArgument(string != null, "string cannot be null");
        if (string.startsWith("#") && string.length() == 7) {
            int rgb;
            try {
                rgb = Integer.parseInt(string.substring(1), 16);
            } catch (NumberFormatException var7) {
                throw new IllegalArgumentException("Illegal hex string " + string);
            }

            StringBuilder magic = new StringBuilder("§x");
            char[] chars = string.substring(1).toCharArray();
            for ( char c : chars ) {
                magic.append('§').append(c);
            }

            return new TagColor(string, magic.toString(), rgb);
        } else {
            TagColor defined = BY_NAME.get(string.toUpperCase(Locale.ROOT));
            if (defined != null) {
                return defined;
            } else {
                throw new IllegalArgumentException("Could not parse TagColor " + string);
            }
        }
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static TagColor valueOf(String name) {
        Preconditions.checkNotNull(name, "Name is null");
        TagColor defined = BY_NAME.get(name);
        Preconditions.checkArgument(defined != null, "No enum constant " + TagColor.class.getName() + "." + name);
        return defined;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static TagColor[] values() {
        return BY_CHAR.values().toArray(new TagColor[0]);
    }

    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.toString);
        return hash;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj != null && this.getClass() == obj.getClass()) {
            TagColor other = (TagColor) obj;
            return Objects.equals(this.toString, other.toString);
        } else {
            return false;
        }
    }

    public String toString() {
        return this.toString;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public String name() {
        return this.getName().toUpperCase(Locale.ROOT);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public int ordinal() {
        Preconditions.checkArgument(this.ordinal >= 0, "Cannot get ordinal of hex color");
        return this.ordinal;
    }

    public String getName() {
        return this.name;
    }

    public Color getColor() {
        return this.color;
    }
}
