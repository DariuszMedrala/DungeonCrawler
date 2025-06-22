package org.example.dungeonCrawler.model;

public class Enemy {
    private final String name;
    protected int health;
    private final int maxHealth;
    private final int damage;
    private final int experienceReward;
    private final String imagePath;
    private final String encounterText;
    private final String deathText;
    public String lastAttackType = "NORMAL";
    private String specialAbility;

    private int stunnedTurns = 0;

    public Enemy(String name, int health, int damage, int experienceReward, String imagePath, String encounterText, String deathText, String specialAbility) {
        this.name = name;
        this.health = health;
        this.maxHealth = health;
        this.damage = damage;
        this.experienceReward = experienceReward;
        this.imagePath = imagePath;
        this.encounterText = encounterText;
        this.deathText = deathText;
        this.specialAbility = specialAbility;
    }

    public int attack() {
        return damage + (int)(Math.random() * 5);
    }

    public int takeDamage(int damage) {
        this.health -= damage;
        if (this.health < 0) {
            this.health = 0;
        }
        return damage;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public boolean isStunned() {
        return stunnedTurns > 0;
    }

    public void stun(int turns) {
        this.stunnedTurns = Math.max(this.stunnedTurns, turns);
    }

    public void decrementStun() {
        if (this.stunnedTurns > 0) {
            this.stunnedTurns--;
        }
    }

    public String getName() { return name; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public int getDamage() { return damage; }
    public int getExperienceReward() { return experienceReward;}
    public String getImagePath() { return imagePath; }
    public String getEncounterText() { return encounterText; }
    public String getDeathText() { return deathText; }

    public String getSpecialAbility() {
        return specialAbility;
    }

    public void setSpecialAbility(String specialAbility) {
        this.specialAbility = specialAbility;
    }
}