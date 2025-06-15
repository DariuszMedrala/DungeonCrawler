package org.example.dungeonCrawler.model.monsters;

import org.example.dungeonCrawler.model.Enemy;
import org.example.dungeonCrawler.model.Player;

public class Vypper extends Enemy {
    private static final int POISON_DAMAGE = 3;
    private static final int POISON_DURATION = 10;
    private static final String imagePath = "/images/vypper.png";

    private static final String encounterText = "Jezus Maria, co za glizda! I... czujesz ten smród? Fuj, to JAD!\n" +
    "To jego ostatnie chwile! Widzisz ten wijący się makaron? Zrób mu z pyska jesień średniowiecza!\n" +
    "No rusz się, zanim opluje ci ten twój zadowolony z siebie ryj!\n\n";

    private static final String deathText = "No i leży! Wielki mi potwór... wygląda jak pęknięty wąż ogrodowy.\n" +
    "I po co było to całe syczenie i plucie? Mówiłem, że jego jad to popłuczyny.\n"  +
            "Dobra, nie stój tak i nie podziwiaj. Bierz się za oprawianie! Z takiej skóry będą prima sort buty, a gruczoły jadowe opchniemy jakiemuś alchemikowi za grubą kasę.\n" +
    "Samo się nie oskóruje, wiedźminie!\n\n" +
            "Zdobywasz %d pkt doświadczenia.\n\n";

    public Vypper() {
        super("Vypper", 30, 10, 30, imagePath, encounterText, deathText);
    }

    @Override
    public int attack() {
        return super.attack();
    }

    public void poisonAttack(Player player) {

        int damage = attack();
        player.takeDamage(damage);

        player.applyPoison(POISON_DAMAGE, POISON_DURATION);
    }
}