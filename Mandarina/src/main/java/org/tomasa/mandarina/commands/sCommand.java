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
        // if (!(sender instanceof Player)) {
        //     sender.sendMessage("Este comando solo puede ser usado por jugadores.");
        //     return true;
        // }

        if (args.length == 0) {
            sender.sendMessage(messageutil.getColoredMessage("&cEl comando tiene la siguiente sintaxis: /s <jugador>"));
            return true;
        }

        Player player = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(messageutil.getColoredMessage("&cEl jugador no existe o está desconectado."));
            return true;
        }else if (target == player) {
            sender.sendMessage(messageutil.getColoredMessage("&cNo puedes traerte a ti mismo."));
            return true;
        }else if (!target.isOnline()) {
            sender.sendMessage(messageutil.getColoredMessage("&cEl jugador no está en línea."));
            return true;
        }
        if (args.length == 1) {
            sender.sendMessage(messageutil.getColoredMessage("&fEl jugador &b&l" + target.getName() + "&r&f ha sido teletransportado a ti."));
            target.teleport(player.getLocation());
            player.sendMessage(messageutil.getColoredMessage("&fhas sido teletransportado a&b&l " + target.getName() + "&f&r."));
            return true;
        }else{
            sender.sendMessage(messageutil.getColoredMessage("&cEl comando tiene la siguiente sintaxis: /s <jugador>"));
        }


        return true;
    }
}
