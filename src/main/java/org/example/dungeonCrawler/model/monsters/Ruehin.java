package org.example.dungeonCrawler.model.monsters;
import org.example.dungeonCrawler.model.Enemy;


public class Ruehin extends Enemy {
    private static final double DODGE_CHANCE = 0.2;
    private static final String imagePath = "/images/ruehin.png";

    private static final String encounterText = "No pięknie. Skrzyżowanie ducha z talerzem owoców morza. I co on tak tuli tego biedaka?\n" +
            "To coś próbuje cię wystraszyć! Pokaż mu, że jedyne, czego się boisz, to pustej sakiewki! Połam mu te szczypce!\n" +
            "Na co czekasz?! Aż zaoferuje ci darmowy uścisk i zrobi z kręgosłupa harmonijkę?!\n\n";

    private static final String deathText = "I po koszmarze. Okazało się, że pod tym całym strasznym płaszczem nie było nic ciekawego.\n" +
            "Leży jak kupa szmat i połamanych widelców. Tyle było z wielkiego postrachu lochów.\n" +
            "Dobra, koniec filozofowania. Sprawdźmy, co z tego da się sprzedać. Może te odnóża nadają się na zupę krabową?\n" +
            "Lepiej zbieraj co cenniejsze i spadajmy stąd, zanim jego mamusia przyjdzie sprawdzić, czemu jej synek nie wraca na kolację.\n\n" +
            "Zdobywasz %d pkt doświadczenia.\n\n";

    public Ruehin() {
        super("Ruehin", 18, 6, 25, imagePath, encounterText, deathText);
    }

    @Override
    public int takeDamage(int damage) {
        if (Math.random() < DODGE_CHANCE) {
            return 0;
        }
        return super.takeDamage(damage);
    }

    @Override
    public int attack() {
        int baseDamage = super.attack();
        if (Math.random() < 0.15) {
            return baseDamage + (super.getDamage() / 2);
        }
        return baseDamage;
    }
}