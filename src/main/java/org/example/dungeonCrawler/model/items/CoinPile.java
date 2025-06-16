package org.example.dungeonCrawler.model.items;

import org.example.dungeonCrawler.model.Player;

public class CoinPile extends Item {

    public CoinPile() {
        super("Sakiewka z monetami", "Sakiewka pełna monet, które można wykorzystać do zakupu przedmiotów w grze.", ItemType.MISCELLANEOUS);
    }

   @Override
    public void use(Player player) {
    }
}
