package com.articreep.travelfrog.playerdata;

import com.articreep.travelfrog.ItemType;
import com.articreep.travelfrog.TravelFrog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TableDatabase {
    private static void addPlayer(UUID uuid) throws SQLException {
        // Adds a new UUID into the database
        try (Connection connection = TravelFrog.getSQLConnection(); PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO tabletable(uuid) VALUES(?)"
        )) {
            stmt.setString(1, uuid.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error while adding new user to database!");
        }
    }

    protected static List<ItemType> getContents(UUID uuid) throws SQLException {
        try (Connection connection = TravelFrog.getSQLConnection(); PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM tabletable WHERE uuid = ?"
        )) {
            stmt.setString(1, uuid.toString());
            ResultSet result = stmt.executeQuery();
            ArrayList<ItemType> list = new ArrayList<>();
            if (result.next()) {
                list.add(ItemType.valueOf(result.getString("foodSlot1")));
                list.add(ItemType.valueOf(result.getString("foodSlot2")));
                list.add(ItemType.valueOf(result.getString("charmSlot1")));
                list.add(ItemType.valueOf(result.getString("charmSlot2")));
                list.add(ItemType.valueOf(result.getString("toolSlot1")));
                list.add(ItemType.valueOf(result.getString("toolSlot2")));
                list.add(ItemType.valueOf(result.getString("toolSlot3")));
                list.add(ItemType.valueOf(result.getString("toolSlot4")));
                return list;
            } else {
                // If they didn't exist before, add them!
                addPlayer(uuid);
                return getContents(uuid);
            }
        }
    }

    protected static void updateTable(PlayerData data) throws SQLException {

        try (Connection connection = TravelFrog.getSQLConnection(); PreparedStatement stmt = connection.prepareStatement(
                "UPDATE tabletable SET foodSlot1 = ?, foodSlot2 = ?, charmSlot1 = ?, charmSlot2 = ?," +
                        " toolSlot1 = ?, toolSlot2 = ?, toolSlot3 = ?, toolSlot4 = ? WHERE uuid = ?"
        )) {
            List<ItemType> list = data.getTable();
            for (int i = 0; i < 8; i++) {
                stmt.setString(i+1, list.get(i).name());
            }
            stmt.setString(9, data.getUuid().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error while saving table to database");
        }
    }
}
