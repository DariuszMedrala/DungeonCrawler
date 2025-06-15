package org.example.dungeonCrawler.model.items.keys;

import org.example.dungeonCrawler.model.Player;
import org.example.dungeonCrawler.model.items.Item;

public class Key extends Item {

    public Key() {
        super("Stary zerdziewialy klucz", "Kto wie, może się do czegoś przyda", ItemType.MISCELLANEOUS);
    }

    @Override
    public void use(Player player) {
    }
}