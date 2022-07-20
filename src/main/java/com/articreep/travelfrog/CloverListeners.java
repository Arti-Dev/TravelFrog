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

        long counter = 0;

        // The counter increases 5 per 1800s (30 minutes), max 25.
        // When the player logs in, add a potential bonus of 0 to 5. If the counter is below 5 don't do this.

        // If the player already has clovers waiting in the field from previous sessions, load that, and add it to the clovers that generated now.
        // If this value is non-zero don't add the random bonus.

        counter = CloverDatabase.getCloversWaiting(p) +
                (CloverDatabase.getLastSeen(p).until(Instant.now(), ChronoUnit.MINUTES) / 6);

        if (counter > 25) counter = 25;

        if (counter > 5 && CloverDatabase.getCloversWaiting(p) == 0) {
            counter = counter + (int) (Math.random() * 5);
        }

        // Add all of these to a Set.
        final Set<Location> locationSet = new HashSet<>();
        for (; counter > 0; counter--) locationSet.add(getRandomCloverLocation(locationSet, p.getWorld()));

        CloverDisplayRunnable runnable = new CloverDisplayRunnable(p, locationSet);

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
            if (runnableMap.get(p).removeClover(clickedLoc)) {
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

    private static Location getRandomCloverLocation(Set<Location> locationSet, World w) {
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
            if (Math.random() < 0.3) target.add(0, 1, 0);

            return target;
        }
        return null;
    }
}
