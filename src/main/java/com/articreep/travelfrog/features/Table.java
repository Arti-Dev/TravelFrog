package com.articreep.travelfrog.features;

import com.articreep.travelfrog.ItemCategory;
import com.articreep.travelfrog.ItemType;
import com.articreep.travelfrog.Utils;
import com.articreep.travelfrog.features.Backpack;
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

public class Table implements CommandExecutor, Listener {

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
        Inventory inv = buildInventory(data.getTable());
        p.openInventory(inv);
    }
    protected static Inventory buildInventory(List<ItemType> list) {
        Inventory inv = Bukkit.createInventory(null, 27, Component.text("Table"));
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }

        inv.setItem(2, Utils.createDisplayItem(list.get(0)));
        inv.setItem(3, Utils.createDisplayItem(list.get(1)));
        inv.setItem(5, Utils.createDisplayItem(list.get(2)));
        inv.setItem(6, Utils.createDisplayItem(list.get(3)));
        inv.setItem(20, Utils.createDisplayItem(list.get(4)));
        inv.setItem(21, Utils.createDisplayItem(list.get(5)));
        inv.setItem(23, Utils.createDisplayItem(list.get(6)));
        inv.setItem(24, Utils.createDisplayItem(list.get(7)));
        inv.setItem(9, new ItemStack(Material.ARROW));
        return inv;
    }

    @EventHandler
    public void onTable(InventoryClickEvent event) {
        InventoryView view = event.getView();
        Inventory inv = event.getClickedInventory();
        if (view.title().equals(Component.text("Table"))) {
            event.setCancelled(true);
            ItemStack itemClicked = event.getCurrentItem();
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
                ItemType type = Utils.getItemType(itemClicked);
                if (type == null) return;

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

    // TODO make these configurable
    @EventHandler
    public void onTableClick(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.HAND) return;
        if (event.getClickedBlock() == null) return;
        if (!event.getAction().isRightClick()) return;
        Location tableLocation = new Location(event.getPlayer().getWorld(), 634, 72, 73);
        Location tableLocation2 = new Location(event.getPlayer().getWorld(), 634, 73, 73);
        Location clickedLocation = event.getClickedBlock().getLocation();
        if (clickedLocation.equals(tableLocation) || clickedLocation.equals(tableLocation2)) {
            runCommand(event.getPlayer());
        }
    }


}
