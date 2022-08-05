package com.articreep.travelfrog.commands;

import com.articreep.travelfrog.playerdata.PlayerData;
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

public class CloversCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (args.length <= 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /clovers <add/remove/set/check> <player> [amount]");

        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("check")) {
                // Is it an online player?
                Player player = Bukkit.getPlayer(args[1]);
                if (player == null) {
                    sender.sendMessage(ChatColor.RED + "Invalid player! UUID searching in database will come soon.");
                    return true;
                }
                sender.sendMessage(ChatColor.GREEN + player.getName() + " has " + PlayerDataManager.getPlayerData(player.getUniqueId()).getClovers() + " clovers");
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /clovers <add/remove/set/check> <player> [amount]");
            }

        } else if (args.length == 3) {
            // Parse first
            int amount;
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "That's not an integer!");
                return true;
            }

            // Is it an online player?
            Player player = Bukkit.getPlayer(args[1]);

            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Invalid player! UUID searching in database will come soon.");
                return true;
            }

            PlayerData data = PlayerDataManager.getPlayerData(player.getUniqueId());
            if (args[0].equalsIgnoreCase("add")) {
                if (data.getClovers() + amount < 0) {
                    sender.sendMessage(ChatColor.RED + "That would end up with a negative amount of clovers!");
                    return true;
                }
                data.incrementClovers(amount);
                sender.sendMessage(ChatColor.GREEN + "Successfully added " + amount + " clovers to " + player.getName());

            } else if (args[0].equalsIgnoreCase("remove")) {
                if (data.getClovers() - amount < 0) {
                    sender.sendMessage(ChatColor.RED + "That would end up with a negative amount of clovers!");
                    return true;
                }
                PlayerDataManager.getPlayerData(player.getUniqueId()).decrementClovers(amount);
                sender.sendMessage(ChatColor.GREEN + "Successfully removed " + amount + " clovers from " + player.getName());

            } else if (args[0].equalsIgnoreCase("set")) {
                if (amount < 0) {
                    sender.sendMessage(ChatColor.RED + "That would end up with a negative amount of clovers!");
                    return true;
                }
                PlayerDataManager.getPlayerData(player.getUniqueId()).setClovers(amount);
                sender.sendMessage(ChatColor.GREEN + "Successfully set " + player.getName() + "'s clovers to " + amount);

            } else {
                sender.sendMessage(ChatColor.RED + "Invalid operation.");
                sender.sendMessage(ChatColor.RED + "Usage: /clovers <add/remove/set/check> <player> [amount]");
            }

        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /clovers <add/remove/set/check> <player> [amount]");
            return true;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        final ArrayList<String> strings = new ArrayList<>();
        final List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            strings.add("add");
            strings.add("remove");
            strings.add("check");
            strings.add("set");
            StringUtil.copyPartialMatches(args[0], strings, completions);
        } else if (args.length == 2) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                strings.add(player.getName());
            }
            StringUtil.copyPartialMatches(args[1], strings, completions);
        }
        Collections.sort(completions);
        return completions;
    }
}
