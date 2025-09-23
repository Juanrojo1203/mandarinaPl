package org.tomasa.mandarina.warp;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.tomasa.mandarina.Mandarina;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Main command executor for the /warp command system.
 * Handles all warp-related subcommands and provides tab completion.
 */
public class WarpCommand implements CommandExecutor, TabCompleter {
    private final Mandarina plugin;
    private final WarpManager warpManager;
    private final ShareRequestManager shareRequestManager;

    public WarpCommand(Mandarina plugin, WarpManager warpManager, ShareRequestManager shareRequestManager) {
        this.plugin = plugin;
        this.warpManager = warpManager;
        this.shareRequestManager = shareRequestManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Este comando solo puede ser usado por jugadores.", NamedTextColor.RED));
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "create" -> handleCreate(player, args);
            case "delete" -> handleDelete(player, args);
            case "list" -> handleList(player);
            case "share" -> handleShare(player, args);
            case "accept" -> handleAccept(player, args);
            case "tp", "teleport" -> handleTeleportCommand(player, args);
            case "help" -> sendHelp(player);
            default -> {
                // Show help for unknown commands
                player.sendMessage(Component.text("Comando desconocido. Usa ", NamedTextColor.RED)
                    .append(Component.text("/warp help", NamedTextColor.YELLOW))
                    .append(Component.text(" para ver los comandos disponibles.", NamedTextColor.RED)));
            }
        }

