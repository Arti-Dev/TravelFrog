package com.articreep.travelfrog.features;

import com.articreep.travelfrog.ItemCategory;
import com.articreep.travelfrog.ItemType;
import com.articreep.travelfrog.Utils;
import com.articreep.travelfrog.playerdata.PlayerData;
import com.articreep.travelfrog.playerdata.PlayerDataManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Backpack implements CommandExecutor, Listener {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player p) {
            runCommand(p);
        }
        return true;
    }

    private void runCommand(Player p) {
        PlayerData data = PlayerDataManager.getPlayerData(p.getUniqueId());
        if (data == null) {
            p.sendMessage(ChatColor.RED + "Didn't work!");
            return;
        }
        Inventory inv = buildInventory(data.getBackpack());
        p.openInventory(inv);
    }

    protected static Inventory buildInventory(List<ItemType> list) {
        Inventory inv = Bukkit.createInventory(null, 27, Component.text("Backpack"));
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }

        inv.setItem(3, Utils.createDisplayItem(list.get(0)));
        inv.setItem(5, Utils.createDisplayItem(list.get(1)));
        inv.setItem(21, Utils.createDisplayItem(list.get(2)));
        inv.setItem(23, Utils.createDisplayItem(list.get(3)));
        inv.setItem(17, new ItemStack(Material.ARROW));
        return inv;
    }

    @EventHandler
    public void onBackpack(InventoryClickEvent event) {
        InventoryView view = event.getView();
        Inventory inv = event.getClickedInventory();
        if (view.title().equals(Component.text("Backpack"))) {
            event.setCancelled(true);
            ItemStack itemClicked = event.getCurrentItem();
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

            } else if (inv == view.getBottomInventory()) {
                Inventory topInv = view.getTopInventory();
                ItemType type = Utils.getItemType(itemClicked);
                if (type == null) return;

                if (type.getCategory() == ItemCategory.FOOD) {
                    data.setInBackpack(0, type);
                    topInv.setItem(3, Utils.createDisplayItem(type));

                } else if (type.getCategory() == ItemCategory.CHARM) {
                    data.setInBackpack(1, type);
                    topInv.setItem(5, Utils.createDisplayItem(type));

                } else if (type.getCategory() == ItemCategory.TOOL) {
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

    // TODO make these configurable
    @EventHandler
    public void onBackpackClick(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.HAND) return;
        if (event.getClickedBlock() == null) return;
        if (!event.getAction().isRightClick()) return;
        Location backpackLocation = new Location(event.getPlayer().getWorld(), 634, 72, 75);
        Location clickedLocation = event.getClickedBlock().getLocation();
        if (clickedLocation.equals(backpackLocation)) {
            runCommand(event.getPlayer());
        }
    }
}
