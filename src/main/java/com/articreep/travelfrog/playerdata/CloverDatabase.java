package com.articreep.travelfrog.playerdata;

import com.articreep.travelfrog.CloverDisplayRunnable;
import com.articreep.travelfrog.CloverListeners;
import com.articreep.travelfrog.TravelFrog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

public class CloverDatabase {

    protected static int getClovers(UUID uuid) throws SQLException {
        try (Connection connection = TravelFrog.getSQLConnection(); PreparedStatement stmt = connection.prepareStatement(
                "SELECT clovers FROM clovertable WHERE uuid = ?"
        )) {
            stmt.setString(1, uuid.toString());
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                return result.getInt("clovers");
            } else {
                // If they didn't exist before, add them!
                addPlayer(uuid);
                return 0;
            }
        }
    }

    protected static int getCloversWaiting(UUID uuid) throws SQLException {
        try (Connection connection = TravelFrog.getSQLConnection(); PreparedStatement stmt = connection.prepareStatement(
                "SELECT cloversWaiting FROM clovertable WHERE uuid = ?"
        )) {
            stmt.setString(1, uuid.toString());
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                return result.getInt("cloversWaiting");
            } else {
                // If they didn't exist before, add them!
                addPlayer(uuid);
                return 25;
            }
        }
    }

    protected static Instant getLastSeen(UUID uuid) throws SQLException {
        try (Connection connection = TravelFrog.getSQLConnection(); PreparedStatement stmt = connection.prepareStatement(
                "SELECT lastSeen FROM clovertable WHERE uuid = ?"
        )) {
            stmt.setString(1, uuid.toString());
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                return result.getTimestamp("lastSeen").toInstant();
            } else {
                // If they didn't exist before, add them!
                addPlayer(uuid);
                return Instant.now();
            }
        }
    }

    protected static int getFourLeafCloversWaiting(UUID uuid) throws SQLException {
        try (Connection connection = TravelFrog.getSQLConnection(); PreparedStatement stmt = connection.prepareStatement(
                "SELECT fourLeafCloversWaiting FROM clovertable WHERE uuid = ?"
        )) {
            stmt.setString(1, uuid.toString());
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                return result.getInt("fourLeafCloversWaiting");
            } else {
                // If they didn't exist before, add them!
                addPlayer(uuid);
                return 0;
            }
        }
    }

    protected static int getTickets(UUID uuid) throws SQLException {
        try (Connection connection = TravelFrog.getSQLConnection(); PreparedStatement stmt = connection.prepareStatement(
                "SELECT tickets FROM clovertable WHERE uuid = ?"
        )) {
            stmt.setString(1, uuid.toString());
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                return result.getInt("tickets");
            } else {
                // If they didn't exist before, add them!
                addPlayer(uuid);
                return 0;
            }
        }
    }

    private static void addPlayer(UUID uuid) throws SQLException {
        // Adds a new UUID into the database
        try (Connection connection = TravelFrog.getSQLConnection(); PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO clovertable(uuid, clovers) VALUES(?, ?)"
        )) {
            stmt.setString(1, uuid.toString());
            stmt.setInt(2, 0);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error while adding new user to database!");
        }

    }

    protected static void updateClovers(PlayerData data) throws SQLException {


        try (Connection connection = TravelFrog.getSQLConnection(); PreparedStatement stmt = connection.prepareStatement(
                "UPDATE clovertable SET clovers = ? WHERE uuid = ?"
        )) {
            stmt.setLong(1, data.getClovers());
            stmt.setString(2, data.getUuid().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error while saving clovers to database");
        }
    }

    protected static void updateCloversWaiting(PlayerData data) throws SQLException {
        CloverDisplayRunnable runnable = data.getRunnable();
        // The runnable is only null if the plugin was reloaded and a player hasn't relogged
        if (runnable == null) return;
        try (Connection connection = TravelFrog.getSQLConnection(); PreparedStatement stmt = connection.prepareStatement(
                "UPDATE clovertable SET cloversWaiting = ? WHERE uuid = ?"
        )) {
            stmt.setLong(1, runnable.getCloverAmount());
            stmt.setString(2, data.getUuid().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error while saving clovers waiting to database");
        }
        // Also update the four leaf clovers!
        updateFourLeafCloversWaiting(data);
    }

    protected static void updateFourLeafCloversWaiting(PlayerData data) throws SQLException {
        CloverDisplayRunnable runnable = data.getRunnable();
        try (Connection connection = TravelFrog.getSQLConnection(); PreparedStatement stmt = connection.prepareStatement(
                "UPDATE clovertable SET fourLeafCloversWaiting = ? WHERE uuid = ?"
        )) {
            stmt.setLong(1, runnable.getFourLeafCloverAmount());
            stmt.setString(2, data.getUuid().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error while saving four-leaf clovers waiting to database");
        }
    }

    protected static void updateLastSeen(UUID uuid) throws SQLException {
        try (Connection connection = TravelFrog.getSQLConnection(); PreparedStatement stmt = connection.prepareStatement(
                "UPDATE clovertable SET lastSeen = CURRENT_TIMESTAMP() WHERE uuid = ?"
        )) {
            stmt.setString(1, uuid.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error while saving lastSeen to database");
        }
    }

    protected static void updateTickets(PlayerData data) throws SQLException {
        try (Connection connection = TravelFrog.getSQLConnection(); PreparedStatement stmt = connection.prepareStatement(
                "UPDATE clovertable SET tickets = ? WHERE uuid = ?"
        )) {
            stmt.setLong(1, data.getTickets());
            stmt.setString(2, data.getUuid().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error while saving tickets to database");
        }
    }
}
