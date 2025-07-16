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
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        // Check if sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(messageutil.getColoredMessage("&cEste comando solo puede ser usado por jugadores."));
            return true;
        }

        Player player = (Player) sender;

        // If no arguments, show help
        if (args.length == 0) {
            player.sendMessage(messageutil.getColoredMessage("&cComandos de muerte:"));
            player.sendMessage(messageutil.getColoredMessage("&b - &6&l/muerte cord&r&c para ver tus ultimas coordenadas de muerte"));
            player.sendMessage(messageutil.getColoredMessage("&b - &6&l/muerte tp&r&c para teleportarte a tus ultimas coordenadas de muerte"));
            player.sendMessage(messageutil.getColoredMessage("&b - &6&l/muerte help&r&c para ver esta ayuda"));
            return true;
        }

        // Check if player has died before
        if (player.getLastDeathLocation() == null) {
            player.sendMessage(messageutil.getColoredMessage("&cNo has muerto todavia"));
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "cord":
            case "coords":
                // Show death coordinates
                player.sendMessage(messageutil.getColoredMessage("&fTus ultimas coordenadas de muerte son: &6X=" +
                    player.getLastDeathLocation().getBlockX() + " Y=" +
                    player.getLastDeathLocation().getBlockY() + " Z=" +
                    player.getLastDeathLocation().getBlockZ()));
                break;

            case "tp":
            case "teleport":
                // Teleport to death location
                player.teleport(player.getLastDeathLocation());
                player.sendMessage(messageutil.getColoredMessage("&fHas sido teletransportado a tu ultima ubicacion de muerte."));
                break;

            case "help":
                // Show help
                player.sendMessage(messageutil.getColoredMessage("&cComandos de muerte:"));
                player.sendMessage(messageutil.getColoredMessage("&b - &6&l/muerte cord&r&c para ver tus ultimas coordenadas de muerte"));
                player.sendMessage(messageutil.getColoredMessage("&b - &6&l/muerte tp&r&c para teleportarte a tus ultimas coordenadas de muerte"));
                break;

            default:
                player.sendMessage(messageutil.getColoredMessage("&cComando desconocido. Usa &6/muerte help&c para ver los comandos disponibles."));
                break;
        }

        return true;
    }
}
