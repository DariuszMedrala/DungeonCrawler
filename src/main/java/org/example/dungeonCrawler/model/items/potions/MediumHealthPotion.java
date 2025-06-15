package org.example.dungeonCrawler.model.items.potions;

import org.example.dungeonCrawler.model.Player;

public class MediumHealthPotion extends Potion {
    public MediumHealthPotion() {
        super("Średnia Mikstura Zdrowia", "Przywraca 50 punktów życia", 50);
    }

    @Override
    public void use(Player player) {
        player.heal(effectValue);
    }
}