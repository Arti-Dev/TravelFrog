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
    // TODO Add item limits.
    FOUR_LEAF_CLOVER(Material.MANGROVE_PROPAGULE, -1, false, ItemCategory.CHARM, Component.text("Four-leaf Clover", NamedTextColor.GREEN)
            .decoration(TextDecoration.ITALIC, false),
            Component.text("It's some kind of good-luck charm.", NamedTextColor.GREEN)),
    LANTERN(Material.LANTERN, 600, true, ItemCategory.TOOL, Component.text("Lantern", NamedTextColor.YELLOW).
            decoration(TextDecoration.ITALIC, false),
            Component.text("An excellent and very portable light source.", NamedTextColor.YELLOW)),
    BREAD(Material.BREAD, 10, false, ItemCategory.FOOD, Component.text("Bread", NamedTextColor.GOLD),
            Component.text("Three wheat.").color(NamedTextColor.GRAY)),
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
