package com.articreep.travelfrog;

import com.articreep.travelfrog.playerdata.PlayerData;
import com.articreep.travelfrog.playerdata.PlayerDataManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Table implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player p) {
            PlayerData data = PlayerDataManager.getPlayerData(p.getUniqueId());
            if (data == null) {
                p.sendMessage(ChatColor.RED + "Didn't work!");
                return true;
            }
            Inventory inv = buildInventory(data.getTable());
            p.openInventory(inv);
        }
        return true;
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
}
