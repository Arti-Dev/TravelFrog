package com.articreep.travelfrog.playerdata;

import com.articreep.travelfrog.CloverDisplayRunnable;
import com.articreep.travelfrog.CloverType;
import com.articreep.travelfrog.TravelFrog;
import com.articreep.travelfrog.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class PlayerData {

    // Only used sometimes
    private final Player player;

    private CloverDisplayRunnable runnable;
    private final UUID uuid;
    private int clovers;
    private int fourLeafClovers;
    private int lanterns;

    protected PlayerData(Player player) {
        this.player = player;
        uuid = player.getUniqueId();
    }

    protected void load() throws SQLException {
        clovers = CloverDatabase.getClovers(uuid);
        fourLeafClovers = InventoryDatabase.getFourLeafClovers(uuid);
        addToInventory(Material.SMALL_DRIPLEAF);
        lanterns = InventoryDatabase.getLanterns(uuid);

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

    public void incrementFourLeafCloverCount(int amount) {
        fourLeafClovers += amount;
        addToInventory(Material.SMALL_DRIPLEAF);
    }

    public void decrementFourLeafCloverCount(int amount) {
        fourLeafClovers -= amount;
    }

    // TODO Testing purposes. Take an enum later.
    // TODO Account for the fact that stacks only go to 64!
    private void addToInventory(Material material) {
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

    public CloverDisplayRunnable getRunnable() {
        return runnable;
    }
}
