package com.articreep.travelfrog;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.*;

import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class CloverListeners implements Listener {

    protected static Map<Player, CloverDisplayRunnable> runnableMap = new HashMap<>();

    @EventHandler
    protected void onPlayerJoin(PlayerJoinEvent event) throws SQLException {
        Player p = event.getPlayer();
        displayScoreboardToPlayer(p);

        // Spawn the clovers in the field.

        // Import the amount of four-leaf and regular clovers.
        int fourLeafClovers = CloverDatabase.getFourLeafCloversWaiting(p);
        int importedClovers = CloverDatabase.getCloversWaiting(p);
        long generatedClovers = (CloverDatabase.getLastSeen(p).until(Instant.now(), ChronoUnit.MINUTES) / 6);
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

        // Four leaf clovers count towards the total clover count, but are stored in a separate Set.

        // Add four-leaf clovers first
        for (; counter > 0 && fourLeafClovers > 0; fourLeafClovers--, counter--) {
            fourLeafCloverSet.add(getRandomCloverLocation(fourLeafCloverSet, p.getWorld(), CloverType.FOUR_LEAF_CLOVER));
        }

        // Next add the rest of the normal clovers.
        for (; counter > 0; counter--, importedClovers--) {
            // Every regular clover that is newly generated has a 1/200 (0.005) chance to be a four-leaf clover.
            if (importedClovers <= 0 && Math.random() < 0.005) {
                fourLeafCloverSet.add(getRandomCloverLocation(fourLeafCloverSet, p.getWorld(), CloverType.FOUR_LEAF_CLOVER));
            } else {
                cloverSet.add(getRandomCloverLocation(cloverSet, p.getWorld(), CloverType.CLOVER));
            }
        }

        // Use a BukkitRunnable to display all the clovers to the player.
        CloverDisplayRunnable runnable = new CloverDisplayRunnable(p, cloverSet, fourLeafCloverSet);

        runnable.runTaskTimer(TravelFrog.getPlugin(), 1, 5);
        runnableMap.put(p, runnable);

        // I'll allow clovers to regen while they're online - but clovers will not spawn while they're online. They'll have to log back in.
        // This mechanic is actually present in the actual game!
        CloverDatabase.updateLastSeen(event.getPlayer());
    }

    @EventHandler
    protected void onPlayerLeave(PlayerQuitEvent event) throws SQLException {
        // Basically save everything and remove them from the map
        // If they're not in the map somehow just return
        Player p = event.getPlayer();
        if (!runnableMap.containsKey(p)) return;

        CloverDatabase.updateClovers(p);
        CloverDatabase.updateCloversWaiting(p);
        runnableMap.get(p).cancel();
        runnableMap.remove(p);
    }

    @EventHandler
    protected void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        Player p = event.getPlayer();
        Location clickedLoc = event.getClickedBlock().getLocation();

        // Check y-value, must be the set value or one higher
        if (clickedLoc.getY() == TravelFrog.getCloverYValue() || clickedLoc.getY() == TravelFrog.getCloverYValue() + 1) {
            // Check if they actually broke a clover
            if (runnableMap.get(p).removeClover(clickedLoc) == CloverType.CLOVER) {
                incrementCloverCount(event.getPlayer(), 1);
            }
        }
    }

    protected static void displayScoreboardToPlayer(Player p) {
        int clovers;

        //TODO put EVERYTHING in the try catch

        try {
            clovers = CloverDatabase.getClovers(p);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();

        Objective objective = board.registerNewObjective("Title", "dummy",
                Component.text("Travel Frog").color(NamedTextColor.YELLOW), RenderType.INTEGER);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score score = objective.getScore(ChatColor.GREEN + "Clovers:");
        score.setScore(clovers);

        p.setScoreboard(board);
    }

    protected static void incrementCloverCount(Player p, int amount) {
        // This increments only on the scoreboard. It does not save to SQL.
        Scoreboard board = p.getScoreboard();
        Objective objective = board.getObjective("Title");
        Score score = objective.getScore(ChatColor.GREEN + "Clovers:");
        score.setScore(score.getScore() + amount);
    }

    private static Location getRandomCloverLocation(Set<Location> locationSet, World w, CloverType type) {
        // TODO Will hardcode for now :)
        Random random = new Random();

        Location loc1 = new Location(w, 634, 72, 83);
        Location loc2 = new Location(w, 644, 72, 93);

        for (int i = 0; i < 101; i++) {
            int x = random.nextInt(loc2.getBlockX() - loc1.getBlockX() + 1) + loc1.getBlockX();
            int z = random.nextInt(loc2.getBlockZ() - loc1.getBlockZ() + 1) + loc1.getBlockZ();

            // Conditions: Material is air, bottom is muddy mangrove roots

            Location target = new Location(w, x, TravelFrog.getCloverYValue(), z);
            if (target.getBlock().getType() != Material.AIR) continue;
            if (target.clone().subtract(0, 1, 0).getBlock().getType() != Material.MUDDY_MANGROVE_ROOTS) continue;

            // Check if these coordinates are already in the Set
            boolean contains = false;
            for (Location l : locationSet) {
                if (l.getBlockX() == x && l.getBlockZ() == z) {
                    contains = true;
                    break;
                }
            }
            if (contains) continue;

            // 30% chance that the clover spawns 1 higher than usual
            if (type == CloverType.CLOVER && Math.random() < 0.3) target.add(0, 1, 0);

            return target;
        }
        return null;
    }
}
