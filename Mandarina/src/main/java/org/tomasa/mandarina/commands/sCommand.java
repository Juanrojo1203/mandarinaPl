package org.tomasa.mandarina.commands;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.tomasa.mandarina.utils.messageutil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class sCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        Player player = (Player) sender;
        // Check if sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(messageutil.getColoredMessage("&cEste comando solo puede ser usado por jugadores."));
            return true;
        }

        // Check if command has correct number of arguments
        if (args.length != 1) {
            sender.sendMessage(messageutil.getColoredMessage("&cEl comando tiene la siguiente sintaxis: /s <jugador>"));
            return true;
        }

        Player player = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);

        // Check if target player exists
        if (target == null) {
            sender.sendMessage(messageutil.getColoredMessage("&cEl jugador no existe o está desconectado."));
            return true;
        }

        // Check if target is the same as sender
        if (target == player) {
            sender.sendMessage(messageutil.getColoredMessage("&cNo puedes traerte a ti mismo."));
            return true;
        }

        // Check if target is online
        if (!target.isOnline()) {
            sender.sendMessage(messageutil.getColoredMessage("&cEl jugador no está en línea."));
            return true;
        }

        // Teleport target to player
        target.teleport(player.getLocation());

        // Send success messages
        sender.sendMessage(messageutil.getColoredMessage("&fEl jugador &b&l" + target.getName() + "&r&f ha sido teletransportado a ti."));
        target.sendMessage(messageutil.getColoredMessage("&fHas sido teletransportado a &b&l" + player.getName() + "&r&f."));

        return true;
    }
}
