package com.articreep.travelfrog;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class Utils {
    private static final NamespacedKey key = new NamespacedKey(TravelFrog.getPlugin(), "TravelFrogType");

    /**
     * Gets a random location where a clover can be placed. The location may change depending on the clover type.
     * @param w The world to generate Locations for
     * @param type Type of clover. Four-leaf clovers only spawn one block tall.
     * @param locationSetToExclude When a location is generated, this method will check against this list and will try generating again if one matches.
     * @return A valid Location for a clover to be spawned on.
     */
    public static Location getRandomCloverLocation(World w, CloverType type, Set<Location> locationSetToExclude) {
        // TODO Will hardcode for now :)
        Random random = new Random();

        Location loc1 = new Location(w, 634, 72, 83);
        Location loc2 = new Location(w, 644, 72, 93);

        for (int i = 0; i < 101; i++) {
            int x = random.nextInt(loc2.getBlockX() - loc1.getBlockX() + 1) + loc1.getBlockX();
            int z = random.nextInt(loc2.getBlockZ() - loc1.getBlockZ() + 1) + loc1.getBlockZ();

            // Conditions: Material is air, bottom is muddy mangrove roots

            Location target = new Location(w, x, TravelFrog.getCloverYValue(), z);
            if (target.getBlock().getType() != Material.AIR) continue;
            if (target.clone().subtract(0, 1, 0).getBlock().getType() != Material.MUDDY_MANGROVE_ROOTS) continue;

            // Check if these coordinates are already in the Set
            boolean contains = false;
            for (Location l : locationSetToExclude) {
                if (l.getBlockX() == x && l.getBlockZ() == z) {
                    contains = true;
                    break;
                }
            }
            if (contains) continue;

            // 30% chance that the clover spawns 1 higher than usual
            if (type == CloverType.CLOVER && Math.random() < 0.3) target.add(0, 1, 0);

            return target;
        }
        return null;
    }

    public static ItemStack updateInventoryItem(ItemStack item, ItemType type, int amount) {
        ItemMeta meta = item.getItemMeta();
        meta.displayName(type.getName());
        meta.lore(type.createLore(amount));
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, type.toString());
        item.setItemMeta(meta);
        if (amount < 0) item.setAmount(1);
        else item.setAmount(Math.min(amount, 64));
        item.setType(type.getMaterial());
        return item;
    }

    public static ItemStack createShopItem(ItemType type, int amount) {
        ItemStack item = new ItemStack(type.getMaterial());
        ItemMeta meta = item.getItemMeta();
        meta.displayName(type.getName());
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, type.toString());
        List<Component> loreList = type.createLore(amount);
        loreList.add(Component.text(""));
        loreList.add(Component.text("Cost: ", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
                .append(Component.text().content(type.getPrice() + " Clovers").color(NamedTextColor.GREEN).build()));
        if (type.isSingleItem() && amount >= 1) {
            loreList.add(Component.text("You already bought this item!", NamedTextColor.RED));
        }
        meta.lore(loreList);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemType getItemType(ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        if (!container.has(key)) return null;
        return ItemType.valueOf(container.get(key, PersistentDataType.STRING));
    }
}
