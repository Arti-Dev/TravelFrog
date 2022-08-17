package com.articreep.travelfrog.playerdata;

import com.articreep.travelfrog.ItemType;
import com.articreep.travelfrog.MailboxItem;
import com.articreep.travelfrog.TravelFrog;
import com.articreep.travelfrog.features.Mailbox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MailboxDatabase {
    protected static void addMailboxEntry(UUID uuid, Mailbox.MailReason reason, Mailbox.MailSender sender, ItemType type, int amount) throws SQLException {
        try (Connection connection = TravelFrog.getSQLConnection(); PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO mailbox(uuid, reason, sender, itemtype, itemamount) VALUES(?, ?, ?, ?, ?)"
        )) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, reason.name());
            stmt.setString(3, sender.name());
            stmt.setString(4, type.name());
            stmt.setInt(5, amount);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error while adding new mailbox entry to database!");
        }
    }

    protected static Map<Integer, MailboxItem> getMailboxEntries(UUID uuid) throws SQLException {
        try (Connection connection = TravelFrog.getSQLConnection(); PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM mailbox WHERE uuid = ?"
        )) {
            stmt.setString(1, uuid.toString());
            ResultSet result = stmt.executeQuery();
            HashMap<Integer, MailboxItem> map = new HashMap<>();
            while (result.next()) {
                Mailbox.MailReason reason = Mailbox.MailReason.toEnum(result.getString("reason"));
                Mailbox.MailSender sender = Mailbox.MailSender.toEnum(result.getString("sender"));
                ItemType type = ItemType.toEnum(result.getString("itemtype"));
                int amount = result.getInt("itemamount");
                int id = result.getInt("id");
                map.put(id, new MailboxItem(sender, reason, type, amount));
            }
            return map;
        }
    }

    protected static void removeMailboxEntry(int id) throws SQLException {
        try (Connection connection = TravelFrog.getSQLConnection(); PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM mailbox WHERE id = ?"
        )) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error while removing mailbox entry in database");
        }
    }
}
