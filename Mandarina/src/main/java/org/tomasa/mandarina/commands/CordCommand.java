package org.tomasa.mandarina.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.tomasa.mandarina.utils.messageutil;

public class CordCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        Player player = (Player) sender;
        if (sender instanceof Player) {
        player.sendMessage(messageutil.getColoredMessage("&cTus coordenadas son: &r&6&lX=" + player.getLocation().getBlockX() + " Y=" + player.getLocation().getBlockY() + " Z=" + player.getLocation().getBlockZ()));

        return false;
    }
        return false;
    }
}
