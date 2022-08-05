package com.articreep.travelfrog;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.SmallDripleaf;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.Set;

public class CloverDisplayRunnable extends BukkitRunnable {
    private final Set<Location> cloverSet;
    private final Set<Location> fourLeafCloverSet;
    private final Player player;
    private static final SmallDripleaf topBlockData;
    private static final SmallDripleaf bottomBlockData;
    private static final BlockData fourLeafData;

    static {
        topBlockData = (SmallDripleaf) Material.SMALL_DRIPLEAF.createBlockData();
        bottomBlockData = (SmallDripleaf) Material.SMALL_DRIPLEAF.createBlockData();
        topBlockData.setHalf(Bisected.Half.TOP);
        bottomBlockData.setHalf(Bisected.Half.BOTTOM);
        fourLeafData = Material.MANGROVE_PROPAGULE.createBlockData();
    }

    public CloverDisplayRunnable(Player player, Set<Location> cloverSet, Set<Location> fourLeafCloverSet) {
        this.cloverSet = cloverSet;
        this.player = player;
        this.fourLeafCloverSet = fourLeafCloverSet;
    }

    @Override
    public void run() {

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

        for (Location loc : fourLeafCloverSet) {
            player.sendBlockChange(loc, fourLeafData);
        }
    }

    public CloverType removeClover(Location target) {
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


            return CloverType.CLOVER;
        } else if (fourLeafCloverSet.remove(target)) {
            player.sendBlockChange(target, target.getBlock().getBlockData());
            player.sendMessage(ChatColor.GREEN + "You got a four-leaf clover! It's been added to your inventory.");
            return CloverType.FOUR_LEAF_CLOVER;
        }
        return null;

    }

    public int getCloverAmount() {
        return cloverSet.size() + fourLeafCloverSet.size();
    }

    public int getFourLeafCloverAmount() {
        return fourLeafCloverSet.size();
    }

    /**
     * Attempts to cancel the task.
     * Additionally, removes all clovers from the client's view.
     */
    @Override
    public void cancel() {
        for (Location loc : cloverSet) {
            player.sendBlockChange(loc, loc.getBlock().getBlockData());
            loc.subtract(0, 1, 0);
            player.sendBlockChange(loc, loc.getBlock().getBlockData());
        }
        super.cancel();
    }

}
