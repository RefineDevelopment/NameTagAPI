package xyz.refinedev.api.nametag.util;

import com.google.common.collect.ImmutableMap;
import org.bukkit.ChatColor;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ColorUtil {

    public static Map<ChatColor, String> colorMap = new HashMap<>();

    static {
        colorMap.put(ChatColor.BLACK, "BLACK");
        colorMap.put(ChatColor.DARK_BLUE, "DARK_BLUE");
        colorMap.put(ChatColor.DARK_GREEN, "DARK_GREEN");
        colorMap.put(ChatColor.DARK_AQUA, "DARK_AQUA");
        colorMap.put(ChatColor.DARK_RED, "DARK_RED");
        colorMap.put(ChatColor.DARK_PURPLE, "DARK_PURPLE");
        colorMap.put(ChatColor.GOLD, "GOLD");
        colorMap.put(ChatColor.GRAY, "GRAY");
        colorMap.put(ChatColor.DARK_GRAY, "DARK_GRAY");
        colorMap.put(ChatColor.BLUE, "BLUE");
        colorMap.put(ChatColor.GREEN, "GREEN");
        colorMap.put(ChatColor.AQUA, "AQUA");
        colorMap.put(ChatColor.RED, "RED");
        colorMap.put(ChatColor.LIGHT_PURPLE, "LIGHT_PURPLE");
        colorMap.put(ChatColor.YELLOW, "YELLOW");
        colorMap.put(ChatColor.WHITE, "WHITE");
        colorMap.put(ChatColor.RESET, "RESET");
        colorMap.put(ChatColor.ITALIC, "ITALIC");
        colorMap.put(ChatColor.UNDERLINE, "UNDERLINE");
        colorMap.put(ChatColor.STRIKETHROUGH, "STRIKETHROUGH");
        colorMap.put(ChatColor.MAGIC, "MAGIC");
        colorMap.put(ChatColor.BOLD, "BOLD");
    }

    private static final Map<ChatColor, Color> COLOR_MAPPINGS = ImmutableMap.<ChatColor, Color>builder()
            .put(ChatColor.BLACK, new Color(0, 0, 0))
            .put(ChatColor.DARK_BLUE, new Color(0, 0, 170))
            .put(ChatColor.DARK_GREEN, new Color(0, 170, 0))
            .put(ChatColor.DARK_AQUA, new Color(0, 170, 170))
            .put(ChatColor.DARK_RED, new Color(170, 0, 0))
            .put(ChatColor.DARK_PURPLE, new Color(170, 0, 170))
            .put(ChatColor.GOLD, new Color(255, 170, 0))
            .put(ChatColor.GRAY, new Color(170, 170, 170))
            .put(ChatColor.DARK_GRAY, new Color(85, 85, 85))
            .put(ChatColor.BLUE, new Color(85, 85, 255))
            .put(ChatColor.GREEN, new Color(85, 255, 85))
            .put(ChatColor.AQUA, new Color(85, 255, 255))
            .put(ChatColor.RED, new Color(255, 85, 85))
            .put(ChatColor.LIGHT_PURPLE, new Color(255, 85, 255))
            .put(ChatColor.YELLOW, new Color(255, 255, 85))
            .put(ChatColor.WHITE, new Color(255, 255, 255))
            .build();


    public static ChatColor getClosestChatColor(Color color) {
        ChatColor closest = null;
        int mark = 0;
        for (Map.Entry<ChatColor, Color> entry : COLOR_MAPPINGS.entrySet()) {
            ChatColor key = entry.getKey();
            Color value = entry.getValue();

            int diff = getDiff(value, color);
            if (closest == null || diff < mark) {
                closest = key;
                mark = diff;
            }
        }

        return closest;
    }

    private static int getDiff(Color color, Color compare) {
        int a = color.getAlpha() - compare.getAlpha(),
                r = color.getRed() - compare.getRed(),
                g = color.getGreen() - compare.getGreen(),
                b = color.getBlue() - compare.getBlue();
        return a * a + r * r + g * g + b * b;
    }
    
    public static ChatColor convertToColor(String color) {
        if (isHexColor(color)) {
            String replacedColor = color
                    .replace("&", "")
                    .replace("x", "#")
                    .replace("§", "");

            return getClosestChatColor(Color.decode(replacedColor));
        } else {
            return ChatColor.getByChar(color
                    .replace(String.valueOf('§'), "")
                    .replace("&", "")
                    .replace("l", "")
                    .replace("n", "")
                    .replace("m", "")
                    .replace("o", "")
                    .replace("k", ""));

        }
    }

    public static String convertChatColor(ChatColor color) {
        return colorMap.get(color);
    }

    public static boolean isHexColor(String input) {
        if (!VersionUtil.canHex()) return false;

        input = input.replace("&", "").replace("x", "#").replace("§", "");
        try {
            net.md_5.bungee.api.ChatColor.of(input);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}