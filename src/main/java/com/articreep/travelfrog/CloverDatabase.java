package com.articreep.travelfrog;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.sql.*;
import java.time.Instant;

public class CloverDatabase {

    protected static int getClovers(Player p) throws SQLException {
        try (Connection connection = TravelFrog.getSQLConnection(); PreparedStatement stmt = connection.prepareStatement(
                "SELECT clovers FROM clovertable WHERE uuid = ?"
        )) {
            stmt.setString(1, p.getUniqueId().toString());
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                return result.getInt("clovers");
            } else {
                // If they didn't exist before, add them!
                addPlayer(p);
                return 0;
            }
        }
    }

    protected static int getCloversWaiting(Player p) throws SQLException {
        try (Connection connection = TravelFrog.getSQLConnection(); PreparedStatement stmt = connection.prepareStatement(
                "SELECT cloversWaiting FROM clovertable WHERE uuid = ?"
        )) {
            stmt.setString(1, p.getUniqueId().toString());
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                return result.getInt("cloversWaiting");
            } else {
                // If they didn't exist before, add them!
                addPlayer(p);
                return 25;
            }
        }
    }

    protected static Instant getLastSeen(Player p) throws SQLException {
        try (Connection connection = TravelFrog.getSQLConnection(); PreparedStatement stmt = connection.prepareStatement(
                "SELECT lastSeen FROM clovertable WHERE uuid = ?"
        )) {
            stmt.setString(1, p.getUniqueId().toString());
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                return result.getTimestamp("lastSeen").toInstant();
            } else {
                // If they didn't exist before, add them!
                addPlayer(p);
                return Instant.now();
            }
        }
    }

    private static void addPlayer(Player p) throws SQLException {
        // Adds a new UUID into the database
        try (Connection connection = TravelFrog.getSQLConnection(); PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO clovertable(uuid, clovers) VALUES(?, ?)"
        )) {
            stmt.setString(1, p.getUniqueId().toString());
            stmt.setInt(2, 0);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error while adding new user to database!");
        }

    }

    protected static void updateClovers(Player p) throws SQLException {
        Scoreboard board = p.getScoreboard();
        Objective objective = board.getObjective("Title");
        if (objective == null) return;
        Score score = objective.getScore(ChatColor.GREEN + "Clovers:");

        try (Connection connection = TravelFrog.getSQLConnection(); PreparedStatement stmt = connection.prepareStatement(
                "UPDATE clovertable SET clovers = ? WHERE uuid = ?"
        )) {
            stmt.setLong(1, score.getScore());
            stmt.setString(2, p.getUniqueId().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error while saving clovers to database");
        }
    }

    protected static void updateCloversWaiting(Player p) throws SQLException {
        CloverDisplayRunnable runnable = CloverListeners.runnableMap.get(p);
        try (Connection connection = TravelFrog.getSQLConnection(); PreparedStatement stmt = connection.prepareStatement(
                "UPDATE clovertable SET cloversWaiting = ? WHERE uuid = ?"
        )) {
            stmt.setLong(1, runnable.getCloverAmount());
            stmt.setString(2, p.getUniqueId().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error while saving clovers waiting to database");
        }
    }

    protected static void updateLastSeen(Player p) throws SQLException {

        try (Connection connection = TravelFrog.getSQLConnection(); PreparedStatement stmt = connection.prepareStatement(
                "UPDATE clovertable SET lastSeen = CURRENT_TIMESTAMP() WHERE uuid = ?"
        )) {
            stmt.setString(1, p.getUniqueId().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error while saving lastSeen to database");
        }
    }
}
