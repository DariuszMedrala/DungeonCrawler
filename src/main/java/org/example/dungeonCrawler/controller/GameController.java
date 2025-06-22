package org.example.dungeonCrawler.controller;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.util.Duration;
import org.example.dungeonCrawler.model.*;
import org.example.dungeonCrawler.model.items.CoinPile;
import org.example.dungeonCrawler.model.monsters.*;
import org.example.dungeonCrawler.view.GameView;
import org.example.dungeonCrawler.model.items.Item;

import java.util.Random;

public class GameController {
    private Player player;
    private Map map;
    private Room currentRoom;
    private GameView gameView;
    private final Random random = new Random();

    private Enemy currentEnemy = null;

    public GameController() {
        initializeGame();
    }

    private void initializeGame() {
        this.map = new Map(30, 30);
        Map.Point startPos = map.getStartPosition();
        this.player = new Player(startPos.x, startPos.y);
        this.currentRoom = map.getRoom(startPos.x, startPos.y);
        this.currentEnemy = null;

        if (currentRoom != null) {
            currentRoom.visit();
        }
    }

    public void setGameView(GameView gameView) {
        this.gameView = gameView;
    }

    public void movePlayer(int dx, int dy) {
        if (currentEnemy != null) return;

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

            if (gameView != null && currentEnemy == null) {
                gameView.updateDisplay();
            }
        }
    }

    private void handleRoomContent() {
        if (currentRoom == null) return;
        if (currentRoom.getType() == Room.RoomType.EVENT) {
            if (gameView != null) gameView.showEventRoomDialog(currentRoom);
            return;
        }
        if (currentRoom.getType() == Room.RoomType.MERCHANT) {
            if (gameView != null) gameView.showMerchantDialog(player);
            return;
        }
        Enemy enemy = currentRoom.getEnemy();
        Item item = currentRoom.getItem();

        if (enemy != null && enemy.isAlive()) {
            startCombat(enemy);
        } else if (item instanceof CoinPile) {
            int coinsFound = 1 + random.nextInt(5);
            player.addCoins(coinsFound);
            currentRoom.removeItem();
            gameView.showCoinFoundDialog(coinsFound);
            gameView.updateDisplay();
        } else if (currentRoom.getType() == Room.RoomType.TREASURE && !currentRoom.isTreasureOpened()) {
            gameView.showTreasureDiscoveryDialog(item, currentRoom, player);
        } else if (item != null) {
            gameView.showItemDiscoveryDialog(item, currentRoom);
        }
    }

    private void startCombat(Enemy enemy) {
        currentEnemy = enemy;
        player.resetCombatStats();
        if (gameView != null) {
            gameView.showCombatChoiceDialog(enemy, true);
        }
    }

    public void beginInteractiveCombat() {
        if (gameView != null && currentEnemy != null) {
            gameView.showInteractiveCombatScreen(currentEnemy);
        }
    }

    public void handlePlayerAction(String actionType) {
        if (currentEnemy == null || !player.isAlive() || !currentEnemy.isAlive()) return;

        int playerDamage = 0;
        int actualDamage;

        switch (actionType) {
            case "ATTACK":
                playerDamage = player.getDamage() + (int)(Math.random() * 5);
                actualDamage = currentEnemy.takeDamage(playerDamage);

                if (actualDamage == 0 && playerDamage > 0) {
                    gameView.updateCombatLog(currentEnemy.getName() + " zręcznie unika Twojego ciosu!", "enemy-log-miss");
                } else if (actualDamage < playerDamage) {
                    gameView.updateCombatLog(currentEnemy.getName() + " blokuje część ataku, otrzymując tylko " + actualDamage + " pkt. obrażeń!", "enemy-log");
                } else {
                    gameView.updateCombatLog("Zadajesz " + actualDamage + " pkt. obrażeń!", "player-log");
                }
                break;

            case "CRITICAL_ATTACK":
                if (random.nextDouble() < 0.5) {
                    playerDamage = player.getDamage() * 2;
                    actualDamage = currentEnemy.takeDamage(playerDamage);

                    if (actualDamage == 0 && playerDamage > 0) {
                        gameView.updateCombatLog("Krytyczny cios, ale " + currentEnemy.getName() + " zdołał go uniknąć!", "enemy-log-miss");
                    } else if (actualDamage < playerDamage) {
                        gameView.updateCombatLog("Mocny cios! " + currentEnemy.getName() + " blokuje go, otrzymując " + actualDamage + " pkt. obrażeń krytycznych!", "player-log-crit");
                    } else {
                        gameView.updateCombatLog("Trafienie krytyczne! Zadajesz " + actualDamage + " pkt. obrażeń!", "player-log-crit");
                    }
                } else {
                    gameView.updateCombatLog("Atak krytyczny spudłował!", "player-log-miss");
                }
                break;
            case "IGNI":
                if (player.hasUsedIgni()) break;
                int igniDamage = 35 + random.nextInt(11);
                int actualIgniDamage = currentEnemy.takeDamage(igniDamage);
                player.setUsedIgni(true);

                if (actualIgniDamage == 0) {
                    gameView.updateCombatLog(currentEnemy.getName() + " unika płomieni znaku Igni!", "enemy-log-miss");
                } else if (actualIgniDamage < igniDamage) {
                    gameView.updateCombatLog(currentEnemy.getName() + " częściowo osłania się przed Igni, otrzymując " + actualIgniDamage + " pkt. obrażeń!", "player-log-sign");
                } else {
                    gameView.updateCombatLog("Znak Igni osmala wroga, zadając " + actualIgniDamage + " pkt. obrażeń!", "player-log-sign");
                }
                break;
            case "AARD":
                if (player.hasUsedAard()) break;
                int aardDamage = 5 + random.nextInt(6);
                int actualAardDamage = currentEnemy.takeDamage(aardDamage);
                currentEnemy.stun(1);
                player.setUsedAard(true);

                String stunMessage = " i ogłusza go na 1 turę!";
                if (actualAardDamage == 0) {
                    gameView.updateCombatLog(currentEnemy.getName() + " unika fali uderzeniowej Aard, ale i tak zostaje ogłuszony!", "enemy-log-miss");
                } else if (actualAardDamage < aardDamage) {
                    gameView.updateCombatLog("Aard trafia " + currentEnemy.getName() + ", który blokuje część siły! Otrzymuje " + actualAardDamage + " pkt. obrażeń" + stunMessage, "player-log-sign");
                } else {
                    gameView.updateCombatLog("Znak Aard rani wroga za " + actualAardDamage + " pkt. obrażeń" + stunMessage, "player-log-sign");
                }
                break;
            case "QUEN":
                if (player.hasUsedQuen()) break;
                player.activateQuen();
                player.setUsedQuen(true);
                gameView.updateCombatLog("Tworzysz tarczę Quen, która zablokuje następny atak.", "player-log-sign");
                break;
            case "AKSJI":
                if (player.hasUsedAksji()) break;
                currentEnemy.stun(2);
                player.setUsedAksji(true);
                gameView.updateCombatLog("Używasz znaku Aksji, paraliżuje wroga na 2 tury!", "player-log-sign");
                break;
        }

        gameView.refreshCombatScreenState(currentEnemy);

        if (!currentEnemy.isAlive()) {
            PauseTransition victoryDelay = new PauseTransition(Duration.seconds(1.2));
            victoryDelay.setOnFinished(e -> handleEnemyDefeated(currentEnemy));
            victoryDelay.play();
            return;
        }

        PauseTransition enemyTurnDelay = new PauseTransition(Duration.seconds(1.0));
        enemyTurnDelay.setOnFinished(event -> {

            if (currentEnemy.isStunned()) {
                gameView.updateCombatLog(currentEnemy.getName() + " jest ogłuszony i nie może się ruszyć.", "enemy-log-miss");
                currentEnemy.decrementStun();
            } else {
                handleEnemyCounterAttack(currentEnemy);
            }

            player.processTurnEffects();
            gameView.refreshCombatScreenState(currentEnemy);

            if (!player.isAlive()) {
                PauseTransition gameOverDelay = new PauseTransition(Duration.seconds(1.2));
                gameOverDelay.setOnFinished(e -> gameOver());
                gameOverDelay.play();
            } else {
                gameView.enableActionButtons();
            }
        });
        enemyTurnDelay.play();
    }


    private void handleEnemyDefeated(Enemy enemy) {
        int expGained = enemy.getExperienceReward();
        boolean levelUp = player.gainExperience(expGained);

        gameView.closeCombatScreen();
        currentRoom.setEnemy(null);
        currentEnemy = null;

        if (currentRoom.getType() == Room.RoomType.BOSS) {
            Platform.runLater(() -> gameView.showVictory(enemy, expGained));
        } else {
            Platform.runLater(() -> gameView.showCombatResultDialog(enemy, true, expGained, levelUp));
        }
    }

    private void handleEnemyCounterAttack(Enemy enemy) {
        String message;

        if (player.isQuenActive()) {
            player.consumeQuen();
            message = "Tarcza Quen absorbuje cały atak od " + enemy.getName() + "!";
            gameView.updateCombatLog(message, "player-log-quen");
            return;
        }

        int enemyDamage = enemy.attack();

        if (enemy instanceof Vypper) {
            ((Vypper) enemy).poisonAttack(player);
            int poisonAttackDamage = enemy.getDamage();
            message = enemy.getName() + " pluje jadem! Otrzymujesz " + poisonAttackDamage + " pkt. obrażeń i zostajesz zatruty!";
            gameView.updateCombatLog(message, "enemy-log");

        } else if (enemy instanceof Ogre && ((Ogre) enemy).isEnraged()) {
            player.takeDamage(enemyDamage);
            message = "Wściekły " + enemy.getName() + " atakuje z furią! Otrzymujesz " + enemyDamage + " pkt. obrażeń!";
            gameView.updateCombatLog(message, "enemy-log");

        } else if (enemy instanceof Villentretenmerth) {
            player.takeDamage(enemyDamage);

            if ("FIRE_BREATH".equals(enemy.lastAttackType)) {
                message = enemy.getName() + " zieje ogniem! Otrzymujesz " + enemyDamage + " pkt. obrażeń od płomieni!";
                gameView.updateCombatLog(message, "enemy-log");
            } else {
                message = enemy.getName() + " atakuje! Otrzymujesz " + enemyDamage + " pkt. obrażeń!";
                gameView.updateCombatLog(message, "enemy-log");
            }
            ((Villentretenmerth) enemy).regenerate();
            gameView.updateCombatLog(enemy.getName() + " regeneruje część zdrowia!", "player-log-sign");

        } else {
            player.takeDamage(enemyDamage);
            message = enemy.getName() + " atakuje! Otrzymujesz " + enemyDamage + " pkt. obrażeń!";
            gameView.updateCombatLog(message, "enemy-log");
        }
    }

    public void flee() {
        gameView.closeCombatScreen();
        currentEnemy = null;

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
        gameView.closeCombatScreen();
        currentEnemy = null;
        if (gameView != null) {
            gameView.showGameOver();
        }
    }

    public void triggerEvent(Room eventRoom) {
        if (eventRoom.getEnemy() != null && eventRoom.getEnemy().isAlive()) {
            startCombat(eventRoom.getEnemy());
            eventRoom.consumeEvent();

        } else if (eventRoom.getItem() != null) {
            Item item = eventRoom.getItem();

            if (item instanceof CoinPile) {
                int coinsFound = 1 + random.nextInt(5);
                player.addCoins(coinsFound);
                gameView.showCoinFoundDialog(coinsFound);
                eventRoom.consumeEvent();
            } else {
                boolean pickedUp = gameView.showItemDiscoveryDialog(item, eventRoom);
                if (pickedUp) {
                    eventRoom.consumeEvent();
                } else {
                    eventRoom.degradeEventToItemRoom();
                }
            }
        }
        gameView.updateDisplay();
    }
    public void declineEvent(Room eventRoom) {
        eventRoom.consumeEvent();
        gameView.updateDisplay();
    }

    public Enemy getEnemy() {
        return this.currentEnemy;
    }

    public Player getPlayer() { return player; }
    public Map getMap() { return map; }
    public Room getCurrentRoom() { return currentRoom; }
    public boolean isInCombat() { return currentEnemy != null; }
}