package com.articreep.travelfrog;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class InventoryListeners implements Listener {

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() == null) {
            if (event.getView().title().equals(Component.text("Backpack")) ||
                    event.getView().title().equals(Component.text("Table"))) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Item item = event.getItemDrop();
        if (Utils.getItemType(item.getItemStack()) != null) {
            event.setCancelled(true);
        }
    }




}
