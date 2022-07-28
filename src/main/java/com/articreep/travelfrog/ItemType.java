package com.articreep.travelfrog;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
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
            Component.text("An excellent and very portable light source.", NamedTextColor.YELLOW)),
    BREAD(Material.BREAD, 10, false, ItemCategory.FOOD, Component.text("Bread", NamedTextColor.GOLD),
            Component.text("Three wheat.").color(NamedTextColor.GRAY)),
    BOTTLE_O_ENCHANTING(Material.EXPERIENCE_BOTTLE, -1, false, ItemCategory.CHARM,
            Component.text("Bottle o' Enchanting", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false),
            Component.text("Might be split into variations soon!"),
            Component.text("Makes it easier to travel to certain biomes.", NamedTextColor.WHITE)),
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
        return list;
    }



}
