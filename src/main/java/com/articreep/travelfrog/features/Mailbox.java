package com.articreep.travelfrog.features;

import net.kyori.adventure.text.Component;

public class Mailbox {

    public enum MailReason {
        VISITING, THANKS, SYSTEM, UNKNOWN;

        /**
         * Safe version of valueOf(). If a match is not found, it will return UNKNOWN
         * @param string String to convert to a MailReason
         * @return The MailReason or UNKNOWN
         */
        public static MailReason toEnum(String string) {
            try {
                return valueOf(string);
            } catch (IllegalArgumentException e) {
                return MailReason.UNKNOWN;
            }
        }
    }

    public enum MailSender {
        SYSTEM(Component.text("System")),
        UNKNOWN(Component.text("Unknown"));

        private final Component name;
        MailSender(Component name) {
            this.name = name;
        }

        public Component getName() {
            return name;
        }

        /**
         * Safe version of valueOf(). If a match is not found, it will return UNKNOWN
         * @param string String to convert to a MailReason
         * @return The MailSender or UNKNOWN
         */
        public static MailSender toEnum(String string) {
            try {
                return valueOf(string);
            } catch (IllegalArgumentException e) {
                return MailSender.UNKNOWN;
            }
        }
    }
}
