package org.tomasa.mandarina;

import org.tomasa.mandarina.commands.CordCommand;
import org.tomasa.mandarina.commands.InfoCommand;
import org.tomasa.mandarina.commands.PlayerVaultCommand;
import org.tomasa.mandarina.commands.sCommand;
import org.tomasa.mandarina.commands.tpCommand;
import org.tomasa.mandarina.warp.WarpCommand;
import org.tomasa.mandarina.warp.WarpManager;
import org.tomasa.mandarina.warp.ShareRequestManager;
import org.tomasa.mandarina.warp.WarpListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.tomasa.mandarina.commands.deathCommand;
import org.tomasa.mandarina.listeners.VaultListener;

import java.util.Objects;

public final class Mandarina extends JavaPlugin {
    private static Mandarina instance;
    private WarpManager warpManager;
    private ShareRequestManager shareRequestManager;

    public final String prefix = "&8[&6Mandarina&8]";
    public final String version = getDescription().getVersion();

    @Override
    public void onEnable() {
        instance = this;

        // Initialize warp system
        warpManager = new WarpManager(this);
        shareRequestManager = new ShareRequestManager(this, warpManager);

        // Plugin startup logic
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes(
                '&', prefix + " &aPlugin iniciado en la versión: &b" + version));

        registerCommands();
    }

    private void registerCommands() {
        Objects.requireNonNull(this.getCommand("mandarina")).setExecutor(new InfoCommand(this));
        Objects.requireNonNull(this.getCommand("muerte")).setExecutor(new deathCommand());
        Objects.requireNonNull(this.getCommand("cords")).setExecutor(new CordCommand());
        Objects.requireNonNull(this.getCommand("vault")).setExecutor(new PlayerVaultCommand(this));
        Objects.requireNonNull(this.getCommand("s")).setExecutor(new sCommand());
        Objects.requireNonNull(this.getCommand("tp")).setExecutor(new tpCommand());

        // Register warp command with tab completion
        WarpCommand warpCommand = new WarpCommand(this, warpManager, shareRequestManager);
        Objects.requireNonNull(this.getCommand("warp")).setExecutor(warpCommand);
        Objects.requireNonNull(this.getCommand("warp")).setTabCompleter(warpCommand);

        // Register event listeners
        getServer().getPluginManager().registerEvents(new VaultListener(), this);
        getServer().getPluginManager().registerEvents(new WarpListener(warpManager, shareRequestManager), this);
    }

    @Override
    public void onDisable() {
        // Shutdown warp system and save all data
        if (warpManager != null) {
            warpManager.shutdown();
        }

        // Plugin shutdown logic
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes(
                '&', prefix + " &cPlugin desactivado"));
    }
    public static Mandarina getPlugin() {
        return instance;
    }
}
