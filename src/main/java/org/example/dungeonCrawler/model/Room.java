package org.example.dungeonCrawler.model;

import org.example.dungeonCrawler.model.items.*;
import org.example.dungeonCrawler.model.items.armors.*;
import org.example.dungeonCrawler.model.items.keys.Key;
import org.example.dungeonCrawler.model.items.potions.*;
import org.example.dungeonCrawler.model.items.weapons.*;
import org.example.dungeonCrawler.model.monsters.*;
import java.util.Objects;
import java.util.Random;

public class Room {
    private final Random random = new Random();
    public void removeItem() {
        this.item = null;
    }

    public boolean isTreasureOpened() {
        return treasureOpened;
    }

    public enum RoomType {
        WALL, ROOM, START, BOSS, TREASURE, EVENT, MERCHANT
    }

    private RoomType type;
    private Enemy enemy;
    private Item item;
    private boolean visited;
    private String description;
    private boolean treasureOpened;

    public Room(RoomType type) {
        this.type = type;
        this.visited = false;
        this.treasureOpened = false;
        generateContent();
    }

    private void generateContent() {
        switch (type) {
            case ROOM:
                description = "Duży pokój z kamiennymi ścianami.";
                break;
            case TREASURE:
                item = createTreasureItem();
                description = "Komora skarbca błyszczy w świetle pochodów.";
                break;
            case BOSS:
                enemy = createBossEnemy();
                description = "Ogromna komnata z wysokim sklepieniem. Czujesz obecność potężnego przeciwnika.";
                break;
            case START:
                description = "Wejście do lochów. Stąd rozpoczyna się twoja przygoda.";
                break;
            case EVENT:
                description = "Dziwny symbol na podłodze emanuje tajemniczą energią. Podejdziesz bliżej?";
                if (random.nextInt(100) < 55) {
                    this.enemy = createRandomEnemy();
                } else {
                    this.item = createRandomItem();
                }
                break;
            case MERCHANT:
                description = "Stoisz przed straganem kupca, który oferuje różne przedmioty.";
                this.enemy = null;
                this.item = null;
                break;
            default:
                description = "Nic szczególnego.";
        }
    }
    public Enemy createRandomEnemy() {
        int chance = random.nextInt(100);

        if (chance < 25) {
            return new Ruehin();
        } else if (chance < 40) {
            return new Glustyworp();
        } else if (chance < 75) {
            return new Fiend();
        } else if (chance < 90) {
            return new Vypper();
        } else {
            return new Ogre();
        }
    }
    public void consumeEvent() {
        if (this.type == RoomType.EVENT) {
            setType(RoomType.ROOM);
            setEnemy(null);
            setItem(null);
            setDescription("Przeszukałeś to miejsce. Nic więcej tu nie ma.");
        }
    }

    public Enemy createBossEnemy() {
        return new Villentretenmerth();
    }

    public Item createRandomItem() {
        double chance = Math.random();

        if (chance < 0.50) {
            return new CoinPile();
        }

        if (chance < 0.65) {
            return new SmallHealthPotion();
        }

        if (chance < 0.90) {
            Item[] commonItems = {
                    new Key(),
                    new AntidotePotion(),
                    new BoneShield(),
                    new LeatherArmor(),
                    new IronSword()

            };
            return commonItems[(int) (Math.random() * commonItems.length)];
        }

        if (chance < 0.975) {
            Item[] rareItems = {
                    new MediumHealthPotion(),
                    new StrengthPotion(),
                    new SteelSword(),
                    new ChainMail()
            };
            return rareItems[(int) (Math.random() * rareItems.length)];
        }

        else {
            Item[] epicItems = {
                    new BattleAxe(),
                    new PlateArmor(),
                    new SilverSword()
            };
            return epicItems[(int) (Math.random() * epicItems.length)];
        }
    }


    public void degradeEventToItemRoom() {
        if (this.type == Room.RoomType.EVENT) {
            this.type = Room.RoomType.ROOM;
            this.enemy = null;
            this.description = "Coś tu leży. Zostawiłeś to na później.";
        }
    }

    public Item createTreasureItem() {
        Item[] treasures = {
                new DragonScaleArmor(),
                new LargeHealthPotion(),
                new DragonSword()
        };
        return treasures[(int) (Math.random() * treasures.length)];
    }

    public void visit() {
        this.visited = true;
    }

    public boolean isPassable() {
        return type != RoomType.WALL;
    }


    public RoomType getType() {
        return type;
    }

    public Enemy getEnemy() {
        return enemy;
    }

    public Item getItem() {
        return item;
    }

    public boolean isVisited() {
        return visited;
    }

    public String getDescription() {
        return Objects.requireNonNullElse(description, "Nieopisane miejsce.");
    }

    public void setType(RoomType type) {
        this.type = type;
        generateContent();
    }

    public void setEnemy(Enemy enemy) {
        this.enemy = enemy;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTreasureOpened(boolean treasureOpened) {
        this.treasureOpened = treasureOpened;
    }

}