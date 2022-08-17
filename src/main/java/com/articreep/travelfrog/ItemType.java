package com.articreep.travelfrog;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum ItemType {
    FOUR_LEAF_CLOVER(Material.MANGROVE_PROPAGULE, -1, false, ItemCategory.CHARM, Component.text("Four-leaf Clover", NamedTextColor.GREEN)
            .decoration(TextDecoration.ITALIC, false),
            Component.text("It's some kind of good-luck charm.", NamedTextColor.GREEN)),
    LANTERN(Material.LANTERN, 600, true, ItemCategory.TOOL, Component.text("Lantern", NamedTextColor.YELLOW).
            decoration(TextDecoration.ITALIC, false),
            Component.text("An excellent and very portable light source.", NamedTextColor.GRAY)),
    BREAD(Material.BREAD, 10, false, ItemCategory.FOOD, Component.text("Bread", NamedTextColor.GOLD),
            Component.text("Three wheat.").color(NamedTextColor.GRAY)),
    SMALL_FISH(Material.COD, 20, false, ItemCategory.FOOD,
            Component.text("Small Fish", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false),
            Component.text("For small froggies.", NamedTextColor.GRAY)),
    KABOCHA_PIE(Material.PUMPKIN_PIE, 50, false, ItemCategory.FOOD,
            Component.text("Kabocha Pie", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false),
            Component.text("A Japanese gourd.. but pie.", NamedTextColor.GRAY),
            Component.text("I'm not doing a good job at porting the original food, aren't I.", NamedTextColor.DARK_GRAY)),
    RABBIT_STEW(Material.RABBIT_STEW, 80, false, ItemCategory.FOOD,
            Component.text("Rabbit Stew", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false),
            Component.text("ok can the frog REALLY eat that much", NamedTextColor.DARK_GRAY)),
    INANIMATE_SLIME(Material.SLIME_BLOCK, 100, false, ItemCategory.FOOD,
            Component.text("Inanimate Slime", NamedTextColor.DARK_GREEN).decoration(TextDecoration.ITALIC, false),
            Component.text("Not quite the real thing..", NamedTextColor.GRAY)),
    BOTTLE_OF_FIREFLIES(Material.GLASS_BOTTLE, 100, false, ItemCategory.FOOD,
            Component.text("Bottle of Fireflies", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false),
            Component.text("..except they were never added to the game", NamedTextColor.GRAY),
            Component.text("I KNOW FROGS DON'T EAT FIREFLIES IRL", NamedTextColor.DARK_GRAY)),

    // Sends the frog in a particular direction, sends back exclusive photos, and if those have been exhausted sends back unique photos
    TICKET_WHITE(Material.NAME_TAG, -1, false, ItemCategory.CHARM, Component.text("Biome Ticket (white)", NamedTextColor.WHITE)
            .decoration(TextDecoration.ITALIC, false),
            Component.text("Sends your frog to a cold or snowy biome.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)),
    TICKET_ORANGE(Material.NAME_TAG, -1, false, ItemCategory.CHARM, Component.text("Biome Ticket (orange)", NamedTextColor.GOLD)
            .decoration(TextDecoration.ITALIC, false),
            Component.text("Sends your frog to a warm biome.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)),
    TICKET_BLUE(Material.NAME_TAG, -1, false, ItemCategory.CHARM, Component.text("Biome Ticket (blue)", NamedTextColor.BLUE)
            .decoration(TextDecoration.ITALIC, false),
            Component.text("Sends your frog to a aquatic or cave biome.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)),
    TICKET_GREEN(Material.NAME_TAG, -1, false, ItemCategory.CHARM, Component.text("Biome Ticket (green)", NamedTextColor.GREEN)
            .decoration(TextDecoration.ITALIC, false),
            Component.text("Sends your frog to a temperate biome.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)),

    // Makes it easier to travel in a certain direction. Well, it works all the time.
    // Will always send back a unique photo.
    STAR_WHITE(Material.NETHER_STAR, -1, false, ItemCategory.CHARM,
            Component.text("Miniature Star (white)", NamedTextColor.WHITE)
                    .decoration(TextDecoration.ITALIC, false),
            Component.text("Makes it easier to travel to", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
            Component.text("unique areas in cold or snowy biomes.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)),
    STAR_ORANGE(Material.NETHER_STAR, -1, false, ItemCategory.CHARM,
            Component.text("Miniature Star (orange)", NamedTextColor.GOLD)
                    .decoration(TextDecoration.ITALIC, false),
            Component.text("Makes it easier to travel to", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
            Component.text("unique areas in warm biomes.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)),
    STAR_BLUE(Material.NETHER_STAR, -1, false, ItemCategory.CHARM,
                Component.text("Miniature Star (blue)", NamedTextColor.BLUE)
                    .decoration(TextDecoration.ITALIC, false),
            Component.text("Makes it easier to travel to", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
            Component.text("unique areas in aquatic or cave biomes.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)),
    STAR_GREEN(Material.NETHER_STAR, -1, false, ItemCategory.CHARM,
            Component.text("Miniature Star (green)", NamedTextColor.GREEN)
                    .decoration(TextDecoration.ITALIC, false),
            Component.text("Makes it easier to travel to", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
            Component.text("unique areas in temperate biomes.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)),

    // The frog will be out for longer, but will bring back unique souvenir consumables depending on the color.
    KONPEITO_RED(Material.SUGAR, -1, false, ItemCategory.FOOD,
            Component.text("Konpeito (red)", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false),
            Component.text("Strawberry flavored.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
            Component.text("It's straight up just sugar..", NamedTextColor.DARK_GRAY)),
    KONPEITO_YELLOW(Material.SUGAR, -1, false, ItemCategory.FOOD,
            Component.text("Konpeito (yellow)", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false),
            Component.text("Tastes like lemon.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
            Component.text("It's straight up just sugar..", NamedTextColor.DARK_GRAY)),
    KONPEITO_PURPLE(Material.SUGAR, -1, false, ItemCategory.FOOD,
            Component.text("Konpeito (purple)", NamedTextColor.DARK_PURPLE).decoration(TextDecoration.ITALIC, false),
            Component.text("Mmm, grapes.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
            Component.text("It's straight up just sugar..", NamedTextColor.DARK_GRAY)),
    KONPEITO_GREEN(Material.SUGAR, -1, false, ItemCategory.FOOD,
            Component.text("Konpeito (purple)", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false),
            Component.text("Melons!", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
            Component.text("It's straight up just sugar..", NamedTextColor.DARK_GRAY)),

    // The frog will bring back a unique souvenir treasure depending on the color.
    CRACKERS_RED(Material.COOKIE, -1, false, ItemCategory.FOOD,
            Component.text("Crackers (red)", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false),
            Component.text("Just some crackers!", NamedTextColor.GRAY)),
    CRACKERS_YELLOW(Material.COOKIE, -1, false, ItemCategory.FOOD,
            Component.text("Crackers (yellow)", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false),
            Component.text("Just some crackers!", NamedTextColor.GRAY)),
    CRACKERS_PURPLE(Material.COOKIE, -1, false, ItemCategory.FOOD,
            Component.text("Crackers (purple)", NamedTextColor.DARK_PURPLE).decoration(TextDecoration.ITALIC, false),
            Component.text("Just some crackers!", NamedTextColor.GRAY)),
    CRACKERS_GREEN(Material.COOKIE, -1, false, ItemCategory.FOOD,
            Component.text("Crackers (green)", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false),
            Component.text("Just some crackers!", NamedTextColor.GRAY)),
    // Special cases
    CLOVER(Material.SMALL_DRIPLEAF, -1, true, null, null),
    TICKET(Material.NAME_TAG, -1, true, null, null),
    NONE(null, -1, true, null, null);

    private final int price;
    private final boolean singleItem;
    private final ItemCategory category;
    private final Material material;
    private final Component name;
    private final Component[] lore;

    ItemType(Material material, int price, boolean singleItem, ItemCategory category, Component name, TextComponent... text) {
        this.material = material;
        this.price = price;
        this.singleItem = singleItem;
        this.category = category;
        this.name = name;
        this.lore = text;
    }

    public Material getMaterial() {
        return material;
    }

    public Component getName() {
        return name;
    }

    public List<Component> getRawLore() {
        return new ArrayList<>(Arrays.asList(lore));
    }

    public List<Component> createLore(int quantity) {
        List<Component> list = new ArrayList<>(Arrays.asList(lore));
        if (quantity < 0) return list;
        list.add(Component.text("You have " + quantity + ".").color(NamedTextColor.GRAY));
        return list;
    }

    public int getPrice() {
        return price;
    }

    public boolean isSingleItem() {
        return singleItem;
    }

    public ItemCategory getCategory() {
        return category;
    }

    /**
     * Returns a list of all enums EXCEPT the NONE enum.
     * @return list of all enums except NONE
     */
    public static List<ItemType> valuesList() {
        List<ItemType> list = new ArrayList<>(Arrays.asList(values()));
        list.remove(ItemType.NONE);
        list.remove(ItemType.CLOVER);
        list.remove(ItemType.TICKET);
        return list;
    }

    /**
     * Safe version of valueOf(). If a match is not found, it will return ItemType.NONE.
     * @return the corresponding enum or NONE
     */
    public static ItemType toEnum(String string) {
        try {
            return valueOf(string);
        } catch (IllegalArgumentException e) {
            return ItemType.NONE;
        }
    }



}
