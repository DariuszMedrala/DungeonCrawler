package org.example.dungeonCrawler.model.items;
import org.example.dungeonCrawler.model.Player;

public abstract class Item {
    protected String name;
    protected String description;
    protected ItemType type;

    public enum ItemType {
        POTION, WEAPON, ARMOR, MISCELLANEOUS,
    }

    public Item(String name, String description, ItemType type) {
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public abstract void use(Player player);

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public ItemType getType() { return type; }
}