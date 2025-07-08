package org.tomasa.mandarina.utils;
import org.bukkit.ChatColor;
public class messageutil {

    public  static  String getColoredMessage(String message){
        return ChatColor.translateAlternateColorCodes('&',message);
    }

}

