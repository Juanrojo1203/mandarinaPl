package org.tomasa.mandarina.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.tomasa.mandarina.utils.messageutil;

public class deathCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender Sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        
        if (!(Sender instanceof Player)) {
            Sender.sendMessage(messageutil.getColoredMessage("&cEste comando solo puede ser usado por jugadores."));
            return true;
        }

        Player player = (Player) Sender;


        String subcommand = args[0];

        if (player.getLastDeathLocation() == null) {
            player.sendMessage(messageutil.getColoredMessage("&cNo has muerto todavía"));
            return true;
        }

        switch (subcommand) {
            case "cord":
                player.sendMessage("Tus últimas coordenadas de muerte son: " + 
                    "X=" + player.getLastDeathLocation().getBlockX() + 
                    " Y=" + player.getLastDeathLocation().getBlockY() + 
                    " Z=" + player.getLastDeathLocation().getBlockZ());
                break;
                
            case "tp":
                player.teleport(player.getLastDeathLocation());
                player.sendMessage("Te has teletransportado a tus últimas coordenadas de muerte");
                break;
        
            case "help":
                player.sendMessage(messageutil.getColoredMessage("&cComandos de muerte:"));
                player.sendMessage(messageutil.getColoredMessage("&b - &6&l/muerte cord&r&c para ver tus últimas coordenadas de muerte"));
                player.sendMessage(messageutil.getColoredMessage("&b - &6&l/muerte tp&r&c para teleportarte a tus últimas coordenadas de muerte"));
                break;

            default:
                player.sendMessage(messageutil.getColoredMessage("&cComando no reconocido. Usa /muerte help para ver los comandos disponibles."));
                break;
        }

        return true;
    }
}