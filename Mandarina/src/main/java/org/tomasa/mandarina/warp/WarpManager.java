package org.tomasa.mandarina.warp;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.tomasa.mandarina.Mandarina;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;

/**
 * Manages player warps using YAML configuration files.
 * Each player's warps are stored under their UUID as the root key.
 */
public class WarpManager {
    private final Mandarina plugin;
    private final File warpsFile;
    private final File backupFolder;
    private YamlConfiguration warpsConfig;
    private boolean hasUnsavedChanges = false;

    public WarpManager(Mandarina plugin) {
        this.plugin = plugin;
        this.warpsFile = new File(plugin.getDataFolder(), "warps.yml");
        this.backupFolder = new File(plugin.getDataFolder(), "backups");
        loadWarps();
        startAutoSaveTask();
    }

    /**
     * Loads the warps configuration from file.
     * Creates the file if it doesn't exist and creates backup folder.
     */
    public void loadWarps() {
        // Create plugin data folder if it doesn't exist
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        // Create backup folder if it doesn't exist
        if (!backupFolder.exists()) {
            backupFolder.mkdirs();
        }

        // Create warps file if it doesn't exist
        if (!warpsFile.exists()) {
            try {
                warpsFile.createNewFile();
                plugin.getLogger().info("Created new warps.yml file");
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create warps.yml", e);
                return;
            }
        }

        try {
            warpsConfig = YamlConfiguration.loadConfiguration(warpsFile);
            plugin.getLogger().info("Loaded " + getTotalWarpsCount() + " warps from warps.yml");
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error loading warps.yml", e);
            // Try to load from backup
            loadFromBackup();
        }
    }

    /**
     * Saves the warps configuration to file with backup creation.
     */
    public void saveWarps() {
        if (!hasUnsavedChanges) {
            return; // No changes to save
        }

        try {
            // Create backup before saving
            createBackup();

            // Save the configuration
            warpsConfig.save(warpsFile);
            hasUnsavedChanges = false;

            plugin.getLogger().fine("Warps saved successfully to warps.yml");
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save warps.yml", e);
        }
    }

    /**
     * Forces a save even if there are no unsaved changes.
     * Used during plugin shutdown.
     */
    public void forceSave() {
        try {
            createBackup();
            warpsConfig.save(warpsFile);
            hasUnsavedChanges = false;
            plugin.getLogger().info("Force saved warps.yml");
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not force save warps.yml", e);
        }
    }

    /**
     * Creates a new warp for the specified player.
     * 
     * @param player The player creating the warp
     * @param warpName The name of the warp
     * @param location The location to save
     * @return true if the warp was created, false if it already exists
     */
    public boolean createWarp(Player player, String warpName, Location location) {
        String playerUUID = player.getUniqueId().toString();
        String path = playerUUID + "." + warpName;

        if (warpsConfig.contains(path)) {
            return false; // Warp already exists
        }

        // Serialize location
        ConfigurationSection warpSection = warpsConfig.createSection(path);
        warpSection.set("world", location.getWorld().getName());
        warpSection.set("x", location.getX());
        warpSection.set("y", location.getY());
        warpSection.set("z", location.getZ());
        warpSection.set("yaw", location.getYaw());
        warpSection.set("pitch", location.getPitch());

        hasUnsavedChanges = true;
        saveWarps();
        return true;
    }

    /**
     * Deletes a warp for the specified player.
     * 
     * @param player The player deleting the warp
     * @param warpName The name of the warp to delete
     * @return true if the warp was deleted, false if it didn't exist
     */
    public boolean deleteWarp(Player player, String warpName) {
        String playerUUID = player.getUniqueId().toString();
        String path = playerUUID + "." + warpName;

        if (!warpsConfig.contains(path)) {
            return false; // Warp doesn't exist
        }

        warpsConfig.set(path, null);
        hasUnsavedChanges = true;
        saveWarps();
        return true;
    }

    /**
     * Gets a list of all warp names for the specified player.
     * 
     * @param player The player to get warps for
     * @return A set of warp names, empty if none exist
     */
    public Set<String> getPlayerWarps(Player player) {
        String playerUUID = player.getUniqueId().toString();
        ConfigurationSection playerSection = warpsConfig.getConfigurationSection(playerUUID);
        
        if (playerSection == null) {
            return new HashSet<>();
        }
        
        return playerSection.getKeys(false);
    }

