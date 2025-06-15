package org.example.dungeonCrawler.model.items.armors;

import org.example.dungeonCrawler.model.Player;
import org.example.dungeonCrawler.model.items.Item;

public abstract class Armor extends Item {
    protected int armorValue;
    protected ArmorType armorType;

    public enum ArmorType {
        LIGHT, MEDIUM, HEAVY, MAGICAL
    }

    public Armor(String name, String description, int armorValue, ArmorType armorType) {
        super(name, description, ItemType.ARMOR);
        this.armorValue = armorValue;
        this.armorType = armorType;
    }

    @Override
    public void use(Player player) {
        player.equipArmor(this);
    }

    public int getArmorValue() { return armorValue; }
    public ArmorType getArmorType() { return armorType; }
}