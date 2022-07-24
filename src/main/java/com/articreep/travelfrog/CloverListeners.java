package com.articreep.travelfrog;

import com.articreep.travelfrog.playerdata.PlayerData;
import com.articreep.travelfrog.playerdata.PlayerDataManager;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class CloverListeners implements Listener {


    @EventHandler
    protected void onPlayerJoin(PlayerJoinEvent event) {
        PlayerDataManager.registerPlayer(event.getPlayer());
    }

    @EventHandler
    protected void onPlayerLeave(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        PlayerDataManager.unregisterPlayer(uuid);
    }

    @EventHandler
    protected void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        UUID uuid = event.getPlayer().getUniqueId();
        PlayerData data = PlayerDataManager.getPlayerData(uuid);
        Location clickedLoc = event.getClickedBlock().getLocation();

        // Check y-value, must be the set value or one higher
        if (clickedLoc.getY() == TravelFrog.getCloverYValue() || clickedLoc.getY() == TravelFrog.getCloverYValue() + 1) {
            // Check if they actually broke a clover
            CloverType cloverType = data.getRunnable().removeClover(clickedLoc);
            if (cloverType == null) return;
            if (cloverType == CloverType.CLOVER) {
                data.incrementCloverCount(1);
            } else if (cloverType == CloverType.FOUR_LEAF_CLOVER) {
                data.incrementFourLeafCloverCount(1);
            }
        }
    }




}
