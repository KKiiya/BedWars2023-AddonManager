package me.kiiya.addonmanager.utils;

import org.bukkit.ChatColor;
import java.util.List;
import java.util.stream.Collectors;

public class Utility {
    public static String c(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static List<String> cList(List<String> list) {
        return list.stream().map(text -> ChatColor.translateAlternateColorCodes('&', text)).collect(Collectors.toList());
    }
}
