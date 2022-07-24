package com.articreep.travelfrog.playerdata;

import com.articreep.travelfrog.TravelFrog;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {
    private static final Map<UUID, PlayerData> playerToInventoryMap = new HashMap<>();

    public static void registerPlayer(Player p) {
        if (playerToInventoryMap.containsKey(p.getUniqueId())) {
            Bukkit.getLogger().severe("For some reason, " + p.getName() + " already had a PlayerData object!");
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(TravelFrog.getPlugin(), () -> {
            PlayerData inventory = new PlayerData(p);
            try {
                inventory.load();
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }
            playerToInventoryMap.put(p.getUniqueId(), inventory);
        });
    }

    public static PlayerData getPlayerData(UUID uuid) {
        return playerToInventoryMap.get(uuid);
    }

    public static void unregisterPlayer(UUID uuid) {
        // Save everything to SQL, then remove from hashmap
        PlayerData inventory = playerToInventoryMap.get(uuid);
        if (inventory == null) {
            Bukkit.getLogger().severe("For some reason, " + uuid.toString() + " didn't have a PlayerData object!");
            return;
        }
        playerToInventoryMap.remove(uuid);
        Bukkit.getScheduler().runTaskAsynchronously(TravelFrog.getPlugin(), inventory::save);
    }

    public static void savePlayerData(UUID uuid) {
        // Save everything to SQL, then remove from hashmap
        PlayerData inventory = playerToInventoryMap.get(uuid);
        if (inventory == null) {
            Bukkit.getLogger().severe("For some reason, " + uuid.toString() + " didn't have a PlayerData object!");
            return;
        }
        inventory.save();
    }

}
