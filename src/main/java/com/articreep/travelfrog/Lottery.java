package com.articreep.travelfrog;

import com.articreep.travelfrog.playerdata.PlayerData;
import com.articreep.travelfrog.playerdata.PlayerDataManager;
import dev.dbassett.skullcreator.SkullCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class Lottery implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player p) {
            PlayerData data = PlayerDataManager.getPlayerData(p.getUniqueId());
            if (data == null) {
                p.sendMessage(ChatColor.RED + "Didn't work!");
                return true;
            }
            if (data.getTickets() < 5) {
                p.sendMessage(ChatColor.RED + "Not enough tickets!");
                return true;
            }

            data.decrementTickets(5);
            Location loc = new Location(p.getWorld(), 642, 74, 67);

            new BukkitRunnable() {
                int i = 0;
                @Override
                public void run() {
                    if (i < 8) {
                        p.getWorld().playSound(loc, Sound.ENTITY_PANDA_EAT, 1, 1);
                        i++;
                    } else this.cancel();

                }
            }.runTaskTimer(TravelFrog.getPlugin(), 0, 5);

            Bukkit.getScheduler().scheduleSyncDelayedTask(TravelFrog.getPlugin(), () -> p.getWorld().dropItem(loc, BallColor.WHITE.getHead()), 40);
        }
        return true;
    }

    enum BallColor {
        WHITE("http://textures.minecraft.net/texture/ce236e3de8c164d2dff1c4a4ee38ddd6f7f0c0b0472e6abe14213429896a34e7");

        private final String url;
        BallColor(String url) {
            this.url = url;
        }

        public ItemStack getHead() {
            return SkullCreator.itemFromUrl(url);
        }
    }


}
