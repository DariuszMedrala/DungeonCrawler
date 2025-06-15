package org.example.dungeonCrawler.model.items.potions;

import org.example.dungeonCrawler.model.items.Item;

public abstract class Potion extends Item {
    protected int effectValue;

    public Potion(String name, String description, int effectValue) {
        super(name, description, ItemType.POTION);
        this.effectValue = effectValue;
    }

    public int getEffectValue() { return effectValue; }
}