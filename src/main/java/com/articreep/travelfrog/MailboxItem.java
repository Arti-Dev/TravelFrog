package com.articreep.travelfrog;

import com.articreep.travelfrog.features.Mailbox;
import org.bukkit.inventory.ItemStack;

public class MailboxItem {
    // TODO Try records!
    private final Mailbox.MailSender sender;
    private final Mailbox.MailReason reason;
    private final ItemType type;
    private final int amount;

    public MailboxItem(Mailbox.MailSender sender, Mailbox.MailReason reason, ItemType type, int amount) {
        this.sender = sender;
        this.reason = reason;
        this.type = type;
        this.amount = amount;
    }

    public Mailbox.MailSender getSender() {
        return sender;
    }

    public int getAmount() {
        return amount;
    }

    public ItemType getType() {
        return type;
    }

    public Mailbox.MailReason getReason() {
        return reason;
    }

    public ItemStack createMenuItem() {
        // TODO
        return null;
    }
}
