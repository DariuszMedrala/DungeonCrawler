package org.example.dungeonCrawler.model.monsters;

import org.example.dungeonCrawler.model.Enemy;

public class Fiend extends Enemy {
    private static final double BLOCK_CHANCE = 0.25;
    private static final String imagePath = "/images/fiend.png";

    private static final String encounterText = "O w mordę... to się nazywa przerośnięty jeleń. Bambi po dekadzie na siłowni.\n" +
            "Uważaj na jego trzecie oko, wiedźminie! To hipnotyzer! \nJak nagle zaczniesz śpiewać serenady do drzew, to znaczy, że już po tobie.\n" +
            "Trzeba utemperować tego byka, zanim uzna, że cała puszcza to jego prywatny folwark!\n Pokaż mu, kto tu jest prawdziwym drapieżnikiem!\n" +
            "Na co się gapisz?! Aż cię nadzieje na to swoje poroże i zatańczy kankana?! Do roboty!\n\n";

    private static final String deathText = "No i po wielkim królu lasu. Leży i nawet nie drgnie. Cała ta hipnoza psu na budę.\n" +
            "I co mu po tych mięśniach i groźnych minach? Wystarczyło parę cięć w odpowiednim miejscu i leży jak długi.\n" +
            "Dobra, koniec podziwiania. Bierz się za patroszenie! To jego trzecie oko jest warte fortunę u alchemików! Serce i skóra też pójdą za dobrą cenę.\n" +
            "Spiesz się, zanim jego mniejsi koledzy zlecą się na pogrzeb i zrobi się niezręcznie!\n\n" +
            "Zdobywasz %d pkt doświadczenia.\n\n";

    public Fiend() {
        super("Fiend", 35, 12, 40, imagePath, encounterText, deathText);
    }

    @Override
    public void takeDamage(int damage) {
        if (Math.random() < BLOCK_CHANCE) {
            int blockedDamage = damage / 2;
            super.takeDamage(blockedDamage);
        } else {
            super.takeDamage(damage);
        }
    }
}