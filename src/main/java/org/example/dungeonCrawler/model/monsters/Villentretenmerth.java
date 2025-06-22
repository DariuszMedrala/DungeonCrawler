package org.example.dungeonCrawler.model.monsters;
import org.example.dungeonCrawler.model.Enemy;

public class Villentretenmerth extends Enemy {
    private static final double FIRE_BREATH_CHANCE = 0.4;
    private static final int FIRE_BREATH_DAMAGE = 25;
    private boolean hasUsedFireBreath = false;
    private static final String imagePath = "/images/villentretenmerth.png";

    private static final String encounterText = "O w mordę... To nie jest jakiś bies czy inny widłogon.\n" +
            "Krasnoludy sprzedały ci lipne info z kolorem.\n" +
            "To jest prawdziwy, cholerny, złoty SMOK.\n" +
            "Wiesz ile legend o nim słyszałem?\n" +
            "I wiesz ile złota może mieć w swojej komnacie?! Ale... chwila. To chyba TEN smok.\n" +
            "Wiedźminie, mamy problem. Kodeks mówi 'nie zabijać'.\n" +
            "Zdrowy rozsądek mówi 'SPIEPRZAJ'. A chciwość szepcze... 'pomyśl o tej chwale'.\n" +
            "To jest ostatni test. Jesteś wiedźminem... czy tylko rzeźnikiem w pogoni za kasą?\n"+
            "DECYDUJ BO ZARAZ ON ZADECYDUJE ZA NAS!\n\n";

    private static final String deathText = "I... cisza. Zabiłeś legendę, wiedźminie. Nie ma fajerwerków, nie ma okrzyków radości.\n" +
            "Spójrz na niego. To nie był potwór. To była... historia. A my ją właśnie wymazaliśmy z tego świata.\n" +
            "Cóż... Skoro już to zrobiłeś, bierz co twoje. Łuski, zęby, serce.\n"+ "" +
            "Będziemy bogaci. Najbogatsi i najbardziej przeklęci na całym kontynencie.\n" +
            "To koniec twojej ścieżki. Mam nadzieję, że było warto. Bo odwrotu z tej drogi już nie ma.\n\n" +
            "Zdobywasz %d pkt doświadczenia.\n\n";

    public Villentretenmerth() {
        super("Villentretenmerth", 200, 35, 500, imagePath, encounterText, deathText, "Ognisty oddech, Regeneracja zdrowia");
    }

    @Override
    public int attack() {
        if (Math.random() < FIRE_BREATH_CHANCE && !hasUsedFireBreath) {
            this.lastAttackType = "FIRE_BREATH";
            return fireBreathAttack();
        }
        this.lastAttackType = "NORMAL";
        return super.attack();
    }

    private int fireBreathAttack() {
        hasUsedFireBreath = true;
        return FIRE_BREATH_DAMAGE + super.attack();
    }

    public int regenerate() {
        int healAmount = 15;
        heal(healAmount);
        return healAmount;
    }

    private void heal(int amount) {
        int newHealth = Math.min(getHealth() + amount, getMaxHealth());
    }
}