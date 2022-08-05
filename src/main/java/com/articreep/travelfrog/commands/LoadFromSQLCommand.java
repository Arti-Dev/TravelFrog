package com.articreep.travelfrog.commands;

import com.articreep.travelfrog.TravelFrog;
import com.articreep.travelfrog.playerdata.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LoadFromSQLCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /loadfromsql <player/everyone>");
            return true;
        }
        // There hopefully shouldn't be someone named "everyone" joining my server
        if (args[0].equalsIgnoreCase("everyone")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                PlayerDataManager.reloadWithoutSaving(p.getUniqueId());
            }
            sender.sendMessage(ChatColor.GREEN + "Everyone's SQL data was loaded successfully.");
            return true;
        }

        Player player = Bukkit.getPlayer(args[0]);

        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Invalid player!");
            return true;
        }

        PlayerDataManager.reloadWithoutSaving(player.getUniqueId());
        sender.sendMessage(ChatColor.GREEN + "SQL data loaded successfully for " + player.getName());
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        final ArrayList<String> strings = new ArrayList<>();
        final List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            strings.add("everyone");
            for (Player p : Bukkit.getOnlinePlayers()) {
                strings.add(p.getName());
            }
            StringUtil.copyPartialMatches(args[0], strings, completions);
        }
        Collections.sort(completions);
        return completions;
    }
}
