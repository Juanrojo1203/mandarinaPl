package commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.tomasa.mandarina.Mandarina;
import utils.messageutil;

public class InfoCommand implements CommandExecutor {
    Mandarina mandarina = new Mandarina();
    @Override
    public boolean onCommand(@NotNull CommandSender sender,@NotNull Command command,@NotNull String label,@NotNull String @NotNull [] args) {
        Player player = (Player) sender;

        if (args.length >= 1) {
            player.sendMessage(messageutil.getColoredMessage(mandarina.prefix + " &c La version es: " + mandarina.version+"\n &c Desarrollado por: &6 Tomasa"));
            return true;
    }
        return false;
    }
}
