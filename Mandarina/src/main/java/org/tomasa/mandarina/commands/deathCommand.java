package org.tomasa.mandarina.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.tomasa.mandarina.utils.messageutil;

public class deathCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender Sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        Player player =(Player) Sender;
        if (args.length >= 1) {
            //Mensaje de prueba
            player.sendMessage(messageutil.getColoredMessage("pruebapruebaprueba"));
            //Mensaje de que el getLastDeathLocation es null
            if(player.getLastDeathLocation()==null){
                player.sendMessage(messageutil.getColoredMessage("&cNo has muerto todavia"));

            }


            // /muerte cord
            //comando de coordenadas de ultima muerte
            else if(args[0].equalsIgnoreCase("cord")) {
                //manda en el chat las ultimas coordenadas de muerte
                player.sendMessage("Tus ultimas coordenadas de muerte son: " + "X=" + player.getLastDeathLocation().getBlockX() + " Y=" + player.getLastDeathLocation().getBlockY() + " Z=" + player.getLastDeathLocation().getBlockZ());
            }


            // /muerte tp
            //comando de teleportacion hacia las ultimas coordenadas de muerte
            else if(args[0].equalsIgnoreCase("tp")){
                //teleporta al jugador a las ultimas coordenadas de muerte
                player.teleport(player.getLastDeathLocation());
            }


            //muerte help
            //conmando de ayuda
            else if (args[0].equalsIgnoreCase("help")){
            player.sendMessage(messageutil.getColoredMessage("&cComandos de muerte:"));
            player.sendMessage(messageutil.getColoredMessage("&b - &6&l/muerte cord&r&c para ver tus ultimas coordenadas de muerte"));
            player.sendMessage(messageutil.getColoredMessage("&b - &6&l/muerte tp&r&c para teleportarte a tus ultimas coordenadas de muerte"));


            return true;
        }

    return false;
    }
        return false;
}
}
