package org.example.dungeonCrawler.model;

public class Enemy {
    private final String name;
    private int health;
    private final int maxHealth;
    private final int damage;
    private final int experienceReward;
    private final String imagePath;
    private final String encounterText;
    private final String deathText;


    public Enemy(String name, int health, int damage, int experienceReward, String imagePath, String encounterText, String deathText) {
        this.name = name;
        this.health = health;
        this.maxHealth = health;
        this.damage = damage;
        this.experienceReward = experienceReward;
        this.imagePath = imagePath;
        this.encounterText = encounterText;
        this.deathText = deathText;
    }

    public int attack() {
        return damage + (int)(Math.random() * 5);
    }

    public void takeDamage(int damage) {
        this.health -= damage;
        if (this.health < 0) {
            this.health = 0;
        }
    }

    public boolean isAlive() {
        return health > 0;
    }

    public String getName() { return name; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public int getDamage() { return damage; }
    public int getExperienceReward() { return experienceReward;}
    public String getImagePath() { return imagePath; }
    public String getEncounterText() { return encounterText; }
    public String getDeathText() { return deathText; }
}