package com.articreep.travelfrog.playerdata;

import com.articreep.travelfrog.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.*;

import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class PlayerData {

    // Only used sometimes
    private final Player player;

    private CloverDisplayRunnable runnable;
    private Map<ItemType, Integer> itemMap;
    // hasTool is updated on backpack/table load and inventory load (addToInventory method).
    // it determines whether a user possesses a single-buy item.
    private final Set<ItemType> hasSingleItem = new HashSet<>();
    // fixed size list
    private final List<ItemType> backpack = Arrays.asList(new ItemType[4]);
    private final List<ItemType> table = Arrays.asList(new ItemType[8]);
    private final UUID uuid;
    private int clovers;
    private int tickets;

    protected PlayerData(Player player) {
        this.player = player;
        uuid = player.getUniqueId();
    }

    // Can be run multiple times!
    protected void load() throws SQLException {
        clovers = CloverDatabase.getClovers(uuid);
        tickets = CloverDatabase.getTickets(uuid);
        itemMap = InventoryDatabase.getInventory(uuid);
        for (ItemType type : ItemType.valuesList()) {
            addToInventory(type);
        }
        int index = 0;
        for (ItemType type : BackpackDatabase.getContents(uuid)) {
            if (type.isSingleItem()) hasSingleItem.add(type);
            backpack.set(index, type);
            index++;
        }
        index = 0;
        for (ItemType type : TableDatabase.getContents(uuid)) {
            if (type.isSingleItem()) hasSingleItem.add(type);
            table.set(index, type);
            index++;
        }

        // Spawn the clovers in the field.

        // Import the amount of four-leaf and regular clovers.
        int fourLeafClovers = CloverDatabase.getFourLeafCloversWaiting(uuid);
        int importedClovers = CloverDatabase.getCloversWaiting(uuid);
        long generatedClovers = (CloverDatabase.getLastSeen(uuid).until(Instant.now(), ChronoUnit.MINUTES) / 6);
        long counter;

        // Import config
        FileConfiguration config = TravelFrog.getPlugin().getConfig();
        int cloverCap = config.getInt("clovers.maxClovers");
        int cloverBonusCap = config.getInt("clovers.maxCloverBonus");
        double fourLeafChance = config.getDouble("clovers.fourLeafChance") * 0.01;

        // Load clovers from previous session.

        // If these imported clovers amount to 25 or higher, do not generate any new clovers.
        if (importedClovers >= cloverCap) {
            counter = importedClovers;
        } else {
            // Total cannot go above 25
            counter = importedClovers + generatedClovers;
            if (counter > cloverCap) counter = cloverCap;
        }

        // This is the random bonus - default max is 5.
        // There cannot be any imported clovers, and the counter must be above 5.
        if (importedClovers == 0 && counter > 5) {
            counter = counter + (int) (Math.random() * cloverBonusCap);
        }

        // Hard cap at cloverCap + cloverBonusCap - just in case my logic has gone wrong.
        if (counter > cloverCap + cloverBonusCap) counter = cloverCap + cloverBonusCap;

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
            if (importedClovers <= 0 && Math.random() < fourLeafChance) {
                l = Utils.getRandomCloverLocation(player.getWorld(), CloverType.FOUR_LEAF_CLOVER, locationsUsed);
                fourLeafCloverSet.add(l);
            } else {
                l = Utils.getRandomCloverLocation(player.getWorld(), CloverType.CLOVER, locationsUsed);
                cloverSet.add(l);
            }
            locationsUsed.add(l);

        }

        if (runnable != null) runnable.cancel();

        runnable = new CloverDisplayRunnable(player, cloverSet, fourLeafCloverSet);
        runnable.runTaskTimer(TravelFrog.getPlugin(), 1, 5);

        // I'll allow clovers to regen while they're online - but clovers will not spawn while they're online. They'll have to log back in.
        // This mechanic is actually present in the actual game!
        CloverDatabase.updateLastSeen(uuid);

        Bukkit.getScheduler().runTask(TravelFrog.getPlugin(), () -> displayScoreboardToPlayer(uuid, clovers, tickets));
    }

    protected void save() {
        try {
            CloverDatabase.updateClovers(this);
            CloverDatabase.updateCloversWaiting(this);
            CloverDatabase.updateTickets(this);
            InventoryDatabase.updateInventory(this);
            BackpackDatabase.updateBackpack(this);
            TableDatabase.updateTable(this);
            runnable.cancel();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void incrementClovers(int amount) {
        // This increments only on the scoreboard and in PlayerData. It does not save to SQL.
        clovers += amount;
        updateScoreboard();
    }

    public void decrementClovers(int amount) {
        // This decrements only on the scoreboard and in PlayerData. It does not save to SQL.
        clovers -= amount;
        updateScoreboard();
    }

    public void setClovers(int clovers) {
        this.clovers = clovers;
        updateScoreboard();
    }

    public void incrementTickets(int amount) {
        // This increments only on the scoreboard and in PlayerData. It does not save to SQL.
        tickets += amount;
        updateScoreboard();
    }

    public void decrementTickets(int amount) {
        // This decrements only on the scoreboard and in PlayerData. It does not save to SQL.
        tickets -= amount;
        updateScoreboard();
    }

    public void setTickets(int tickets) {
        this.tickets = tickets;
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

    public void setItemCount(ItemType type, int amount) {
        if (type == ItemType.NONE) return;
        itemMap.put(type, amount);
        addToInventory(type);
    }

    public void clearItems() {
        itemMap.clear();
        for (ItemType type : ItemType.valuesList()) {
            itemMap.put(type, 0);
            addToInventory(type);
        }
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

        if (type.isSingleItem()) {
            if (amount > 0) {
                hasSingleItem.add(type);
            }
        }

        for (ItemStack item : inv.getContents()) {
            currentIndex++;
            if (item == null) continue;
            if (Utils.getItemType(item) == type) {

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


    private static void displayScoreboardToPlayer(UUID uuid, int clovers, int tickets) {

        Player p = Bukkit.getPlayer(uuid);
        if (p == null) return;

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();

        Objective objective = board.registerNewObjective("Title", "dummy",
                Component.text("Travel Frog").color(NamedTextColor.YELLOW), RenderType.INTEGER);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score cloverScore = objective.getScore(ChatColor.GREEN + "Clovers:");
        Score ticketScore = objective.getScore(ChatColor.YELLOW + "Tickets:");
        cloverScore.setScore(clovers);
        ticketScore.setScore(tickets);

        p.setScoreboard(board);
    }

    private void updateScoreboard() {
        Scoreboard board = player.getScoreboard();
        Objective objective = board.getObjective("Title");
        if (objective == null) {
            Bukkit.getLogger().severe(player.getName() + "'s scoreboard couldn't be updated!");
            return;
        }
        Score cloverScore = objective.getScore(ChatColor.GREEN + "Clovers:");
        Score ticketScore = objective.getScore(ChatColor.YELLOW + "Tickets:");
        cloverScore.setScore(clovers);
        ticketScore.setScore(tickets);
    }

    public int getClovers() {
        return clovers;
    }

    public int getTickets() {
        return tickets;
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

    public boolean hasSingleItem(ItemType type) {
        return hasSingleItem.contains(type);
    }
}
