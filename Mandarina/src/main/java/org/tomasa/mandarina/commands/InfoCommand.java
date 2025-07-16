package org.tomasa.mandarina.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.tomasa.mandarina.Mandarina;
import org.tomasa.mandarina.utils.messageutil;

public class InfoCommand implements CommandExecutor {
    private final Mandarina plugin;

    public InfoCommand(Mandarina plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,@NotNull Command command,@NotNull String label,@NotNull String @NotNull [] args) {

        Player player = (Player) sender;
        player.sendMessage(messageutil.getColoredMessage(plugin.prefix + " &fLa version es: &b" + plugin.version));
        player.sendMessage(messageutil.getColoredMessage(plugin.prefix + " &fDesarrollado por: &6Tomasa"));

        return true;
    }
}
