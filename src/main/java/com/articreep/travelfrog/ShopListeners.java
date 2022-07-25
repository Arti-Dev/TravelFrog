package com.articreep.travelfrog;

import com.articreep.travelfrog.playerdata.PlayerDataManager;
import io.papermc.paper.event.entity.EntityDamageItemEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
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
                int playerHas = PlayerDataManager.getPlayerData(p.getUniqueId()).getItemCount(type);
                Inventory inv = Bukkit.createInventory(null, 27, Component.text("Buy Item"));
                inv.setItem(13, Utils.createShopItem(type, playerHas));
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
}
