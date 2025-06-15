package org.example.dungeonCrawler.model.items.potions;

import org.example.dungeonCrawler.model.Player;

public class SmallHealthPotion extends Potion {
    public SmallHealthPotion() {
        super("Mała Mikstura Zdrowia", "Przywraca 25 punktów życia", 25);
    }

    @Override
    public void use(Player player) {
        player.heal(effectValue);
    }
}