        return true;
    }

    private void handleCreate(Player player, String[] args) {
        if (args.length != 2) {
            player.sendMessage(Component.text("Uso: /warp create <nombre>", NamedTextColor.RED));
            return;
        }

        String warpName = args[1];
        Location location = player.getLocation();

        if (warpManager.createWarp(player, warpName, location)) {
            player.sendMessage(Component.text("Warp ", NamedTextColor.GREEN)
                .append(Component.text(warpName, NamedTextColor.YELLOW))
                .append(Component.text(" creado.", NamedTextColor.GREEN)));
        } else {
            player.sendMessage(Component.text("Ya tienes un warp llamado ", NamedTextColor.RED)
                .append(Component.text(warpName, NamedTextColor.YELLOW))
                .append(Component.text(".", NamedTextColor.RED)));
        }
    }

    private void handleDelete(Player player, String[] args) {
        if (args.length != 2) {
            player.sendMessage(Component.text("Uso: /warp delete <nombre>", NamedTextColor.RED));
            return;
        }

        String warpName = args[1];

        if (warpManager.deleteWarp(player, warpName)) {
            player.sendMessage(Component.text("Warp ", NamedTextColor.GREEN)
                .append(Component.text(warpName, NamedTextColor.YELLOW))
                .append(Component.text(" eliminado.", NamedTextColor.GREEN)));
        } else {
            player.sendMessage(Component.text("No existe un warp llamado ", NamedTextColor.RED)
                .append(Component.text(warpName, NamedTextColor.YELLOW))
                .append(Component.text(".", NamedTextColor.RED)));
        }
    }

    private void handleList(Player player) {
        Set<String> warps = warpManager.getPlayerWarps(player);

        if (warps.isEmpty()) {
            player.sendMessage(Component.text("(No tienes warps.)", NamedTextColor.GRAY));
            return;
        }

        Component message = Component.text("Tus warps: ", NamedTextColor.WHITE);

        boolean first = true;
        for (String warpName : warps) {
            if (!first) {
                message = message.append(Component.text(", ", NamedTextColor.WHITE));
            }

            message = message.append(Component.text(warpName, NamedTextColor.YELLOW)
                .clickEvent(ClickEvent.suggestCommand("/warp " + warpName)));

            first = false;
        }

        player.sendMessage(message);
    }

    private void handleShare(Player player, String[] args) {
        if (args.length < 2 || args.length > 3) {
            player.sendMessage(Component.text("Uso: /warp share <nombre> [jugador]", NamedTextColor.RED));
            return;
        }

        String warpName = args[1];
        Player target;

        if (args.length == 3) {
            // Explicit player argument
            target = Bukkit.getPlayer(args[2]);
            if (target == null) {
                player.sendMessage(Component.text("Jugador no encontrado.", NamedTextColor.RED));
                return;
            }
        } else {
            // Look for player in line of sight within 5 blocks
            target = getTargetPlayer(player, 5.0);
            if (target == null) {
                player.sendMessage(Component.text("No hay ningún jugador en tu línea de visión (máximo 5 bloques).", NamedTextColor.RED));
                return;
            }
        }

        if (target.equals(player)) {
            player.sendMessage(Component.text("No puedes compartir un warp contigo mismo.", NamedTextColor.RED));
            return;
        }

        if (!warpManager.hasWarp(player, warpName)) {
            player.sendMessage(Component.text("No existe un warp llamado ", NamedTextColor.RED)
                .append(Component.text(warpName, NamedTextColor.YELLOW))
                .append(Component.text(".", NamedTextColor.RED)));
            return;
        }

        shareRequestManager.createShareRequest(player, target, warpName);
    }

    private void handleAccept(Player player, String[] args) {
        if (args.length != 3) {
            player.sendMessage(Component.text("Comando interno inválido.", NamedTextColor.RED));
            return;
        }

        try {
            UUID senderUUID = UUID.fromString(args[1]);
            String warpName = args[2];
            shareRequestManager.acceptRequest(player, senderUUID, warpName);
        } catch (IllegalArgumentException e) {
            player.sendMessage(Component.text("Comando interno inválido.", NamedTextColor.RED));
        }
    }

    private void handleTeleportCommand(Player player, String[] args) {
        if (args.length != 2) {
            player.sendMessage(Component.text("Uso: /warp tp <nombre>", NamedTextColor.RED));
            return;
        }

        String warpName = args[1];
        handleTeleport(player, warpName);
    }

    private void handleTeleport(Player player, String warpName) {
        Location location = warpManager.getWarpLocation(player, warpName);

        if (location == null) {
            player.sendMessage(Component.text("No existe un warp llamado ", NamedTextColor.RED)
                .append(Component.text(warpName, NamedTextColor.YELLOW))
                .append(Component.text(".", NamedTextColor.RED)));
            return;
        }

        player.teleport(location);
        player.sendMessage(Component.text("Teletransportado al warp ", NamedTextColor.GREEN)
            .append(Component.text(warpName, NamedTextColor.YELLOW))
            .append(Component.text(".", NamedTextColor.GREEN)));
    }

    private void sendHelp(Player player) {
        player.sendMessage(Component.text("=== Comandos de Warp ===", NamedTextColor.GOLD));
        player.sendMessage(Component.text("/warp create <nombre>", NamedTextColor.YELLOW)
            .append(Component.text(" - Crear un warp", NamedTextColor.WHITE)));
        player.sendMessage(Component.text("/warp delete <nombre>", NamedTextColor.YELLOW)
            .append(Component.text(" - Eliminar un warp", NamedTextColor.WHITE)));
        player.sendMessage(Component.text("/warp list", NamedTextColor.YELLOW)
            .append(Component.text(" - Listar tus warps", NamedTextColor.WHITE)));
        player.sendMessage(Component.text("/warp tp <nombre>", NamedTextColor.YELLOW)
            .append(Component.text(" - Teletransportarse a un warp", NamedTextColor.WHITE)));
        player.sendMessage(Component.text("/warp share <nombre> [jugador]", NamedTextColor.YELLOW)
            .append(Component.text(" - Compartir un warp", NamedTextColor.WHITE)));
    }

    /**
     * Gets the player the sender is looking at within the specified distance.
     * 
     * @param player The player looking
     * @param maxDistance Maximum distance to look
     * @return The target player, or null if none found
     */
    private Player getTargetPlayer(Player player, double maxDistance) {
        RayTraceResult result = player.getWorld().rayTraceEntities(
            player.getEyeLocation(),
            player.getEyeLocation().getDirection(),
            maxDistance,
            entity -> entity instanceof Player && !entity.equals(player)
        );

        if (result != null && result.getHitEntity() instanceof Player) {
            return (Player) result.getHitEntity();
        }

        return null;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            // First argument: only subcommands (no direct warp names)
            List<String> completions = Arrays.asList("create", "delete", "list", "tp", "share", "help");

            return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                .sorted()
                .collect(Collectors.toList());
        }

        if (args.length == 2) {
            String subCommand = args[0].toLowerCase();

            if ("delete".equals(subCommand) || "share".equals(subCommand) ||
                "tp".equals(subCommand) || "teleport".equals(subCommand)) {
                // Second argument for delete/share/tp: player's warp names
                return warpManager.getPlayerWarps(player).stream()
                    .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                    .sorted()
                    .collect(Collectors.toList());
            }
        }

        if (args.length == 3 && "share".equals(args[0].toLowerCase())) {
            // Third argument for share: online player names
            return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> !name.equals(player.getName())) // Exclude self
                .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
                .sorted()
                .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
