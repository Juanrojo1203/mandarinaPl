package org.tomasa.mandarina.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
public class tpCommand implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                player.sendMessage("Usage: /tp <player>");
                return true;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null && target.isOnline()) {
                player.teleport(target);
                player.sendMessage("Teleported to " + target.getName());
            } else if(!(target.isOnline())){
                player.sendMessage("Player is offline.");
            } else {
                player.sendMessage("Player does'nt found.");
            }
        } else {
            sender.sendMessage("This command can only be used by players.");
        }
        return true;
    }
    
}
