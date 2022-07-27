package com.articreep.travelfrog;

import com.articreep.travelfrog.playerdata.PlayerData;
import com.articreep.travelfrog.playerdata.PlayerDataManager;
import io.papermc.paper.event.entity.EntityDamageItemEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ShopListeners implements Listener {
    private static final NamespacedKey key = new NamespacedKey(TravelFrog.getPlugin(), "TravelFrogType");

    @EventHandler
    public void onItemFrameClick(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof ItemFrame frame) {

            if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;

            // Check what's inside that itemframe and whether it uses our key.
            ItemStack item = frame.getItem();
            ItemMeta meta = item.getItemMeta();
            if (meta == null) return;
            PersistentDataContainer container = meta.getPersistentDataContainer();
            if (container.has(key)) {
                ItemType type = ItemType.valueOf(container.get(key, PersistentDataType.STRING));
                if (type.getPrice() < 0) return;

                event.setCancelled(true);
                Player p = event.getPlayer();
                PlayerData data = PlayerDataManager.getPlayerData(p.getUniqueId());
                Inventory inv = Bukkit.createInventory(null, 27, Component.text("Buy Item"));
                inv.setItem(13, Utils.createShopItem(type, data.getItemCount(type), data.hasSingleItem(type)));
                p.openInventory(inv);
            }
        }
    }

    @EventHandler
    public void onItemFrameBreak(HangingBreakByEntityEvent event) {

        if (event.getEntity() instanceof ItemFrame) {
            event.setCancelled(true);
            if (event.getRemover() instanceof Player p) {
                if (p.getGameMode() == GameMode.CREATIVE) {
                    event.setCancelled(false);
                }
            }
        }

    }

    @EventHandler
    public void onItemFrameTakeOut(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player p) {
            if (event.getEntity() instanceof ItemFrame) {
                if (p.getGameMode() != GameMode.CREATIVE) {
                    event.setCancelled(true);
                }
            }
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

                if (type.isSingleItem()) {
                    if (data.hasSingleItem(type)) {
                        p.closeInventory();
                        p.sendMessage(ChatColor.RED + "You already bought this item!");
                        return;
                    }
                }

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
