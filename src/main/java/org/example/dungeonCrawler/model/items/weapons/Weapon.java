package org.example.dungeonCrawler.model.items.weapons;

import org.example.dungeonCrawler.model.Player;
import org.example.dungeonCrawler.model.items.Item;

public abstract class Weapon extends Item {
    protected int damageBonus;
    protected WeaponType weaponType;

    public enum WeaponType {
        SWORD, AXE, DAGGER, STAFF
    }

    public Weapon(String name, String description, int damageBonus, WeaponType weaponType) {
        super(name, description, ItemType.WEAPON);
        this.damageBonus = damageBonus;
        this.weaponType = weaponType;
    }

    @Override
    public void use(Player player) {
        player.equipWeapon(this);
    }

    public int getDamageBonus() { return damageBonus; }
    public WeaponType getWeaponType() { return weaponType; }
}