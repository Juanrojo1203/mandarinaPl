package org.tomasa.mandarina;

import org.tomasa.mandarina.commands.CordCommand;
import org.tomasa.mandarina.commands.InfoCommand;
import org.tomasa.mandarina.commands.PlayerVaultCommand;
import org.tomasa.mandarina.commands.sCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.tomasa.mandarina.commands.deathCommand;
import org.tomasa.mandarina.listeners.VaultListener;

import java.util.Objects;

public final class Mandarina extends JavaPlugin {
    private static Mandarina instance;

    public final String prefix = "&8[&6Mandarina&8]";
    public final String version = getDescription().getVersion();

    @Override
    public void onEnable() {
        instance = this;

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
        getServer().getPluginManager().registerEvents(new VaultListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes(
                '&', prefix + " &cPlugin desactivado"));
    }
    public static Mandarina getPlugin() {
        return instance;
    }
}
