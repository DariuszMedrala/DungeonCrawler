package org.example.dungeonCrawler.model.items.potions;

import org.example.dungeonCrawler.model.Player;

public class AntidotePotion extends Potion {
    public AntidotePotion() {
        super("Odtrutka", "Usuwa zatrucie i przywraca 15 HP", 15);
    }

    @Override
    public void use(Player player) {
        player.curePoison();
        player.heal(effectValue);
    }
}