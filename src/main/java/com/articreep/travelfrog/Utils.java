package com.articreep.travelfrog;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.Random;
import java.util.Set;

public class Utils {

    /**
     * Gets a random location where a clover can be placed. The location may change depending on the clover type.
     * @param w The world to generate Locations for
     * @param type Type of clover. Four-leaf clovers only spawn one block tall.
     * @param locationSetToExclude When a location is generated, this method will check against this list and will try generating again if one matches.
     * @return A valid Location for a clover to be spawned on.
     */
    public static Location getRandomCloverLocation(World w, CloverType type, Set<Location> locationSetToExclude) {
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
            for (Location l : locationSetToExclude) {
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
