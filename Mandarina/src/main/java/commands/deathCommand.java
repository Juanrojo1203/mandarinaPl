package commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class deathCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender Sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        Player player =(Player) Sender;
        if (args.length >= 1) {
            if(args[0]=="cord") {
                //manda en el chat las ultimas coordenadas de muerte
                player.sendMessage("Tus ultimas coordenadas de muerte son: " + "X=" + player.getLastDeathLocation().getBlockX() + " Y=" + player.getLastDeathLocation().getBlockY() + " Z=" + player.getLastDeathLocation().getBlockZ());
            }else if(args[0]=="tp"){
                //teleporta al jugador a las ultimas coordenadas de muerte
                player.teleport(player.getLastDeathLocation());
            }
            return true;
        }

    return false;
    }
}
