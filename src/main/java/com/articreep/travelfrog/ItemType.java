package com.articreep.travelfrog;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum ItemType {
    FOUR_LEAF_CLOVER(Material.MANGROVE_PROPAGULE, Component.text("Four-leaf Clover").color(NamedTextColor.GREEN),
            Component.text("It's some kind of good-luck charm.").color(NamedTextColor.GREEN)),
    LANTERN(Material.LANTERN, Component.text("Lantern").color(NamedTextColor.YELLOW));

    private final Material material;
    private final Component name;
    private final Component[] lore;

    ItemType(Material material, Component name, TextComponent... text) {
        this.material = material;
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
        list.add(Component.text("You have " + quantity + ".").color(NamedTextColor.GRAY));
        return list;
    }
}
