package com.articreep.travelfrog;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.*;

import java.sql.SQLException;
import java.util.*;

public class PlayerInventory {
    private static final Map<UUID, PlayerInventory> playerToInventoryMap = new HashMap<>();

    // Only used sometimes
    private final Player player;

    private final UUID uuid;
    private int clovers;
    private int fourLeafClovers;
    private int lanterns;

    private PlayerInventory(Player player) {
        this.player = player;
        uuid = player.getUniqueId();
        load();
    }

    private void load() {
        try {
            clovers = CloverDatabase.getClovers(uuid);
            fourLeafClovers = InventoryDatabase.getFourLeafClovers(uuid);
            addToInventory(Material.SMALL_DRIPLEAF);
            lanterns = InventoryDatabase.getLanterns(uuid);
            displayScoreboardToPlayer(uuid, clovers);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void save() {
        try {
            CloverDatabase.updateClovers(this);
            CloverDatabase.updateCloversWaiting(uuid);
            InventoryDatabase.updateLanterns(this);
            InventoryDatabase.updateFourLeafClovers(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void incrementCloverCount(int amount) {
        // This increments only on the scoreboard and in PlayerInventory. It does not save to SQL.
        clovers += amount;
        updateScoreboard();
    }

    protected void decrementCloverCount(int amount) {
        // This decrements only on the scoreboard and in PlayerInventory. It does not save to SQL.
        clovers -= amount;
        updateScoreboard();
    }

    protected void incrementFourLeafCloverCount(int amount) {
        fourLeafClovers += amount;
        addToInventory(Material.SMALL_DRIPLEAF);
    }

    protected void decrementFourLeafCloverCount(int amount) {
        fourLeafClovers -= amount;
    }

    // TODO Testing purposes. Take an enum later.
    // TODO Account for the fact that stacks only go to 64!
    protected void addToInventory(Material material) {
        if (fourLeafClovers == 0) return;
        // If the user doesn't have that item in the inventory yet, add it
        Inventory inv = player.getInventory();
        if (!inv.contains(material)) {
            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            meta.lore(Collections.singletonList(Component.text(fourLeafClovers)));
            item.setItemMeta(meta);
            item.setAmount(fourLeafClovers);
            inv.addItem(item);
        } else {
            for (ItemStack item : inv.getContents()) {
                if (item == null) continue;
                if (item.getType() == material) {
                    ItemMeta meta = item.getItemMeta();
                    meta.lore(Collections.singletonList(Component.text(fourLeafClovers)));
                    item.setItemMeta(meta);
                    item.setAmount(fourLeafClovers);
                }
            }
        }
    }

    private static void displayScoreboardToPlayer(UUID uuid, int clovers) {

        Player p = Bukkit.getPlayer(uuid);
        if (p == null) return;

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();

        Objective objective = board.registerNewObjective("Title", "dummy",
                Component.text("Travel Frog").color(NamedTextColor.YELLOW), RenderType.INTEGER);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score score = objective.getScore(ChatColor.GREEN + "Clovers:");
        score.setScore(clovers);

        p.setScoreboard(board);
    }

    private void updateScoreboard() {
        Scoreboard board = player.getScoreboard();
        Objective objective = board.getObjective("Title");
        Score score = objective.getScore(ChatColor.GREEN + "Clovers:");
        score.setScore(clovers);
    }

    protected static PlayerInventory registerPlayer(Player p) {
        if (playerToInventoryMap.containsKey(p.getUniqueId())) {
            return null;
        }

        PlayerInventory inventory = new PlayerInventory(p);
        playerToInventoryMap.put(p.getUniqueId(), inventory);
        return inventory;
    }

    protected static PlayerInventory getPlayerInventory(UUID uuid) {
        return playerToInventoryMap.get(uuid);
    }

    protected static void unregisterPlayer(UUID uuid) {
        // Save everything to SQL, then remove from hashmap
        PlayerInventory inventory = playerToInventoryMap.get(uuid);
        if (inventory == null) {
            return;
        }
        inventory.save();
        playerToInventoryMap.remove(uuid);
    }

    public int getClovers() {
        return clovers;
    }

    public int getFourLeafClovers() {
        return fourLeafClovers;
    }

    public int getLanterns() {
        return lanterns;
    }

    public UUID getUuid() {
        return uuid;
    }
}
