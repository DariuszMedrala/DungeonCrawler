package org.example.dungeonCrawler.controller;

import javafx.application.Platform;
import org.example.dungeonCrawler.model.*;
import org.example.dungeonCrawler.model.monsters.*;
import org.example.dungeonCrawler.view.GameView;
import org.example.dungeonCrawler.model.items.Item;

public class GameController {
    private Player player;
    private Map map;
    private Room currentRoom;
    private GameView gameView;
    private boolean inCombat;

    public GameController() {
        initializeGame();
    }

    private void initializeGame() {
        this.map = new Map(30, 30);

        Map.Point startPos = map.getStartPosition();
        this.player = new Player(startPos.x, startPos.y);

        this.currentRoom = map.getRoom(startPos.x, startPos.y);
        this.inCombat = false;

        if (currentRoom != null) {
            currentRoom.visit();
        }
    }

    public void setGameView(GameView gameView) {
        this.gameView = gameView;
    }

    public void movePlayer(int dx, int dy) {
        if (inCombat) return;

        int newX = player.getX() + dx;
        int newY = player.getY() + dy;

        if (map.isValidPosition(newX, newY)) {
            player.move(dx, dy);
            currentRoom = map.getRoom(newX, newY);
            currentRoom.visit();

            player.processTurnEffects();

            if (!player.isAlive()) {
                gameOver();
                return;
            }

            handleRoomContent();

            if (gameView != null && !inCombat) {
                gameView.updateDisplay();
            }
        }
    }

    private void handleRoomContent() {
        if (currentRoom == null) return;
        if (currentRoom.getType() == Room.RoomType.EVENT) {
            if (gameView != null) {
                gameView.showEventRoomDialog(currentRoom);
            }
            return;
        }
        Enemy enemy = currentRoom.getEnemy();
        Item item = currentRoom.getItem();

        if (enemy != null && enemy.isAlive()) {
            startCombat(enemy);
        } else if (currentRoom.getType() == Room.RoomType.TREASURE && !currentRoom.isTreasureOpened()) {
            gameView.showTreasureDiscoveryDialog(item, currentRoom, player);
        } else if (item != null) {
            gameView.showItemDiscoveryDialog(item, currentRoom);
        } else {
            handleSpecialRoom();
        }
    }

    private void handleSpecialRoom() {
        switch (currentRoom.getType()) {
            case BOSS:
                break;
            case TREASURE:
                break;
        }
    }


    private void startCombat(Enemy enemy) {
        inCombat = true;
        if (gameView != null) {
            gameView.showCombatChoiceDialog(enemy, true);
        }
    }

    public void attack() {
        Enemy enemy = currentRoom.getEnemy();

        while (player.isAlive() && enemy.isAlive()) {
            int playerDamage = player.getDamage() + (int)(Math.random() * 5);
            enemy.takeDamage(playerDamage);

            if (!enemy.isAlive()) {
                handleEnemyDefeated(enemy);
                break;
            }
            handleEnemyCounterAttack(enemy);

            player.processTurnEffects();

            if (!player.isAlive()) {;
                break;
            }
        }

        inCombat = false;
    }




    private void handleEnemyDefeated(Enemy enemy) {
        int expGained = enemy.getExperienceReward();
        boolean levelUp = player.gainExperience(expGained);

        inCombat = false;
        currentRoom.setEnemy(null);


        if (currentRoom.getType() == Room.RoomType.BOSS) {
            Platform.runLater(() -> gameView.showVictory(enemy, expGained));
        }else {
            Platform.runLater(() -> gameView.showCombatResultDialog(enemy, true, expGained, levelUp));
        }
    }

    public void triggerEvent(Room eventRoom) {
        if (eventRoom.getEnemy() != null && eventRoom.getEnemy().isAlive()) {
            inCombat = true;
            gameView.showCombatChoiceDialog(eventRoom.getEnemy(), false);
            eventRoom.consumeEvent();

        } else if (eventRoom.getItem() != null) {
            boolean pickedUp = gameView.showItemDiscoveryDialog(eventRoom.getItem(), eventRoom);
            if (pickedUp) {
                eventRoom.consumeEvent();
            } else {
                eventRoom.degradeEventToItemRoom();
            }
        }
        gameView.updateDisplay();
    }



    public void declineEvent(Room eventRoom) {
        eventRoom.consumeEvent();
        gameView.updateDisplay();
    }

    private void handleEnemyCounterAttack(Enemy enemy) {
        if (enemy instanceof Vypper) {
            ((Vypper) enemy).poisonAttack(player);
        } else if (enemy instanceof Villentretenmerth) {
            int enemyDamage = enemy.attack() + (int)(Math.random() * 3);
            player.takeDamage(enemyDamage);
            ((Villentretenmerth) enemy).regenerate();
        } else {
            int enemyDamage = enemy.attack() + (int)(Math.random() * 3);
            player.takeDamage(enemyDamage);
        }
    }

    public void flee() {
        inCombat = false;
        int fleeDamage = 20;
        player.takeDamage(fleeDamage);
        if (gameView != null) {
            Platform.runLater(() -> gameView.showCombatResultDialog(null, false, 0, false));
        }
        player.processTurnEffects();


        if (!player.isAlive()) {
            gameOver();
        }
    }

    private void gameOver() {
        inCombat = false;
        if (gameView != null) {
            gameView.showGameOver();
        }
    }

    public Player getPlayer() { return player; }
    public Map getMap() { return map; }
    public Room getCurrentRoom() { return currentRoom; }
    public boolean isInCombat() { return inCombat; }
}