package com.articreep.travelfrog;

import com.articreep.travelfrog.playerdata.PlayerData;
import com.articreep.travelfrog.playerdata.PlayerDataManager;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class InventoryListeners implements Listener {
    private static final NamespacedKey key = new NamespacedKey(TravelFrog.getPlugin(), "TravelFrogType");

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        // We are canceling all clicks in the player's inventory
        if (event.getClickedInventory() == event.getWhoClicked().getInventory()) {
            // DEBUGGING PURPOSES
            if (event.getWhoClicked().getGameMode() == GameMode.CREATIVE) return;
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getInventory() == event.getWhoClicked().getInventory()) {
            // DEBUGGING PURPOSES
            if (event.getWhoClicked().getGameMode() == GameMode.CREATIVE) return;
            event.setCancelled(true);
        } else if (event.getInventory().getHolder() == null) {
            if (event.getView().title().equals(Component.text("Backpack")) ||
                    event.getView().title().equals(Component.text("Table"))) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBackpack(InventoryClickEvent event) {
        InventoryView view = event.getView();
        Inventory inv = event.getClickedInventory();
        if (view.title().equals(Component.text("Backpack"))) {
            event.setCancelled(true);
            ItemStack itemClicked = view.getItem(event.getRawSlot());
            if (itemClicked == null) return;

            PlayerData data = PlayerDataManager.getPlayerData(event.getWhoClicked().getUniqueId());
            List<ItemType> backpack = data.getBackpack();

            // Determine which inventory was clicked
            if (inv == view.getTopInventory()) {
                // Check the index clicked
                switch (event.getSlot()) {
                    case 3 -> data.removeFromBackpack(0);
                    case 5 -> data.removeFromBackpack(1);
                    case 21 -> data.removeFromBackpack(2);
                    case 23 -> data.removeFromBackpack(3);
                    case 17 -> {
                        view.close();
                        event.getWhoClicked().openInventory(Table.buildInventory(data.getTable()));
                    }
                    default -> {
                        return;
                    }
                }
                inv.setItem(event.getSlot(), Utils.createDisplayItem(ItemType.NONE));

            } else if (inv == view.getBottomInventory()){
                Inventory topInv = view.getTopInventory();
                PersistentDataContainer container = itemClicked.getItemMeta().getPersistentDataContainer();
                if (container.has(key)) {
                    // They want to add the item to the backpack.
                    ItemType type = ItemType.valueOf(container.get(key, PersistentDataType.STRING));

                    if (type.getCategory() == ItemCategory.FOOD) {
                        data.setInBackpack(0, type);
                        topInv.setItem(3, Utils.createDisplayItem(type));

                    } else if (type.getCategory() == ItemCategory.CHARM) {
                        data.setInBackpack(1, type);
                        topInv.setItem(5, Utils.createDisplayItem(type));

                    } else if (type.getCategory() == ItemCategory.TOOL) {
                        // more complicated logic since there are two slots
                        if (backpack.get(2) == ItemType.NONE) {
                            data.setInBackpack(2, type);
                            topInv.setItem(21, Utils.createDisplayItem(type));
                        } else {
                            data.setInBackpack(3, type);
                            topInv.setItem(23, Utils.createDisplayItem(type));
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onTable(InventoryClickEvent event) {
        InventoryView view = event.getView();
        Inventory inv = event.getClickedInventory();
        if (view.title().equals(Component.text("Table"))) {
            event.setCancelled(true);
            ItemStack itemClicked = view.getItem(event.getRawSlot());
            if (itemClicked == null) return;

            PlayerData data = PlayerDataManager.getPlayerData(event.getWhoClicked().getUniqueId());
            List<ItemType> table = data.getTable();

            // Determine which inventory was clicked
            if (inv == view.getTopInventory()) {
                // Check the index clicked
                switch (event.getSlot()) {
                    case 2 -> data.removeFromTable(0);
                    case 3 -> data.removeFromTable(1);
                    case 5 -> data.removeFromTable(2);
                    case 6 -> data.removeFromTable(3);
                    case 20 -> data.removeFromTable(4);
                    case 21 -> data.removeFromTable(5);
                    case 23 -> data.removeFromTable(6);
                    case 24 -> data.removeFromTable(7);
                    case 9 -> {
                        view.close();
                        event.getWhoClicked().openInventory(Backpack.buildInventory(data.getBackpack()));
                    }
                    default -> {
                        return;
                    }
                }
                inv.setItem(event.getSlot(), Utils.createDisplayItem(ItemType.NONE));

            } else if (inv == view.getBottomInventory()) {
                Inventory topInv = view.getTopInventory();
                PersistentDataContainer container = itemClicked.getItemMeta().getPersistentDataContainer();
                if (container.has(key)) {
                    // They want to add the item to the table.
                    ItemType type = ItemType.valueOf(container.get(key, PersistentDataType.STRING));

                    if (type.getCategory() == ItemCategory.FOOD) {
                        if (table.get(0) == ItemType.NONE) {
                            data.setInTable(0, type);
                            topInv.setItem(2, Utils.createDisplayItem(type));
                        } else {
                            data.setInTable(1, type);
                            topInv.setItem(3, Utils.createDisplayItem(type));
                        }

                    } else if (type.getCategory() == ItemCategory.CHARM) {
                        if (table.get(2) == ItemType.NONE) {
                            data.setInTable(2, type);
                            topInv.setItem(5, Utils.createDisplayItem(type));
                        } else {
                            data.setInTable(3, type);
                            topInv.setItem(6, Utils.createDisplayItem(type));
                        }

                    } else if (type.getCategory() == ItemCategory.TOOL) {
                        if (table.get(4) == ItemType.NONE) {
                            data.setInTable(4, type);
                            topInv.setItem(20, Utils.createDisplayItem(type));
                        } else if (table.get(5) == ItemType.NONE) {
                            data.setInTable(5, type);
                            topInv.setItem(21, Utils.createDisplayItem(type));
                        } else if (table.get(6) == ItemType.NONE) {
                            data.setInTable(6, type);
                            topInv.setItem(23, Utils.createDisplayItem(type));
                        } else {
                            data.setInTable(7, type);
                            topInv.setItem(24, Utils.createDisplayItem(type));
                        }
                    }
                }
            }
        }
    }

}
