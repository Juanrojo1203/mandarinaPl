package org.tomasa.mandarina.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.tomasa.mandarina.Mandarina;
import org.tomasa.mandarina.utils.VaultHolder;
import org.tomasa.mandarina.utils.VaultUtils;

public class PlayerVaultCommand implements CommandExecutor {
    private final Mandarina plugin;
    public PlayerVaultCommand(Mandarina plugin){
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@org.jetbrains.annotations.NotNull CommandSender sender, @org.jetbrains.annotations.NotNull Command command, @org.jetbrains.annotations.NotNull String label, @org.jetbrains.annotations.NotNull String @org.jetbrains.annotations.NotNull [] args) {
        int vaultSize = 54;
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo lo puede ejecutar un jugador.");
            return true;
        }

        Player player = (Player) sender;

        Inventory vault = Bukkit.createInventory(new VaultHolder(), vaultSize, "Tu botso");

        var items = VaultUtils.getItems(player);
        for (int i = 0; i < items.size(); i++) {
            vault.setItem(i, items.get(i));
        }

        player.openInventory(vault);

        return true;
    }
}
