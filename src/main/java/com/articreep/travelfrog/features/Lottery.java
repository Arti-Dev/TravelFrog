package com.articreep.travelfrog.features;

import com.articreep.travelfrog.ItemType;
import com.articreep.travelfrog.TravelFrog;
import com.articreep.travelfrog.Utils;
import com.articreep.travelfrog.playerdata.PlayerData;
import com.articreep.travelfrog.playerdata.PlayerDataManager;
import dev.dbassett.skullcreator.SkullCreator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Lottery implements CommandExecutor, Listener {
    private boolean inUse = false;
    private static final Map<Player, BallColor> prizesInUse = new HashMap<>();
    private static final NamespacedKey key = new NamespacedKey(TravelFrog.getPlugin(), "TravelFrogType");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player p) {
            PlayerData data = PlayerDataManager.getPlayerData(p.getUniqueId());
            if (data == null) {
                p.sendMessage(ChatColor.RED + "Didn't work!");
                return true;
            }
            if (prizesInUse.containsKey(p)) return true;
            if (inUse) {
                p.sendMessage(ChatColor.RED + "The lottery is currently in use!");
                return true;
            }
            inUse = true;
            if (data.getTickets() < 5) {
                p.sendMessage(ChatColor.RED + "Not enough tickets!");
                return true;
            }

            data.decrementTickets(5);
            Location loc = new Location(p.getWorld(), 642.5, 74.5, 67.5);

            // roll
            // TODO is there a better way to do this
            FileConfiguration config = TravelFrog.getPlugin().getConfig();
            double gold = config.getDouble("lottery.gold");
            double red = config.getDouble("lottery.red");
            double green = config.getDouble("lottery.green");
            double blue = config.getDouble("lottery.blue");


            if (gold + red + green + blue > 100) {
                gold = 3;
                red = 6;
                green = 9;
                blue = 16;
            }

            double roll = Math.random() * 100;
            BallColor color;
            if (roll < gold) color = BallColor.GOLD;
            else if (roll < gold + red) color = BallColor.RED;
            else if (roll < gold + red + green) color = BallColor.GREEN;
            else if (roll < gold + red + green + blue) color = BallColor.BLUE;
            else color = BallColor.WHITE;

            prizesInUse.put(p, color);


            new BukkitRunnable() {
                int i = 0;
                @Override
                public void run() {
                    if (i < 8) {
                        p.getWorld().playSound(loc, Sound.ENTITY_PANDA_EAT, 1, 1);
                        i++;
                    } else this.cancel();

                }
            }.runTaskTimer(TravelFrog.getPlugin(), 0, 5);

            Bukkit.getScheduler().scheduleSyncDelayedTask(TravelFrog.getPlugin(), () -> {
                Item item = p.getWorld().dropItem(loc, color.getHead());
                item.setCanPlayerPickup(false);
                item.setTicksLived(5960);
            }, 40);

            Bukkit.getScheduler().scheduleSyncDelayedTask(TravelFrog.getPlugin(), () -> {
                Inventory inv = Bukkit.createInventory(null, 27, Component.text("Lottery"));
                inv.setItem(13, color.getHead());
                p.playSound(p, Sound.BLOCK_BELL_USE, 1, 1);
                p.openInventory(inv);
                inUse = false;
            }, 80);

        }
        return true;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getView().title().equals(Component.text("Lottery"))) {
            event.setCancelled(true);
            if (event.getSlot() == 13) {
                // InventoryCloseEvent will take care of this
                player.closeInventory();
            }
        } else if (event.getView().title().equals(Component.text("Choose a prize"))) {
            event.setCancelled(true);
            // must be in the top inventory though
            if (event.getInventory() != event.getClickedInventory()) return;
            ItemStack itemClicked = event.getCurrentItem();
            if (itemClicked == null) return;

            PersistentDataContainer container = itemClicked.getItemMeta().getPersistentDataContainer();
            if (container.has(key)) {
                ItemType type = ItemType.valueOf(container.get(key, PersistentDataType.STRING));
                PlayerData data = PlayerDataManager.getPlayerData(player.getUniqueId());
                data.incrementItemCount(type, 1);
                prizesInUse.remove(player);
                player.playSound(player, Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
                player.closeInventory();
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().title().equals(Component.text("Lottery"))
                || event.getView().title().equals(Component.text("Choose a prize"))) {
            Player player = (Player) event.getPlayer();
            openRewardInventory(player);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().title().equals(Component.text("Lottery"))
                || event.getView().title().equals(Component.text("Choose a prize"))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        prizesInUse.remove(event.getPlayer());
    }

    private static void openRewardInventory(Player player) {
        BallColor color = prizesInUse.get(player);
        if (color == null) return;

        PlayerData data = PlayerDataManager.getPlayerData(player.getUniqueId());
        if (color == BallColor.WHITE) {
            player.sendMessage(ChatColor.YELLOW + "You received one ticket as a consolation prize!");
            data.incrementTickets(1);
            prizesInUse.remove(player);
            player.closeInventory();
        } else {
            Inventory inv = Bukkit.createInventory(null, 27, Component.text("Choose a prize"));
            ItemType[] items = color.getItems();
            int index = 0;
            for (int slot = 11; slot < 16; slot++) {
                if (slot == 13) continue;
                inv.setItem(slot, Utils.createLotteryItem(items[index]));
                index++;
            }
            // Never open a new inventory on the same tick one is closed!
            Bukkit.getScheduler().runTask(TravelFrog.getPlugin(), () -> player.openInventory(inv));
        }
    }

    enum BallColor {
        WHITE("http://textures.minecraft.net/texture/ce236e3de8c164d2dff1c4a4ee38ddd6f7f0c0b0472e6abe14213429896a34e7",
                Component.text("WHITE", NamedTextColor.WHITE).decorate(TextDecoration.BOLD)),
        GOLD("http://textures.minecraft.net/texture/211ab3a1132c9d1ef835ea81d972ed9b5cd8ddff0a07c55a749bcfcf8df5",
                Component.text("GOLD", NamedTextColor.GOLD).decorate(TextDecoration.BOLD),
                ItemType.TICKET_BLUE, ItemType.TICKET_GREEN, ItemType.TICKET_ORANGE, ItemType.TICKET_WHITE),
        RED("http://textures.minecraft.net/texture/884e92487c6749995b79737b8a9eb4c43954797a6dd6cd9b4efce17cf475846",
                Component.text("RED", NamedTextColor.RED).decorate(TextDecoration.BOLD),
                ItemType.KONPEITO_GREEN, ItemType.KONPEITO_RED, ItemType.KONPEITO_PURPLE, ItemType.KONPEITO_YELLOW),
        GREEN("http://textures.minecraft.net/texture/5e48615df6b7ddf3ad495041876d9169bdc983a3fa69a2aca107e8f251f7687",
                Component.text("GREEN", NamedTextColor.GREEN).decorate(TextDecoration.BOLD),
                ItemType.CRACKERS_GREEN, ItemType.CRACKERS_RED, ItemType.CRACKERS_PURPLE, ItemType.CRACKERS_YELLOW),
        BLUE("http://textures.minecraft.net/texture/67e11db8f392727a1c459eb591dd8a0aa3861c27487e1384bb22f91573be7",
                Component.text("BLUE", NamedTextColor.BLUE).decorate(TextDecoration.BOLD),
                ItemType.STAR_BLUE, ItemType.STAR_GREEN, ItemType.STAR_ORANGE, ItemType.STAR_WHITE);

        private final String url;
        private final Component name;
        private final ItemType[] items;
        BallColor(String url, Component name, ItemType... items) {
            this.url = url;
            this.name = name;
            this.items = items;
        }

        public ItemStack getHead() {
            ItemStack item = SkullCreator.itemFromUrl(url);
            ItemMeta meta = item.getItemMeta();
            meta.displayName(Component.text("You pulled a ", NamedTextColor.WHITE)
                    .decoration(TextDecoration.ITALIC, false)
                    .append(name)
                    .append(Component.text(" marble!", NamedTextColor.WHITE)
                    .decoration(TextDecoration.ITALIC, false)));
            meta.lore(Collections.singletonList(Component.text("Click to receive your reward!", NamedTextColor.YELLOW)
                    .decoration(TextDecoration.ITALIC, false)));
            item.setItemMeta(meta);
            return item;
        }

        public ItemType[] getItems() {
            return items;
        }
    }


}
