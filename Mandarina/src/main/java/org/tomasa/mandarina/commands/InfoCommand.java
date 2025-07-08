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

        if (sender instanceof Player) {
            player.sendMessage(messageutil.getColoredMessage(plugin.prefix + " &c La version es: " + plugin.version+"\n &c Desarrollado por: &6 Tomasa"));
            return true;
    }
        return false;
    }
}
