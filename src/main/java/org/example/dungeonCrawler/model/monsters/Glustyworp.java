package org.example.dungeonCrawler.model.monsters;

import org.example.dungeonCrawler.model.Enemy;

public class Glustyworp extends Enemy {

    private static final String imagePath = "/images/glustyworp.png";
    private static final String encounterText = "Matko Boska, co to za skrzyżowanie homara z traktorem?! Wylazło z wody i kłapie tą paszczą!\n" +
            "Widzisz ten pancerz? Trzeba będzie w niego trochę postukać mieczem. Zrób z tej skorupy durszlak!\n" +
            "Na co czekasz, baranie?! Aż cię złapie w te szczypce i zapyta, czy podać frytki?!\n\n";

    private static final String deathText = "No i leży. A taki był pancerny i groźny, a teraz wygląda jak przewrócona taczka.\n" +
            "Tyle hałasu o jednego przerośniętego robala. Wystarczyło parę razy dźgnąć go tam, gdzie pancerz cieńszy.\n" +
            "Dobra, nie ciesz się tak. Bierz się za oprawianie! Mięso z tego pewnie twarde jak podeszwa, ale za te płyty pancerne jakiś kowal dobrze zapłaci!\n" +
            "Ruszaj się, zanim smród przyciągnie tu wszystkie utopce z okolicy!\n\n" +
            "Zdobywasz %d pkt doświadczenia.\n\n";

    public Glustyworp() {
        super("Glustyworp", 20, 10, 35, imagePath, encounterText, deathText);
    }
}