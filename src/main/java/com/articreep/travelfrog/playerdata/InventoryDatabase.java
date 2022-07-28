package com.articreep.travelfrog.playerdata;

import com.articreep.travelfrog.ItemType;
import com.articreep.travelfrog.TravelFrog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class InventoryDatabase {

    protected static HashMap<ItemType, Integer> getInventory(UUID uuid) throws SQLException {
        try (Connection connection = TravelFrog.getSQLConnection(); PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM inventorytable WHERE uuid = ?"
        )) {
            stmt.setString(1, uuid.toString());
            HashMap<ItemType, Integer> resultMap = new HashMap<>();
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                for (ItemType type : ItemType.valuesList()) {
                    resultMap.put(type, result.getInt(type.name()));
                }
                return resultMap;
            } else {
                // If they didn't exist before, add them!
                addPlayer(uuid);
                return getInventory(uuid);
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


    protected static void updateInventory(PlayerData data) throws SQLException {
        for (ItemType type : ItemType.valuesList()) {
            try (Connection connection = TravelFrog.getSQLConnection(); PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE inventorytable SET " + type.name() + " = ? WHERE uuid = ?"
            )) {
                stmt.setInt(1, data.getItemCount(type));
                stmt.setString(2, data.getUuid().toString());
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new SQLException("Error while saving inventory to database");
            }
        }
    }


}
