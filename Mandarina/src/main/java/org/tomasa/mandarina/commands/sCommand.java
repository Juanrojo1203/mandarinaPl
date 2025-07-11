package org.tomasa.mandarina.commands;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class sCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser usado por jugadores.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("Uso: /s <jugador>");
            return true;
        }

        Player player = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage("El jugador no existe o está desconectado.");
            return true;
        }else if (target == player) {
            sender.sendMessage("No puedes traerte a ti mismo.");
            return true;
        }else if (!target.isOnline()) {
            sender.sendMessage("El jugador no está en línea.");
            return true;
        }
        if (args.length == 1) {
            sender.sendMessage("el jugador " + target.getName() + " ha sido teletransportado a ti.");
            target.teleport(player.getLocation());
            player.sendMessage("has sido teletransportado a " + target.getName() + ".");
            return true;
        }else{
            sender.sendMessage("El comando tiene la siguiente sintaxis: /s <jugador>");
        }


        return true;
    }
}
