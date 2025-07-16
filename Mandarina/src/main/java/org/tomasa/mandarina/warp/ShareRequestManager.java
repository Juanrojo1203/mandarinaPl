package org.tomasa.mandarina.warp;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.tomasa.mandarina.Mandarina;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages pending warp share requests between players.
 * Handles the 10-second timeout mechanism and acceptance logic.
 */
public class ShareRequestManager {
    private final Mandarina plugin;
    private final WarpManager warpManager;
    private final Map<UUID, ShareRequest> pendingRequests;

    public ShareRequestManager(Mandarina plugin, WarpManager warpManager) {
        this.plugin = plugin;
        this.warpManager = warpManager;
        this.pendingRequests = new HashMap<>();
    }

    /**
     * Creates a new share request from one player to another.
     * 
     * @param sender The player sharing the warp
     * @param target The player receiving the share request
     * @param warpName The name of the warp being shared
     */
    public void createShareRequest(Player sender, Player target, String warpName) {
        UUID targetUUID = target.getUniqueId();
        
        // Cancel any existing request for this target
        cancelRequest(targetUUID);
        
        // Create new request
        ShareRequest request = new ShareRequest(sender.getUniqueId(), targetUUID, warpName);
        pendingRequests.put(targetUUID, request);
        
        // Send request message to target with clickable accept button
        Component message = Component.text()
            .append(Component.text(sender.getName(), NamedTextColor.YELLOW))
            .append(Component.text(" quiere compartirte el warp ", NamedTextColor.WHITE))
            .append(Component.text(warpName, NamedTextColor.YELLOW))
            .append(Component.text(" "))
            .append(Component.text("[ACEPTAR]", NamedTextColor.GREEN, TextDecoration.BOLD)
                .clickEvent(ClickEvent.runCommand("/warp accept " + sender.getUniqueId() + " " + warpName)))
            .build();
        
        target.sendMessage(message);
        
        // Notify sender
        sender.sendMessage(Component.text("Solicitud de compartir warp enviada a ", NamedTextColor.GREEN)
            .append(Component.text(target.getName(), NamedTextColor.YELLOW)));
        
        // Schedule timeout
        new BukkitRunnable() {
            @Override
            public void run() {
                if (pendingRequests.containsKey(targetUUID)) {
                    handleTimeout(targetUUID);
                }
            }
        }.runTaskLater(plugin, 200L); // 10 seconds = 200 ticks
    }

    /**
     * Handles acceptance of a share request.
     * 
     * @param target The player accepting the request
     * @param senderUUID The UUID of the player who sent the request
     * @param warpName The name of the warp being shared
     * @return true if the request was successfully accepted, false otherwise
     */
    public boolean acceptRequest(Player target, UUID senderUUID, String warpName) {
        UUID targetUUID = target.getUniqueId();
        ShareRequest request = pendingRequests.get(targetUUID);
        
        if (request == null || !request.senderUUID.equals(senderUUID) || !request.warpName.equals(warpName)) {
            target.sendMessage(Component.text("No hay una solicitud válida para aceptar.", NamedTextColor.RED));
            return false;
        }
        
        // Remove the request
        pendingRequests.remove(targetUUID);
        
        // Get the sender player
        Player sender = Bukkit.getPlayer(senderUUID);
        if (sender == null) {
            target.sendMessage(Component.text("El jugador que envió la solicitud ya no está conectado.", NamedTextColor.RED));
            return false;
        }
        
        // Check if sender still has the warp
        if (!warpManager.hasWarp(sender, warpName)) {
            target.sendMessage(Component.text("El warp ya no existe.", NamedTextColor.RED));
            sender.sendMessage(Component.text("Tu warp " + warpName + " ya no existe, no se pudo compartir.", NamedTextColor.RED));
            return false;
        }
        
        // Check if target already has a warp with this name
        if (warpManager.hasWarp(target, warpName)) {
            target.sendMessage(Component.text("Ya tienes un warp llamado ", NamedTextColor.RED)
                .append(Component.text(warpName, NamedTextColor.YELLOW))
                .append(Component.text(".", NamedTextColor.RED)));
            sender.sendMessage(Component.text(target.getName() + " ya tiene un warp llamado " + warpName + ".", NamedTextColor.RED));
            return false;
        }
        
        // Copy the warp
        if (warpManager.copyWarp(sender, target, warpName)) {
            // Success messages
            target.sendMessage(Component.text("Warp ", NamedTextColor.GREEN)
                .append(Component.text(warpName, NamedTextColor.YELLOW))
                .append(Component.text(" recibido de ", NamedTextColor.GREEN))
                .append(Component.text(sender.getName(), NamedTextColor.YELLOW))
                .append(Component.text(".", NamedTextColor.GREEN)));
            
            sender.sendMessage(Component.text("Warp ", NamedTextColor.GREEN)
                .append(Component.text(warpName, NamedTextColor.YELLOW))
                .append(Component.text(" compartido con ", NamedTextColor.GREEN))
                .append(Component.text(target.getName(), NamedTextColor.YELLOW))
                .append(Component.text(".", NamedTextColor.GREEN)));
            
            return true;
        } else {
            target.sendMessage(Component.text("Error al recibir el warp.", NamedTextColor.RED));
            sender.sendMessage(Component.text("Error al compartir el warp.", NamedTextColor.RED));
            return false;
        }
    }

    /**
     * Cancels a pending request for a target player.
     * 
     * @param targetUUID The UUID of the target player
     */
    public void cancelRequest(UUID targetUUID) {
        pendingRequests.remove(targetUUID);
    }

    /**
     * Handles timeout of a share request.
     * 
     * @param targetUUID The UUID of the target player
     */
    private void handleTimeout(UUID targetUUID) {
        ShareRequest request = pendingRequests.remove(targetUUID);
        if (request == null) {
            return;
        }
        
        Player target = Bukkit.getPlayer(targetUUID);
        Player sender = Bukkit.getPlayer(request.senderUUID);
        
        if (target != null) {
            target.sendMessage(Component.text("La solicitud de warp ha expirado.", NamedTextColor.GRAY));
        }
        
        if (sender != null) {
            sender.sendMessage(Component.text("La solicitud de compartir warp ha expirado.", NamedTextColor.GRAY));
        }
    }

    /**
     * Checks if a player has a pending share request.
     * 
     * @param targetUUID The UUID of the target player
     * @return true if there's a pending request, false otherwise
     */
    public boolean hasPendingRequest(UUID targetUUID) {
        return pendingRequests.containsKey(targetUUID);
    }

    /**
     * Represents a pending share request.
     */
    private static class ShareRequest {
        final UUID senderUUID;
        final UUID targetUUID;
        final String warpName;

        ShareRequest(UUID senderUUID, UUID targetUUID, String warpName) {
            this.senderUUID = senderUUID;
            this.targetUUID = targetUUID;
            this.warpName = warpName;
        }
    }
}
