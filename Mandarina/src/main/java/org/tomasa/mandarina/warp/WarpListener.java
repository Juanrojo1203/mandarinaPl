package org.tomasa.mandarina.warp;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.world.WorldSaveEvent;

/**
 * Listener for warp-related events to ensure data persistence.
 * Handles player disconnections and server events to save warp data.
 */
public class WarpListener implements Listener {
    private final WarpManager warpManager;
    private final ShareRequestManager shareRequestManager;

    public WarpListener(WarpManager warpManager, ShareRequestManager shareRequestManager) {
        this.warpManager = warpManager;
        this.shareRequestManager = shareRequestManager;
    }

    /**
     * Handles player disconnection to save their warp data and cancel any pending share requests.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Save player's warp data
        warpManager.savePlayerWarps(event.getPlayer());
        
        // Cancel any pending share requests for this player
        shareRequestManager.cancelRequest(event.getPlayer().getUniqueId());
    }

    /**
     * Handles world save events to ensure warp data is also saved.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldSave(WorldSaveEvent event) {
        // Save warps when the world saves
        warpManager.saveWarps();
    }

    /**
     * Handles server commands that might indicate a restart/shutdown.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onServerCommand(ServerCommandEvent event) {
        String command = event.getCommand().toLowerCase();
        
        // Check for common restart/stop commands
        if (command.equals("stop") || command.equals("restart") || 
            command.startsWith("stop ") || command.startsWith("restart ")) {
            
            // Force save all warp data
            warpManager.forceSave();
        }
    }
}
