package com.articreep.travelfrog;

import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class InventoryListeners implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        // We are canceling all clicks in the player's inventory
        if (event.getClickedInventory() == event.getWhoClicked().getInventory()) {
            // TODO DEBUGGING PURPOSES
            if (event.getWhoClicked().getGameMode() == GameMode.CREATIVE) return;
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getInventory() == event.getWhoClicked().getInventory()) {
            // TODO DEBUGGING PURPOSES
            if (event.getWhoClicked().getGameMode() == GameMode.CREATIVE) return;
            event.setCancelled(true);
        } else if (event.getInventory().getHolder() == null) {
            if (event.getView().title().equals(Component.text("Backpack")) ||
                    event.getView().title().equals(Component.text("Table"))) {
                event.setCancelled(true);
            }
        }
    }




}