    /**
     * Gets the location of a specific warp for a player.
     * 
     * @param player The player who owns the warp
     * @param warpName The name of the warp
     * @return The location of the warp, or null if it doesn't exist
     */
    public Location getWarpLocation(Player player, String warpName) {
        String playerUUID = player.getUniqueId().toString();
        String path = playerUUID + "." + warpName;
        
        if (!warpsConfig.contains(path)) {
            return null;
        }

        ConfigurationSection warpSection = warpsConfig.getConfigurationSection(path);
        if (warpSection == null) {
            return null;
        }

        String worldName = warpSection.getString("world");
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return null; // World doesn't exist
        }

        double x = warpSection.getDouble("x");
        double y = warpSection.getDouble("y");
        double z = warpSection.getDouble("z");
        float yaw = (float) warpSection.getDouble("yaw");
        float pitch = (float) warpSection.getDouble("pitch");
        
        return new Location(world, x, y, z, yaw, pitch);
    }

    /**
     * Checks if a player has a specific warp.
     * 
     * @param player The player to check
     * @param warpName The name of the warp
     * @return true if the warp exists, false otherwise
     */
    public boolean hasWarp(Player player, String warpName) {
        String playerUUID = player.getUniqueId().toString();
        String path = playerUUID + "." + warpName;
        return warpsConfig.contains(path);
    }

    /**
     * Copies a warp from one player to another.
     * Used for the share functionality.
     * 
     * @param fromPlayer The player sharing the warp
     * @param toPlayer The player receiving the warp
     * @param warpName The name of the warp
     * @return true if successful, false if the source warp doesn't exist
     */
    public boolean copyWarp(Player fromPlayer, Player toPlayer, String warpName) {
        Location location = getWarpLocation(fromPlayer, warpName);
        if (location == null) {
            return false;
        }
        
        return createWarp(toPlayer, warpName, location);
    }

    /**
     * Starts the auto-save task that runs every 5 minutes.
     */
    private void startAutoSaveTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (hasUnsavedChanges) {
                    saveWarps();
                    plugin.getLogger().fine("Auto-saved warps.yml");
                }
            }
        }.runTaskTimerAsynchronously(plugin, 6000L, 6000L); // 5 minutes = 6000 ticks
    }

    /**
     * Creates a backup of the current warps file.
     */
    private void createBackup() {
        if (!warpsFile.exists()) {
            return;
        }

        try {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            File backupFile = new File(backupFolder, "warps_" + timestamp + ".yml");

            Files.copy(warpsFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // Keep only the last 10 backups
            cleanupOldBackups();

        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Could not create backup of warps.yml", e);
        }
    }

    /**
     * Removes old backup files, keeping only the 10 most recent.
     */
    private void cleanupOldBackups() {
        File[] backups = backupFolder.listFiles((dir, name) -> name.startsWith("warps_") && name.endsWith(".yml"));

        if (backups == null || backups.length <= 10) {
            return;
        }

        // Sort by last modified date (newest first)
        Arrays.sort(backups, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));

        // Delete old backups (keep only first 10)
        for (int i = 10; i < backups.length; i++) {
            if (backups[i].delete()) {
                plugin.getLogger().fine("Deleted old backup: " + backups[i].getName());
            }
        }
    }

    /**
     * Attempts to load warps from the most recent backup.
     */
    private void loadFromBackup() {
        File[] backups = backupFolder.listFiles((dir, name) -> name.startsWith("warps_") && name.endsWith(".yml"));

        if (backups == null || backups.length == 0) {
            plugin.getLogger().warning("No backup files found, creating new warps configuration");
            warpsConfig = new YamlConfiguration();
            return;
        }

        // Sort by last modified date (newest first)
        Arrays.sort(backups, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));

        try {
            warpsConfig = YamlConfiguration.loadConfiguration(backups[0]);
            plugin.getLogger().info("Loaded warps from backup: " + backups[0].getName());
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Could not load from backup", e);
            warpsConfig = new YamlConfiguration();
        }
    }

    /**
     * Gets the total number of warps across all players.
     *
     * @return Total warp count
     */
    private int getTotalWarpsCount() {
        int count = 0;
        for (String playerUUID : warpsConfig.getKeys(false)) {
            ConfigurationSection playerSection = warpsConfig.getConfigurationSection(playerUUID);
            if (playerSection != null) {
                count += playerSection.getKeys(false).size();
            }
        }
        return count;
    }

    /**
     * Saves warps for a specific player when they disconnect.
     * This ensures no data is lost if the server crashes.
     *
     * @param player The player disconnecting
     */
    public void savePlayerWarps(Player player) {
        if (hasUnsavedChanges) {
            saveWarps();
            plugin.getLogger().fine("Saved warps for disconnecting player: " + player.getName());
        }
    }

    /**
     * Shuts down the warp manager and ensures all data is saved.
     */
    public void shutdown() {
        forceSave();
        plugin.getLogger().info("WarpManager shutdown complete");
    }
}
