package org.tomasa.mandarina.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.tomasa.mandarina.Mandarina;

import java.util.Base64;

public class VaultUtils {
    public static void saveItems(Player player, ItemStack[] items) {
        try {
            ByteArrayOutputStream io = new ByteArrayOutputStream();
            BukkitObjectOutputStream out = new BukkitObjectOutputStream(io);

            out.writeInt(items.length);

            for (ItemStack item : items) {
                out.writeObject(item);
            }

            out.close();

            String encoded = Base64.getEncoder().encodeToString(io.toByteArray());

            player.getPersistentDataContainer().set(
                    new NamespacedKey(Mandarina.getPlugin(), "vault"),
                    PersistentDataType.STRING,
                    encoded
            );
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static ArrayList<ItemStack> getItems(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        ArrayList<ItemStack> items = new ArrayList<>();

        String encodedItems = data.get(
                new NamespacedKey(Mandarina.getPlugin(), "vault"),
                PersistentDataType.STRING
        );

        if (encodedItems == null || encodedItems.isEmpty()) {
            return new ArrayList<>();
        }

        byte[] rawData = Base64.getDecoder().decode(encodedItems);

        try {
            ByteArrayInputStream io = new ByteArrayInputStream(rawData);
            BukkitObjectInputStream in = new BukkitObjectInputStream(io);

            int itemsCount = in.readInt();

            for (int i = 0; i < itemsCount; i++) {
                items.add((ItemStack) in.readObject());
            }

            in.close();

        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        return items;
    }
    
}
