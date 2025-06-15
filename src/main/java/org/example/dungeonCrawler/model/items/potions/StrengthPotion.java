package org.example.dungeonCrawler.model.items.potions;

import org.example.dungeonCrawler.model.Player;

public class StrengthPotion extends Potion {
    public StrengthPotion() {
        super("Mikstura Siły", "Tymczasowo zwiększa obrażenia o 10 na 5 tur", 10);
    }

    @Override
    public void use(Player player) {
        player.applyStrengthBonus(effectValue, 5);
    }
}