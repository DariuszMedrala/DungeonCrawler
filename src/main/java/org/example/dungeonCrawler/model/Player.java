package org.example.dungeonCrawler.model;

import org.example.dungeonCrawler.model.items.*;
import org.example.dungeonCrawler.model.items.armors.*;
import org.example.dungeonCrawler.model.items.keys.Key;
import org.example.dungeonCrawler.model.items.weapons.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Player {

    public static final int MAX_INVENTORY_SIZE = 10;

    private int x, y;
    private int health;
    private int maxHealth;
    private int baseDamage;
    private int level;
    private int experience;
    private int lvlUpThreshold;
    private int baseArmor;
    private final List<Item> inventory;
    private boolean isPoisoned;
    private int poisonDamage;
    private int poisonTurnsLeft;
    private boolean hasStrengthBonus;
    private int strengthBonusValue;
    private int strengthBonusTurnsLeft;
    private Weapon equippedWeapon;
    private Armor equippedArmor;
    private int coins;

    public Player(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.health = 1000;
        this.maxHealth = 1000;
        this.baseDamage = 15;
        this.level = 1;
        this.experience = 0;
        this.lvlUpThreshold = level * 100;
        this.baseArmor = 0;
        this.inventory = new ArrayList<>();
        this.isPoisoned = false;
        this.poisonDamage = 0;
        this.poisonTurnsLeft = 0;
        this.hasStrengthBonus = false;
        this.strengthBonusValue = 0;
        this.strengthBonusTurnsLeft = 0;
        this.equippedWeapon = null;
        this.equippedArmor = null;
        this.coins = 100;
    }

    public void move(int dx, int dy) {
        this.x += dx;
        this.y += dy;
    }

    public void takeDamage(int damage) {
        int totalArmor = baseArmor + (equippedArmor != null ? equippedArmor.getArmorValue() : 0);
        int finalDamage = Math.max(1, damage - totalArmor);
        this.health -= finalDamage;

        if (this.health < 0) {
            this.health = 0;
        }
    }

    public void heal(int amount) {
        this.health += amount;
        if (this.health > maxHealth) {
            this.health = maxHealth;
        }
    }

    public boolean gainExperience(int exp) {
        this.experience += exp;
        return checkLevelUp();
    }

    private boolean checkLevelUp() {
        if (experience >= lvlUpThreshold) {
            level++;
            experience -= lvlUpThreshold;
            lvlUpThreshold = level * 100;

            maxHealth += 20;
            health = maxHealth;
            baseDamage += 5;
            baseArmor += 2;
            return true;
        }
        else {
            return false;
        }
    }

    public void addItem(Item item) {
        inventory.add(item);
    }

    public void equipWeapon(Weapon weapon) {
        if (equippedWeapon != null) {
            inventory.add(equippedWeapon);
        }
        equippedWeapon = weapon;
        inventory.remove(weapon);
    }

    public void equipArmor(Armor armor) {
        if (equippedArmor != null) {
            inventory.add(equippedArmor);
        }
        equippedArmor = armor;
        inventory.remove(armor);
    }

    public int getDamage() {
        int weaponDamage = equippedWeapon != null ? equippedWeapon.getDamageBonus() : 0;
        int strengthBonus = hasStrengthBonus ? strengthBonusValue : 0;
        return baseDamage + weaponDamage + strengthBonus;
    }

    public void applyPoison(int damage, int duration) {
        this.isPoisoned = true;
        this.poisonDamage = damage;
        this.poisonTurnsLeft = duration;
    }

    public void curePoison() {
        this.isPoisoned = false;
        this.poisonDamage = 0;
        this.poisonTurnsLeft = 0;
    }

    public void processPoisonTurn() {
        if (isPoisoned) {
            health -= poisonDamage;
            poisonTurnsLeft--;

            if (poisonTurnsLeft <= 0) {
                curePoison();
            }

            if (health < 0) {
                health = 0;
            }
        }
    }

    public void applyStrengthBonus(int bonus, int duration) {
        this.hasStrengthBonus = true;
        this.strengthBonusValue = bonus;
        this.strengthBonusTurnsLeft = duration;
    }

    public void processStrengthBonusTurn() {
        if (hasStrengthBonus) {
            strengthBonusTurnsLeft--;
            if (strengthBonusTurnsLeft <= 0) {
                hasStrengthBonus = false;
                strengthBonusValue = 0;
            }
        }
    }
    public boolean hasKey() {
        for (Item item : inventory) {
            if (item instanceof Key) {
                return true;
            }
        }
        return false;
    }

    public void removeKey() {
        Iterator<Item> iterator = inventory.iterator();
        while (iterator.hasNext()) {
            Item item = iterator.next();
            if (item instanceof Key) {
                iterator.remove();
                return;
            }
        }
    }
    public void useItem(String itemName) {
        Item itemToUse = null;
        for (Item item : inventory) {
            if (item.getName().equals(itemName)) {
                itemToUse = item;
                break;
            }
        }

        if (itemToUse != null) {
            itemToUse.use(this);
            if (itemToUse.getType() == Item.ItemType.POTION) {
                for (int i = 0; i < inventory.size(); i++) {
                    if (inventory.get(i).equals(itemToUse)) {
                        inventory.remove(i);
                        break;
                    }
                }
            }
        }
    }

    public void processTurnEffects() {
        processPoisonTurn();
        processStrengthBonusTurn();
    }
    public int getCoins() {
        return coins;
    }

    public void addCoins(int amount) {
        if (amount > 0) {
            this.coins += amount;
        }
    }

    public boolean spendCoins(int amount) {
        if (amount > 0 && this.coins >= amount) {
            this.coins -= amount;
            return true;
        }
        return false;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public boolean isInventoryFull() {
        return inventory.size() >= MAX_INVENTORY_SIZE;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public int getLevel() { return level; }
    public int getExperience() { return experience; }
    public int getArmor() {
        return baseArmor + (equippedArmor != null ? equippedArmor.getArmorValue() : 0);
    }
    public int getLvlUpThreshold() { return lvlUpThreshold; }
    public List<Item> getInventory() { return inventory; }
    public Weapon getEquippedWeapon() { return equippedWeapon; }
    public Armor getEquippedArmor() { return equippedArmor; }
    public boolean isPoisoned() { return isPoisoned; }
    public boolean hasStrengthBonus() { return hasStrengthBonus; }
    public int getPoisonTurnsLeft() { return poisonTurnsLeft; }
    public int getStrengthBonusTurnsLeft() { return strengthBonusTurnsLeft; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
}