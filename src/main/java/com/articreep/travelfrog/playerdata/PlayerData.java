package com.articreep.travelfrog.playerdata;

import com.articreep.travelfrog.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.*;

import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class PlayerData {
    private static final NamespacedKey key = new NamespacedKey(TravelFrog.getPlugin(), "TravelFrogType");

    // Only used sometimes
    private final Player player;

    private CloverDisplayRunnable runnable;
    private final Map<ItemType, Integer> itemMap = new HashMap<>();
    // fixed size list
    private final List<ItemType> backpack = Arrays.asList(new ItemType[4]);
    private final List<ItemType> table = Arrays.asList(new ItemType[8]);
    private final UUID uuid;
    private int clovers;

    protected PlayerData(Player player) {
        this.player = player;
        uuid = player.getUniqueId();
    }

    protected void load() throws SQLException {
        clovers = CloverDatabase.getClovers(uuid);
        itemMap.put(ItemType.FOUR_LEAF_CLOVER, InventoryDatabase.getFourLeafClovers(uuid));
        itemMap.put(ItemType.LANTERN, InventoryDatabase.getLanterns(uuid));
        itemMap.put(ItemType.BREAD, InventoryDatabase.getBread(uuid));
        for (ItemType type : ItemType.values()) {
            // except NONE lol
            if (type == ItemType.NONE) continue;
            addToInventory(type);
        }
        int index = -1;
        for (ItemType type : BackpackDatabase.getContents(uuid)) {
            index++;
            backpack.set(index, type);
        }
        index = -1;
        for (ItemType type : TableDatabase.getContents(uuid)) {
            index++;
            table.set(index, type);
        }

        // Spawn the clovers in the field.

        // Import the amount of four-leaf and regular clovers.
        int fourLeafClovers = CloverDatabase.getFourLeafCloversWaiting(uuid);
        int importedClovers = CloverDatabase.getCloversWaiting(uuid);
        long generatedClovers = (CloverDatabase.getLastSeen(uuid).until(Instant.now(), ChronoUnit.MINUTES) / 6);
        long counter;

        // Load clovers from previous session.

        // If these imported clovers amount to 25 or higher, do not generate any new clovers.
        if (importedClovers >= 25) {
            counter = importedClovers;
        } else {
            // Total cannot go above 25
            counter = importedClovers + generatedClovers;
            if (counter > 25) counter = 25;
        }

        // This is the random bonus, from 0 to 5.
        // There cannot be any imported clovers, and the counter must be above 5.
        if (importedClovers == 0 && counter > 5) {
            counter = counter + (int) (Math.random() * 5);
        }

        // Hard cap at 30 - just in case my logic has gone wrong.
        if (counter > 30) counter = 30;

        // Now that we know how many clovers to add to the field, start making some sets.
        final Set<Location> cloverSet = new HashSet<>();
        final Set<Location> fourLeafCloverSet = new HashSet<>();
        final Set<Location> locationsUsed = new HashSet<>();

        // Four leaf clovers count towards the total clover count, but are stored in a separate Set.

        // Add four-leaf clovers first
        for (Location l; counter > 0 && fourLeafClovers > 0; fourLeafClovers--, counter--) {

            l = Utils.getRandomCloverLocation(player.getWorld(), CloverType.FOUR_LEAF_CLOVER, locationsUsed);
            fourLeafCloverSet.add(l);
            locationsUsed.add(l);

        }

        // Next add the rest of the normal clovers
        for (Location l; counter > 0; counter--, importedClovers--) {

            // Every regular clover that is newly generated has a 1/200 (0.005) chance to be a four-leaf clover.
            if (importedClovers <= 0 && Math.random() < 0.005) {
                l = Utils.getRandomCloverLocation(player.getWorld(), CloverType.FOUR_LEAF_CLOVER, locationsUsed);
                fourLeafCloverSet.add(l);
            } else {
                l = Utils.getRandomCloverLocation(player.getWorld(), CloverType.CLOVER, locationsUsed);
                cloverSet.add(l);
            }
            locationsUsed.add(l);

        }

        runnable = new CloverDisplayRunnable(player, cloverSet, fourLeafCloverSet);
        runnable.runTaskTimer(TravelFrog.getPlugin(), 1, 5);

        // I'll allow clovers to regen while they're online - but clovers will not spawn while they're online. They'll have to log back in.
        // This mechanic is actually present in the actual game!
        CloverDatabase.updateLastSeen(uuid);

        Bukkit.getScheduler().runTask(TravelFrog.getPlugin(), () -> displayScoreboardToPlayer(uuid, clovers));
    }

    protected void save() {
        try {
            CloverDatabase.updateClovers(this);
            CloverDatabase.updateCloversWaiting(this);
            InventoryDatabase.updateLanterns(this);
            InventoryDatabase.updateFourLeafClovers(this);
            InventoryDatabase.updateBread(this);
            BackpackDatabase.updateBackpack(this);
            TableDatabase.updateTable(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void incrementCloverCount(int amount) {
        // This increments only on the scoreboard and in PlayerData. It does not save to SQL.
        clovers += amount;
        updateScoreboard();
    }

    public void decrementCloverCount(int amount) {
        // This decrements only on the scoreboard and in PlayerData. It does not save to SQL.
        clovers -= amount;
        updateScoreboard();
    }

    public void incrementItemCount(ItemType type, int amount) {
        if (type == ItemType.NONE) return;
        itemMap.put(type, itemMap.get(type) + amount);
        addToInventory(type);
    }

    public void decrementItemCount(ItemType type, int amount) {
        if (type == ItemType.NONE) return;
        itemMap.put(type, itemMap.get(type) - amount);
        addToInventory(type);
    }

    public void setInBackpack(int index, ItemType type) {
        decrementItemCount(type, 1);
        incrementItemCount(backpack.get(index), 1);
        backpack.set(index, type);
    }

    public void setInTable(int index, ItemType type) {
        decrementItemCount(type, 1);
        incrementItemCount(table.get(index), 1);
        table.set(index, type);
    }

    public void removeFromBackpack(int index) {
        incrementItemCount(backpack.get(index), 1);
        backpack.set(index, ItemType.NONE);
    }

    public void removeFromTable(int index) {
        incrementItemCount(table.get(index), 1);
        table.set(index, ItemType.NONE);
    }

    private void addToInventory(ItemType type) {
        // If the user doesn't have that item in the inventory yet, add it
        // If the user has some in their inventory, just modify the lore and the amount
        // If the user has multiple items with the same type in their inventory, modify the first one but remove the second one

        Inventory inv = player.getInventory();
        int amount = itemMap.get(type);
        boolean hasItem = false;
        int currentIndex = -1;

        for (ItemStack item : inv.getContents()) {
            currentIndex++;
            if (item == null) continue;
            PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
            if (!container.has(key)) continue;
            String string = container.get(key, PersistentDataType.STRING);
            if (ItemType.valueOf(string) == type) {

                if (hasItem || amount <= 0) {
                    inv.clear(currentIndex);
                } else {
                    hasItem = true;
                    Utils.updateInventoryItem(item, type, amount);
                }
            }
        }

        if (!hasItem && amount > 0) {
            ItemStack item = new ItemStack(type.getMaterial());
            Utils.updateInventoryItem(item, type, amount);
            //TODO Ensure this item is not stored in the hotbar
            inv.addItem(item);
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

    public int getClovers() {
        return clovers;
    }

    public int getItemCount(ItemType type) {
        return itemMap.get(type);
    }

    public UUID getUuid() {
        return uuid;
    }

    public CloverDisplayRunnable getRunnable() {
        return runnable;
    }

    public List<ItemType> getBackpack() {
        return backpack;
    }

    public List<ItemType> getTable() {
        return table;
    }
}
