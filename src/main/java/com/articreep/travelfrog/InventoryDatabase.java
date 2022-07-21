package com.articreep.travelfrog;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class InventoryDatabase {

    protected static int getFourLeafClovers(UUID uuid) throws SQLException {
        try (Connection connection = TravelFrog.getSQLConnection(); PreparedStatement stmt = connection.prepareStatement(
                "SELECT fourLeafClover FROM inventorytable WHERE uuid = ?"
        )) {
            stmt.setString(1, uuid.toString());
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                return result.getInt("fourLeafClover");
            } else {
                // If they didn't exist before, add them!
                addPlayer(uuid);
                return 0;
            }
        }
    }

    protected static int getLanterns(UUID uuid) throws SQLException {
        try (Connection connection = TravelFrog.getSQLConnection(); PreparedStatement stmt = connection.prepareStatement(
                "SELECT lantern FROM inventorytable WHERE uuid = ?"
        )) {
            stmt.setString(1, uuid.toString());
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                return result.getInt("lantern");
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
                "INSERT INTO inventorytable(uuid) VALUES(?)"
        )) {
            stmt.setString(1, uuid.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error while adding new user to database!");
        }

    }

    protected static void updateFourLeafClovers(PlayerInventory inventory) throws SQLException {

        try (Connection connection = TravelFrog.getSQLConnection(); PreparedStatement stmt = connection.prepareStatement(
                "UPDATE inventorytable SET fourLeafClover = ? WHERE uuid = ?"
        )) {
            stmt.setLong(1, inventory.getFourLeafClovers());
            stmt.setString(2, inventory.getUuid().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error while saving four-leaf clovers to database");
        }
    }

    protected static void updateLanterns(PlayerInventory inventory) throws SQLException {

        try (Connection connection = TravelFrog.getSQLConnection(); PreparedStatement stmt = connection.prepareStatement(
                "UPDATE inventorytable SET lantern = ? WHERE uuid = ?"
        )) {
            stmt.setLong(1, inventory.getLanterns());
            stmt.setString(2, inventory.getUuid().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error while saving lanterns to database");
        }
    }


}