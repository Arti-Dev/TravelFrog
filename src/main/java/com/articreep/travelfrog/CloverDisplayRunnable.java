package com.articreep.travelfrog;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.SmallDripleaf;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

public class CloverDisplayRunnable extends BukkitRunnable {
    private final Set<Location> cloverSet;
    private final Player player;
    private static final SmallDripleaf topBlockData;
    private static final SmallDripleaf bottomBlockData;

    static {
        topBlockData = (SmallDripleaf) Material.SMALL_DRIPLEAF.createBlockData();
        bottomBlockData = (SmallDripleaf) Material.SMALL_DRIPLEAF.createBlockData();
        topBlockData.setHalf(Bisected.Half.TOP);
        bottomBlockData.setHalf(Bisected.Half.BOTTOM);
    }

    public CloverDisplayRunnable(Player player, Set<Location> cloverSet) {
        this.cloverSet = cloverSet;
        this.player = player;
    }

    @Override
    public void run() {
        // TODO Remember that these two checks exist.
        if (cloverSet.isEmpty()) this.cancel();

        for (Location loc : cloverSet) {
            if (loc.getY() == TravelFrog.getCloverYValue()) {
                player.sendBlockChange(loc, topBlockData);
            } else if (loc.getY() == TravelFrog.getCloverYValue() + 1) {
                player.sendBlockChange(loc, topBlockData);
                // Send a second block change underneath that block
                player.sendBlockChange(loc.clone().subtract(0, 1, 0), bottomBlockData);
            } else {
                cloverSet.remove(loc);
            }
        }
    }

    public boolean removeClover(Location target) {
        if (cloverSet.removeIf((l) -> (l.getBlockX() == target.getBlockX() && l.getBlockZ() == target.getBlockZ()))) {
            player.sendBlockChange(target, target.getBlock().getBlockData());
            // Update the block above or the block below depending on where the player clicked.
            Location otherLoc = target.clone();
            if (target.getY() == TravelFrog.getCloverYValue()) {
                otherLoc.add(0, 1, 0);
            } else if (target.getY() == TravelFrog.getCloverYValue() + 1) {
                otherLoc.subtract(0, 1, 0);
            }
            player.sendBlockChange(otherLoc, otherLoc.getBlock().getBlockData());

            // When breaking blocks rapidly sometimes they don't update, so try sending another packet a tick later
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.sendBlockChange(target, target.getBlock().getBlockData());
                    player.sendBlockChange(otherLoc, otherLoc.getBlock().getBlockData());
                }
            }.runTaskLater(TravelFrog.getPlugin(), 20);
            return true;
        }
        return false;

    }

    public int getCloverAmount() {
        return cloverSet.size();
    }

}
