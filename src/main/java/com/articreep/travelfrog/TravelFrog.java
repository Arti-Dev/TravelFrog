package com.articreep.travelfrog;

import com.articreep.travelfrog.commands.CloversCommand;
import com.articreep.travelfrog.commands.FrogItemsCommand;
import com.articreep.travelfrog.commands.LoadFromSQLCommand;
import com.articreep.travelfrog.commands.TicketsCommand;
import com.articreep.travelfrog.features.Backpack;
import com.articreep.travelfrog.features.Lottery;
import com.articreep.travelfrog.features.ShopListeners;
import com.articreep.travelfrog.features.Table;
import com.articreep.travelfrog.playerdata.PlayerDataManager;
import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class TravelFrog extends JavaPlugin implements CommandExecutor {
    private static TravelFrog plugin;
    private final static MysqlDataSource dataSource = new MysqlConnectionPoolDataSource();
    private static int cloverYValue;

    @Override
    public void onEnable() {
        plugin = this;

        // Load config
        saveDefaultConfig();

        FileConfiguration config = getConfig();
        dataSource.setServerName(config.getString("database.host"));
        dataSource.setPortNumber(config.getInt("database.port"));
        dataSource.setDatabaseName(config.getString("database.database"));
        dataSource.setUser(config.getString("database.user"));
        dataSource.setPassword(config.getString("database.password"));

        // Test the connection, if it fails do not load the plugin
        try {
            testDatabaseConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }

        // Init SQL table if it doesn't already exist
        // TODO do the other one
        String sql = "CREATE TABLE IF NOT EXISTS cloverTable(" +
                "uuid CHAR(36) NOT NULL," +
                "clovers INT DEFAULT 0 NOT NULL," +
                "lastSeen TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL," +
                "cloversWaiting INT DEFAULT 25 NOT NULL," +
                "fourLeafCloversWaiting INT DEFAULT 0 NOT NULL," +
                "tickets INT DEFAULT 0 NOT NULL," +
                "PRIMARY KEY (uuid));";
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Pull the clover height from the config
        cloverYValue = config.getInt("clovers.y-level");

        // Import everyone's data
        for (Player p : Bukkit.getOnlinePlayers()) {
            PlayerDataManager.registerPlayer(p);
        }

        Lottery lottery = new Lottery();
        Backpack backpack = new Backpack();
        Table table = new Table();

        getServer().getPluginManager().registerEvents(new CloverListeners(), this);
        getServer().getPluginManager().registerEvents(new InventoryListeners(), this);
        getServer().getPluginManager().registerEvents(new ShopListeners(), this);
        getServer().getPluginManager().registerEvents(lottery, this);
        getServer().getPluginManager().registerEvents(backpack, this);
        getServer().getPluginManager().registerEvents(table, this);

        getCommand("backpack").setExecutor(backpack);
        getCommand("table").setExecutor(table);
        getCommand("lottery").setExecutor(lottery);
        getCommand("reloadtravelfrog").setExecutor(this);
        getCommand("loadfromsql").setExecutor(new LoadFromSQLCommand());
        getCommand("clovers").setExecutor(new CloversCommand());
        getCommand("tickets").setExecutor(new TicketsCommand());
        getCommand("frogitems").setExecutor(new FrogItemsCommand());


    }

    // Reload command
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Save everyone's data
        for (Player p : Bukkit.getOnlinePlayers()) {
            PlayerDataManager.unregisterPlayer(p.getUniqueId());
        }

        reloadConfig();

        // Just in case
        saveDefaultConfig();

        FileConfiguration config = getConfig();
        dataSource.setServerName(config.getString("database.host"));
        dataSource.setPortNumber(config.getInt("database.port"));
        dataSource.setDatabaseName(config.getString("database.database"));
        dataSource.setUser(config.getString("database.user"));
        dataSource.setPassword(config.getString("database.password"));

        // Test the connection, if it fails do not load the plugin
        try {
            testDatabaseConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }

        // Pull the clover height from the config
        cloverYValue = config.getInt("clovers.y-level");

        // Import everyone's data
        for (Player p : Bukkit.getOnlinePlayers()) {
            PlayerDataManager.registerPlayer(p);
        }

        sender.sendMessage("Reload successful");
        return true;
    }

    @Override
    public void onDisable() {
        // Save everyone's data
        for (Player p : Bukkit.getOnlinePlayers()) {
            PlayerDataManager.savePlayerData(p.getUniqueId());
        }
    }

    public static TravelFrog getPlugin() {
        return plugin;
    }

    public static Connection getSQLConnection() throws SQLException {
        return dataSource.getConnection();
    }

    private void testDatabaseConnection() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            if (!conn.isValid(1)) {
                throw new SQLException("Could not establish database connection.");
            }
        }
    }

    public static int getCloverYValue() {
        return cloverYValue;
    }

}
