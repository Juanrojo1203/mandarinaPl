package org.tomasa.mandarina.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.tomasa.mandarina.utils.VaultHolder;
import org.tomasa.mandarina.utils.VaultUtils;


public class VaultListener implements Listener {
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof VaultHolder) {
            Player player = (Player) event.getPlayer();
            VaultUtils.saveItems(player, event.getInventory().getContents());
        }
    }

}
