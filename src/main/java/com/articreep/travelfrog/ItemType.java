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
    FOUR_LEAF_CLOVER(Material.MANGROVE_PROPAGULE, -1, Component.text("Four-leaf Clover").color(NamedTextColor.GREEN),
            Component.text("It's some kind of good-luck charm.").color(NamedTextColor.GREEN)),
    LANTERN(Material.LANTERN, 600, Component.text("Lantern").color(NamedTextColor.YELLOW),
            Component.text("An excellent and very portable light source.").color(NamedTextColor.YELLOW)),
    BREAD(Material.BREAD, 10, Component.text("Bread").color(NamedTextColor.GOLD),
            Component.text("Three wheat.").color(NamedTextColor.GRAY));

    private final int price;
    private final Material material;
    private final Component name;
    private final Component[] lore;

    ItemType(Material material, int price, Component name, TextComponent... text) {
        this.material = material;
        this.price = price;
        this.name = name;
        this.lore = text;
    }

    public Material getMaterial() {
        return material;
    }

    public Component getName() {
        return name;
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
}
