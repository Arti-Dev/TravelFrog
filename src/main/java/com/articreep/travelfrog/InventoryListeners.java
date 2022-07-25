package com.articreep.travelfrog;

import com.articreep.travelfrog.playerdata.PlayerData;
import com.articreep.travelfrog.playerdata.PlayerDataManager;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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

    @EventHandler
    public void onShopClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getHolder() == null) {
            if (event.getView().title().equals(Component.text("Buy Item"))) {
                event.setCancelled(true);
                ItemStack item;
                try {
                    item = inv.getItem(event.getSlot());
                } catch (ArrayIndexOutOfBoundsException e) {
                    return;
                }
                if (item == null) return;
                ItemType type = Utils.getItemType(item);
                if (type == null) return;

                int price = type.getPrice();
                Player p = (Player) event.getWhoClicked();
                PlayerData data = PlayerDataManager.getPlayerData(p.getUniqueId());
                if (data.getClovers() < price) {
                    p.closeInventory();
                    p.sendMessage(ChatColor.RED + "Not enough clovers.");
                    return;
                }
                data.decrementCloverCount(price);
                data.incrementItemCount(type, 1);
                p.closeInventory();
                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            }
        }
    }

}
