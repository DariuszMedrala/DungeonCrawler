package org.example.dungeonCrawler.model.monsters;
import org.example.dungeonCrawler.model.Enemy;

public class Ogre extends Enemy {
    private boolean isEnraged = false;
    private static final double RAGE_THRESHOLD = 0.3;
    private static final String imagePath = "/images/ogre.png";

    private static final String encounterText = "No proszę, MasterChef w edycji potwornej! Co on tam pichci, zupę z kamieni i zgniłych liści?\n" +
            "Widzisz ten jego gar? Zrób mu z niego hełm, zanim postanowi dodać cię do zupy jako wkładkę mięsną!\n" +
            "Na co czekasz, koneserze?! Aż cię poczęstuje tą swoją breją i umrzesz na niestrawność?!\n\n";

    private static final String deathText = "No i po rewolucjach kuchennych. Szef kuchni poległ na stanowisku pracy.\n" +
            "Przynajmniej świat jest bezpieczniejszy o jedną obrzydliwą zupę. To się nazywa bohaterstwo!\n" +
            "Dobra, przestań się mazać. Przeszukaj jego sakwy i ten bajzel wokół gara. Może miał jakieś unikalne przyprawy albo chociaż jadalne grzyby.\n\n" +
            "I weź tę jego chochlę na trofeum! Będzie komicznie wyglądać przy siodle!\n\n" +
            "Zdobywasz %d pkt doświadczenia.\n\n";

    public Ogre() {
        super("Ogre", 80, 30, 50, imagePath, encounterText, deathText, "Zwiększone obrażenia przy niskim zdrowiu");
    }

    @Override
    public int attack() {
        checkRage();
        int baseDamage = super.attack();

        if (isEnraged) {
            return (int)(baseDamage * 1.5);
        }

        return baseDamage;
    }

    private void checkRage() {
        double healthPercentage = (double)getHealth() / getMaxHealth();
        if (healthPercentage <= RAGE_THRESHOLD && !isEnraged) {
            isEnraged = true;
        }
    }

    public boolean isEnraged() {
        return isEnraged;
    }
}