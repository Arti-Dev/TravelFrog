package com.articreep.travelfrog;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

public class InventoryListeners implements Listener {

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
        }
    }

}
