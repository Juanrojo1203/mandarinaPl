package org.tomasa.mandarina.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
public class tpCommand implements CommandExecutor{

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Check if sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        if(args.length == 0 || args[0].equalsIgnoreCase(player.getName())){
            player.sendMessage("You can't teleport to yourself.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Usage: /tp <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage("Player not found.");
            return true;
        }

        if (!target.isOnline()) {
            player.sendMessage("Player is offline.");
            return true;
        }

        player.teleport(target);
        player.sendMessage("Teleported to " + target.getName());

        return true;
    }
    
}
