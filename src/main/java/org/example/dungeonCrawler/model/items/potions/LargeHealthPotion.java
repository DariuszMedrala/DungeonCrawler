package org.example.dungeonCrawler.model.items.potions;

import org.example.dungeonCrawler.model.Player;

public class LargeHealthPotion extends Potion {
    public LargeHealthPotion() {
        super("Duża Mikstura Zdrowia", "Przywraca 100 punktów życia", 100);
    }

    @Override
    public void use(Player player) {
        player.heal(effectValue);
    }
}