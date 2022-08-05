package com.articreep.travelfrog.commands;

import com.articreep.travelfrog.ItemType;
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

public class FrogItemsCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (args.length <= 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /frogitems <add/remove/set/check> <player> <itemtype> [amount]");

        } else if (args.length == 3) {

            // Is it an online player?
            Player player = Bukkit.getPlayer(args[1]);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Invalid player! UUID searching in database will come soon.");
                return true;
            }

            PlayerData data = PlayerDataManager.getPlayerData(player.getUniqueId());

            if (args[0].equalsIgnoreCase("check")) {
                ItemType type = ItemType.toEnum(args[2]);
                if (type == ItemType.NONE) {
                    sender.sendMessage(ChatColor.RED + "Invalid item type!");
                    return true;
                }
                sender.sendMessage(ChatColor.GREEN + player.getName() + " has " +
                        data.getItemCount(type) + " " + type.name());

            } else if (args[0].equalsIgnoreCase("remove")) {
                // If the action is remove and the type is "all" just remove everything lol
                if (args[2].equalsIgnoreCase("ALL")) {
                    data.clearItems();
                    sender.sendMessage(ChatColor.GREEN + "Cleared all items from " + player.getName() + "!");
                    return true;
                } else {
                    sender.sendMessage(ChatColor.RED + "Usage: /frogitems <add/remove/set/check> <player> <itemtype> [amount]");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /frogitems <add/remove/set/check> <player> <itemtype> [amount]");
            }

        } else if (args.length == 4) {

            int amount;
            try {
                amount = Integer.parseInt(args[3]);
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

            ItemType type = ItemType.toEnum(args[2]);
            if (type == ItemType.NONE) {
                sender.sendMessage(ChatColor.RED + "Invalid item type!");
                return true;
            }

            if (args[0].equalsIgnoreCase("add")) {
                if (data.getItemCount(type) + amount < 0) {
                    sender.sendMessage(ChatColor.RED + "That would end up with a negative amount!");
                    return true;
                }
                data.incrementItemCount(type, amount);
                sender.sendMessage(ChatColor.GREEN + "Successfully added " + amount + " " + type.name() + " to " + player.getName());

            } else if (args[0].equalsIgnoreCase("remove")) {
                if (data.getItemCount(type) - amount < 0) {
                    sender.sendMessage(ChatColor.RED + "That would end up with a negative amount!");
                    return true;
                }
                data.decrementItemCount(type, amount);
                sender.sendMessage(ChatColor.GREEN + "Successfully removed " + amount + " " + type.name() + " from " + player.getName());

            } else if (args[0].equalsIgnoreCase("set")) {
                if (amount < 0) {
                    sender.sendMessage(ChatColor.RED + "That would end up with a negative amount!");
                    return true;
                }
                data.setItemCount(type, amount);
                sender.sendMessage(ChatColor.GREEN + "Successfully set " + player.getName() + "'s " + type.name() +  " to " + amount);

            } else {
                sender.sendMessage(ChatColor.RED + "Invalid operation.");
                sender.sendMessage(ChatColor.RED + "Usage: /frogitems <add/remove/set/check> <player> <itemtype> [amount]");
            }

        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /frogitems <add/remove/set/check> <player> <itemtype> [amount]");
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
        } else if (args.length == 3) {
            for (ItemType type : ItemType.valuesList()) {
                strings.add(type.name());
            }
            strings.add("ALL");
            StringUtil.copyPartialMatches(args[2], strings, completions);
        }
        Collections.sort(completions);
        return completions;
    }
}
