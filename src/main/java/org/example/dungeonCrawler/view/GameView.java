package org.example.dungeonCrawler.view;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.*;
import javafx.stage.StageStyle;
import org.example.dungeonCrawler.controller.GameController;
import org.example.dungeonCrawler.model.*;
import org.example.dungeonCrawler.model.items.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.example.dungeonCrawler.Main;
import org.example.dungeonCrawler.model.items.armors.Armor;
import org.example.dungeonCrawler.model.items.keys.Key;
import org.example.dungeonCrawler.model.items.potions.Potion;
import org.example.dungeonCrawler.model.items.weapons.Weapon;

public class GameView {
    private final Main mainApp;
    private final GameController controller;
    private BorderPane gameLayout;
    private GridPane mapGrid;
    private ScrollPane mapScrollPane;
    private Label playerStatsLabel;
    private Label inventoryLabel;
    private Label statusEffectsLabel;
    private final String playerName;

    private VBox sidePanel;
    private VBox playerStatsSection;
    private VBox inventorySection;
    private VBox statusEffectsSection;

    private Stage backpackStage;
    private VBox backpackItemsContainer;
    private AudioClip buttonClickSound;
    private MediaPlayer gameMusicPlayer;
    private MediaPlayer victoryMusicPlayer;

    private Stage combatChoiceStage;
    private Stage combatResultStage;

    public GameView(Main mainApp, String playerName) {
        this.mainApp = mainApp;
        this.playerName = playerName;
        this.controller = new GameController();
        controller.setGameView(this);
        loadSounds();
        createMainLayout();
    }

    public Parent getViewLayout() {
        return gameLayout;
    }

    public void playMusic() {
        if (gameMusicPlayer != null) {
            gameMusicPlayer.play();
        }
    }


    private void loadSounds() {

        String soundPath = Objects.requireNonNull(getClass().getResource("/sounds/button_click.wav")).toExternalForm();
        buttonClickSound = new AudioClip(soundPath);
        buttonClickSound.setVolume(0.05);
        String musicPath = Objects.requireNonNull(getClass().getResource("/music/game_music.mp3")).toExternalForm();
        Media gameMusic = new Media(musicPath);
        gameMusicPlayer = new MediaPlayer(gameMusic);
        gameMusicPlayer.setVolume(0.1);
        gameMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);

        String victoryMusicPath = Objects.requireNonNull(getClass().getResource("/music/victory_music.mp3")).toExternalForm();
        Media victoryMusic = new Media(victoryMusicPath);
        victoryMusicPlayer = new MediaPlayer(victoryMusic);
        victoryMusicPlayer.setVolume(0.1);
        victoryMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
    }

    private void playButtonClickSound() {
        if (buttonClickSound != null) {
            buttonClickSound.play();
        }
    }

    private void createMainLayout() {
        gameLayout = new BorderPane();
        gameLayout.getStyleClass().add("root");

        createMapArea();
        gameLayout.setCenter(mapScrollPane);

        createSidePanel();
        gameLayout.setRight(sidePanel);
    }

    private void createMapArea() {
        mapGrid = new GridPane();
        mapGrid.setAlignment(Pos.CENTER);
        mapGrid.getStyleClass().add("map-grid");

        mapScrollPane = new ScrollPane(mapGrid);
        mapScrollPane.setFitToWidth(true);
        mapScrollPane.setFitToHeight(true);
        mapScrollPane.getStyleClass().add("map-scroll");
    }

    private String toWebColor(Color color) {
        if (color == null) return "transparent";
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    public void showWelcomeDialog() {
        Stage welcomeStage = new Stage();
        welcomeStage.initModality(Modality.APPLICATION_MODAL);
        welcomeStage.initOwner(mainApp.getPrimaryStage());
        welcomeStage.setTitle("Witaj w Lochach Novigradu!");
        welcomeStage.setResizable(false);
        welcomeStage.initStyle(StageStyle.UNDECORATED);
        welcomeStage.initModality(Modality.APPLICATION_MODAL);

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("custom-dialog-background");

        ImageView welcomeImage;

        welcomeImage = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/dungeon_entrance.png"))));
        welcomeImage.setFitHeight(550);
        welcomeImage.setPreserveRatio(true);

        StackPane imageContainer = new StackPane();
        imageContainer.getChildren().add(welcomeImage);
        imageContainer.getStyleClass().add("image-frame");
        imageContainer.setPrefWidth(Region.USE_COMPUTED_SIZE);

        VBox textAndButtonsVBox = new VBox(15);
        textAndButtonsVBox.setAlignment(Pos.CENTER);
        textAndButtonsVBox.setPadding(new Insets(0, 20, 30, 20));

        Text localTitle = new Text("Wiedźmin: Lochy Novigradu");
        localTitle.getStyleClass().add("about-title");
        VBox.setMargin(localTitle, new Insets(0, 0, 10, 0));

        TextFlow contentTextFlow = new TextFlow();
        contentTextFlow.setTextAlignment(TextAlignment.CENTER);
        contentTextFlow.getStyleClass().add("about-content-flow");

        Text welcomeMessage = new Text(
                "A tyle razy sobie powtarzałeś/aś,\n'" + playerName + ", trzymaj się z daleka od bagien i polityki.'\n" +
                        "Ale skoro już tu jesteś, to słuchaj: pod Novigradem coś gryzie ludzi.\n" +
                        "Coś wielkiego, zębatego i wkur**nego. Twoim zadaniem jest to znaleźć i ubić.\n" +
                        "Prosto, prawda? No, nie do końca.\n\n" +
                        "Słuchaj uważnie: widzisz ten żółty kwadrat? TO TY!\n" +
                        "Widzisz tego zielonego? TO TWOJ CEL.\n" +
                        "Już prościej Ci tego nie wytłumaczę.\n\n" +
                        "Powodzenia, wiedźminie. I nie daj się zabić. Chyba, że naprawdę musisz."
        );

        welcomeMessage.getStyleClass().add("about-content");
        welcomeMessage.setLineSpacing(5);

        contentTextFlow.getChildren().add(welcomeMessage);

        Button startButton = new Button("ROZUMIEM, ROZUMIEM...");
        startButton.getStyleClass().add("dialog-button-primary");
        startButton.setOnAction(e -> {
            playButtonClickSound();
            welcomeStage.close();
            gameLayout.requestFocus();
        });

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        textAndButtonsVBox.getChildren().addAll(localTitle, contentTextFlow, spacer, startButton);

        HBox mainContentHBox = new HBox(10);
        mainContentHBox.setAlignment(Pos.CENTER);

        imageContainer.getChildren();
        mainContentHBox.getChildren().addAll(imageContainer, textAndButtonsVBox);
        HBox.setHgrow(textAndButtonsVBox, Priority.ALWAYS);

        root.getChildren().addAll(mainContentHBox);

        Scene welcomeScene = new Scene(root, 1100, 650);
        welcomeScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());

        welcomeStage.setScene(welcomeScene);

        welcomeStage.setOnShown(e -> {
            Stage primaryStage = mainApp.getPrimaryStage();
            welcomeStage.setX(primaryStage.getX() + (primaryStage.getWidth() - welcomeStage.getWidth()) / 2);
            welcomeStage.setY(primaryStage.getY() + (primaryStage.getHeight() - welcomeStage.getHeight()) / 2);
        });

        welcomeStage.showAndWait();
    }

    public void updateMap() {
        mapGrid.getChildren().clear();
        mapGrid.getColumnConstraints().clear();
        mapGrid.getRowConstraints().clear();

        Map map = controller.getMap();
        Player player = controller.getPlayer();

        if (map == null || map.getWidth() == 0 || map.getHeight() == 0) return;
        if (player == null) return;

        for (int i = 0; i < map.getWidth(); i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / map.getWidth());
            mapGrid.getColumnConstraints().add(cc);
        }
        for (int i = 0; i < map.getHeight(); i++) {
            RowConstraints rc = new RowConstraints();
            rc.setPercentHeight(100.0 / map.getHeight());
            mapGrid.getRowConstraints().add(rc);
        }

        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                StackPane cellNode = new StackPane();
                Room room = map.getRoom(x, y);
                if (room == null) {
                    cellNode.setStyle("-fx-background-color: black; -fx-border-color: grey; -fx-border-width: 1px;");
                    mapGrid.add(cellNode, x, y);
                    continue;
                }

                StringBuilder style = new StringBuilder();
                Color backgroundColor;
                Color borderColor = Color.rgb(50, 50, 50);
                double borderWidth = 0.5;

                if (player.getX() == x && player.getY() == y) {
                    if (player.isPoisoned()) {
                        backgroundColor = Color.GREEN;
                        borderColor = Color.DARKGREEN;
                    } else if (player.hasStrengthBonus()) {
                        backgroundColor = Color.ORANGE;
                        borderColor = Color.DARKORANGE;
                    } else {
                        backgroundColor = Color.GOLD;
                        borderColor = Color.ORANGERED;
                    }
                    borderWidth = 2;
                } else if (room.getType() == Room.RoomType.WALL) {
                    backgroundColor = Color.rgb(20, 20, 20);
                    borderColor = Color.rgb(30, 30, 30);
                    borderWidth = 1;
                } else if (!room.isVisited()) {
                    backgroundColor = Color.rgb(40, 40, 40);
                } else {
                    switch (room.getType()) {
                        case START:
                            backgroundColor = Color.rgb(50, 150, 50);
                            break;
                        case BOSS:
                            backgroundColor = Color.rgb(80, 40, 40);
                            break;
                        case TREASURE:
                            backgroundColor = Color.rgb(87, 12, 89);
                            break;
                        case EVENT:
                            backgroundColor = Color.rgb(60, 20, 120);
                            break;
                        default:
                            backgroundColor = Color.rgb(80, 80, 80);
                    }

                    if (room.getEnemy() != null && room.getEnemy().isAlive()) {
                        if (room.getType() == Room.RoomType.BOSS) {
                            backgroundColor = Color.rgb(80, 80, 80);
                        } else if (room.getType() != Room.RoomType.EVENT) {
                            backgroundColor = Color.rgb(80, 80, 80);
                        }
                    } else if (room.getItem() != null && room.getType() != Room.RoomType.TREASURE) {
                        if (room.getType() != Room.RoomType.EVENT) {
                            backgroundColor = Color.rgb(70, 130, 180);
                        }
                    }
                }
                style.append("-fx-background-color: ").append(toWebColor(backgroundColor)).append(";");
                style.append("-fx-border-color: ").append(toWebColor(borderColor)).append(";");
                style.append("-fx-border-width: ").append(borderWidth).append("px;");
                cellNode.setStyle(style.toString());


                if (room.getType() == Room.RoomType.EVENT) {
                    Text eventMarker = new Text("❓");
                    eventMarker.setFont(Font.font("Arial", FontWeight.BOLD, 16));
                    eventMarker.setFill(Color.RED);
                    eventMarker.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.9), 2, 0.0, 0, 1);");
                    cellNode.getChildren().add(eventMarker);
                }
                else if (room.getEnemy() != null && room.getEnemy().isAlive() && !(x == player.getX() && y == player.getY())) {
                    String markerSymbol;
                    double markerFontSize;
                    Color symbolFillColor;

                    if (room.getType() == Room.RoomType.BOSS) {
                        markerSymbol = "🐉";
                        markerFontSize = 16;
                        symbolFillColor = Color.YELLOW;
                    } else {
                        markerSymbol = "💀";
                        markerFontSize = 14;
                        if ((backgroundColor.getRed() + backgroundColor.getGreen() + backgroundColor.getBlue()) / 3.0 < 0.5) {
                            symbolFillColor = Color.WHITE;
                        } else {
                            symbolFillColor = Color.BLACK;
                        }
                    }
                    Text enemyMarker = new Text(markerSymbol);
                    enemyMarker.setFont(Font.font("Arial", FontWeight.BOLD, markerFontSize));
                    enemyMarker.setFill(symbolFillColor);
                    enemyMarker.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.85), 1.5, 0.0, 0, 1);");
                    cellNode.getChildren().add(enemyMarker);
                }
                mapGrid.add(cellNode, x, y);
            }
        }
    }

    private HBox createLegendItem(Color color, String description, String symbol, Color symbolColorOverride) {
        HBox legendItem = new HBox(12);
        legendItem.setAlignment(Pos.CENTER_LEFT);
        legendItem.setPadding(new Insets(4, 0, 4, 0));

        StackPane symbolSwatchContainer = new StackPane();
        symbolSwatchContainer.setPrefSize(28, 28);
        symbolSwatchContainer.setMinSize(28, 28);

        Region colorSwatch = new Region();
        String swatchStyle = "-fx-background-color: " + toWebColor(color) + "; -fx-border-color: #444; -fx-border-width: 1px;";
        colorSwatch.setStyle(swatchStyle);
        symbolSwatchContainer.getChildren().add(colorSwatch);

        if (symbol != null && !symbol.isEmpty()) {
            Text symbolText = new Text(symbol);
            symbolText.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            if (symbolColorOverride != null) {
                symbolText.setFill(symbolColorOverride);
            } else {
                if (color != null && (color.getRed() + color.getGreen() + color.getBlue()) / 3.0 < 0.5) {
                    symbolText.setFill(Color.WHITE);
                } else {
                    symbolText.setFill(Color.BLACK);
                }
            }
            symbolText.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 1, 0.0, 0, 1);");
            symbolSwatchContainer.getChildren().add(symbolText);
        }

        legendItem.getChildren().add(symbolSwatchContainer);

        Label descLabel = new Label(description);
        descLabel.getStyleClass().add("legend-text");
        HBox.setMargin(descLabel, new Insets(0, 0, 0, 5));

        legendItem.getChildren().add(descLabel);
        return legendItem;
    }

    public void showEventRoomDialog(Room eventRoom) {
        Stage eventStage = new Stage();
        eventStage.initModality(Modality.APPLICATION_MODAL);
        eventStage.initOwner(mainApp.getPrimaryStage());
        eventStage.setTitle("Tajemnicze Miejsce");
        eventStage.setResizable(false);
        eventStage.setOnCloseRequest(Event::consume);
        eventStage.initStyle(StageStyle.UNDECORATED);
        eventStage.initModality(Modality.APPLICATION_MODAL);

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("custom-dialog-background");

        ImageView eventImage;
        eventImage = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/surprise.png"))));
       

        StackPane imageContainer = new StackPane();
        imageContainer.getStyleClass().add("image-frame");


        double targetImageHeight = 400;
        eventImage.setFitHeight(targetImageHeight);
        eventImage.setPreserveRatio(true);
        imageContainer.getChildren().add(eventImage);
        
        double imageRatio = eventImage.getImage().getWidth() / eventImage.getImage().getHeight();
        double targetImageWidth = targetImageHeight * imageRatio;
        
        double framePadding = 5.0; 
        double finalFrameWidth = targetImageWidth + (2 * framePadding);
        double finalFrameHeight = targetImageHeight + (2 * framePadding);
        
        imageContainer.setPrefSize(finalFrameWidth, finalFrameHeight);
        imageContainer.setMinSize(finalFrameWidth, finalFrameHeight);
        imageContainer.setMaxSize(finalFrameWidth, finalFrameHeight);
        

        VBox textAndButtonsVBox = new VBox(15);
        textAndButtonsVBox.setAlignment(Pos.CENTER);

        Label title = new Label("❓ TAJEMNICZE MIEJSCE ❓");
        title.getStyleClass().add("about-title");

        Label contentLabel = new Label(
                "A to co, u licha? Znak zapytania nabazgrany na podłodze.\n" +
                        "Albo jakiś pijany mag próbował rzucić zaklęcie i zapomniał końcówki,\n" +
                        "albo to zaproszenie na herbatkę z sukkubami.\n" +
                        "Z jednej strony może tam leżeć legendarny miecz, góra złota albo chociaż czyste onuce.\n" +
                        "Z drugiej – może czekać stado wygłodniałych ghuli, pułapka gazowa albo, co gorsza, rachunek od poborcy podatkowego.\n" +
                        "Jedno jest pewne – za takimi drzwiami rzadko czeka miska ciepłej zupy.\n\n" +
                        "No dalej, wiedźminie, rusz głową. Gramy w ‘va banque’\n" +
                        "czy zwijamy manatki i udajemy, że tego nie widzieliśmy?"
        );
        contentLabel.getStyleClass().add("about-content");
        contentLabel.setTextAlignment(TextAlignment.CENTER);
        contentLabel.setWrapText(true);

        Button yesButton = new Button("✅ TAK, WCHODZĘ");
        yesButton.getStyleClass().add("dialog-button-primary");
        yesButton.setOnAction(e -> {
            playButtonClickSound();
            eventStage.close();
            Platform.runLater(() -> controller.triggerEvent(eventRoom));
        });

        Button noButton = new Button("❌ NIE, REZYGNUJĘ");
        noButton.getStyleClass().add("dialog-button-secondary");
        noButton.setOnAction(e -> {
            playButtonClickSound();
            eventStage.close();
            Platform.runLater(() -> controller.declineEvent(eventRoom));
        });

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(yesButton, noButton);

        textAndButtonsVBox.getChildren().addAll(title, contentLabel, buttonBox);

        HBox mainContentHBox = new HBox(25);
        mainContentHBox.setAlignment(Pos.CENTER);
        mainContentHBox.getChildren().addAll(imageContainer, textAndButtonsVBox);
        HBox.setHgrow(textAndButtonsVBox, Priority.ALWAYS);

        root.getChildren().add(mainContentHBox);

        Scene eventScene = new Scene(root, 1100, 470);
        
        eventScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
        eventStage.setScene(eventScene);
        eventStage.sizeToScene();

        eventStage.setOnShown(e -> {
            Stage primaryStage = mainApp.getPrimaryStage();
            eventStage.setX(primaryStage.getX() + (primaryStage.getWidth() - eventStage.getWidth()) / 2);
            eventStage.setY(primaryStage.getY() + (primaryStage.getHeight() - eventStage.getHeight()) / 2);
        });

        eventStage.showAndWait();
    }


    private void createSidePanel() {
        sidePanel = new VBox();
        sidePanel.setPadding(new Insets(15));
        sidePanel.getStyleClass().add("side-panel");
        sidePanel.setPrefWidth(380);

        Label sidePanelTitleLabel = new Label("WIEDŹMIN: " + playerName.toUpperCase());
        sidePanelTitleLabel.getStyleClass().add("panel-title");
        HBox titleContainer = new HBox(sidePanelTitleLabel);
        titleContainer.setAlignment(Pos.CENTER);
        VBox.setMargin(titleContainer, new Insets(0, 0, 10, 0));

        createPlayerStatsSection();
        createInventorySection();
        createStatusEffectsSection();

        VBox.setMargin(inventorySection, new Insets(10, 0, 0, 0));
        VBox.setMargin(statusEffectsSection, new Insets(0, 0, 0, 0));

        Button openBackpackButton = new Button("🎒 Otwórz Plecak");
        openBackpackButton.getStyleClass().add("action-button");
        openBackpackButton.setMaxWidth(Double.MAX_VALUE);
        openBackpackButton.setOnAction(e -> {
            playButtonClickSound();
            showBackpackWindow();
        });

        Button newMenuButton = new Button("📜 Menu");
        newMenuButton.getStyleClass().add("action-button");
        newMenuButton.setMaxWidth(Double.MAX_VALUE);
        newMenuButton.setOnAction(e -> {
            playButtonClickSound();
            showMenuWindow();
        });

        Button legendDisplayButton = new Button("🗺 Pokaż Legendę");
        legendDisplayButton.getStyleClass().add("action-button");
        legendDisplayButton.setMaxWidth(Double.MAX_VALUE);
        legendDisplayButton.setOnAction(e -> {
            playButtonClickSound();
            showLegendDialog();
        });

        VBox actionButtonsContainer = new VBox(8);
        actionButtonsContainer.setAlignment(Pos.BOTTOM_CENTER);
        actionButtonsContainer.getChildren().addAll(openBackpackButton, newMenuButton, legendDisplayButton);
        VBox.setMargin(actionButtonsContainer, new Insets(10, 0, 0, 0));

        sidePanel.getChildren().clear();
        sidePanel.getChildren().addAll(
                titleContainer,
                playerStatsSection,
                inventorySection,
                statusEffectsSection,
                actionButtonsContainer
        );

        VBox.setVgrow(inventorySection, Priority.ALWAYS);
    }

    private void createPlayerStatsSection() {
        playerStatsSection = new VBox(2);
        playerStatsSection.getStyleClass().add("section");
        Label statsTitle = new Label("STATYSTYKI");
        statsTitle.getStyleClass().add("section-title");
        playerStatsLabel = new Label("Ładowanie statystyk...");
        playerStatsLabel.getStyleClass().clear();
        playerStatsLabel.getStyleClass().add("status-effects");
        playerStatsLabel.setWrapText(true);
        playerStatsLabel.setMaxWidth(Double.MAX_VALUE);

        playerStatsSection.getChildren().addAll(statsTitle, playerStatsLabel);
    }

    private void createInventorySection() {
        inventorySection = new VBox(5);
        inventorySection.getStyleClass().add("section");

        inventoryLabel = new Label("Ładowanie ekwipunku...");
        inventoryLabel.getStyleClass().clear();
        inventoryLabel.getStyleClass().add("status-effects");
        inventoryLabel.setWrapText(true);
        inventoryLabel.setMaxWidth(Double.MAX_VALUE);

        inventorySection.getChildren().addAll(inventoryLabel);
    }


    private void createStatusEffectsSection() {
        statusEffectsSection = new VBox(5);
        statusEffectsSection.getStyleClass().add("section");

        Label statusTitle = new Label("STATUSY");
        statusTitle.getStyleClass().add("section-title");

        statusEffectsLabel = new Label("Brak aktywnych statusów.");
        statusEffectsLabel.getStyleClass().add("status-effects");
        statusEffectsLabel.setWrapText(true);

        statusEffectsSection.getChildren().addAll(statusTitle, statusEffectsLabel);
    }
    

    public void showBackpackWindow() {
        if (backpackStage == null) {
            backpackStage = new Stage();
            backpackStage.initModality(Modality.WINDOW_MODAL);
            backpackStage.initOwner(mainApp.getPrimaryStage());
            backpackStage.setTitle("Plecak Wiedźmina");
            backpackStage.setResizable(false);
            backpackStage.initStyle(StageStyle.UNDECORATED);
            backpackStage.initModality(Modality.APPLICATION_MODAL);

            VBox root = new VBox(15);
            root.getStyleClass().add("custom-dialog-background");
            root.setPadding(new Insets(20));

            Label title = new Label("🎒 TWOJA PODRĘCZNA SAKWA 🎒");
            title.getStyleClass().add("about-title");

            VBox inventoryViewSection = new VBox(10);

            ScrollPane inventoryScrollPane = new ScrollPane();
            backpackItemsContainer = new VBox(8);
            inventoryScrollPane.setContent(backpackItemsContainer);
            
            inventoryScrollPane.setPrefHeight(550);

            inventoryScrollPane.setFitToWidth(true);
            inventoryViewSection.getChildren().addAll(inventoryScrollPane);

            Button closeButton = new Button("Zamknij");
            closeButton.getStyleClass().add("dialog-button-secondary");
            closeButton.setOnAction(e -> {
                playButtonClickSound();
                backpackStage.close();
            });

            root.getChildren().addAll(title, inventoryViewSection, closeButton);
            Scene backpackScene = new Scene(root, 800, 750);
            backpackScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
            backpackStage.setScene(backpackScene);
        }

        updateBackpackContent();

        backpackStage.setOnShown(e -> {
            Stage primaryStage = mainApp.getPrimaryStage();

            backpackStage.setX(primaryStage.getX() + (mapScrollPane.getWidth() - backpackStage.getWidth()) / 2);
            backpackStage.setY(primaryStage.getY() + 70);
        });

        backpackStage.showAndWait();
    }

    private void updateBackpackContent() {
        if (backpackItemsContainer == null) {
            return;
        }
        backpackItemsContainer.getChildren().clear();

        Player player = controller.getPlayer();

        if (player.getInventory().isEmpty()) {
            Label emptyLabel = new Label("Plecak jest pusty!");
            emptyLabel.getStyleClass().add("inventory");
            backpackItemsContainer.getChildren().add(emptyLabel);
        } else {
            List<Item> currentInventory = new ArrayList<>(player.getInventory());

            for (Item item : currentInventory) {
                HBox itemRow = new HBox(10);
                itemRow.setAlignment(Pos.CENTER_LEFT);

                String icon = getItemIcon(item);
                Label itemLabel = new Label(icon + " " + item.getName());
                itemLabel.getStyleClass().add("inventory");
                itemLabel.setPrefWidth(220);

                HBox buttonsBox = new HBox(5);
                buttonsBox.setAlignment(Pos.CENTER_RIGHT);

                Button infoButton = new Button("Info");
                infoButton.getStyleClass().add("dialog-button-primary");
                infoButton.setOnAction(e -> {
                    playButtonClickSound();
                    showItemDetailsPopup(item, itemLabel);
                });
                buttonsBox.getChildren().add(infoButton);

                if (item instanceof Potion) {
                    Button useButton = new Button("Użyj");
                    useButton.getStyleClass().add("dialog-button-primary");
                    useButton.setOnAction(e -> {
                        playButtonClickSound();
                        player.useItem(item.getName());
                        updateBackpackContent();
                        updateDisplay();
                    });
                    buttonsBox.getChildren().add(useButton);
                } else if (item instanceof Weapon || item instanceof Armor) {
                    Button equipButton = new Button("Załóż");
                    equipButton.getStyleClass().add("dialog-button-primary");
                    equipButton.setOnAction(e -> {
                        playButtonClickSound();
                        player.useItem(item.getName());
                        updateBackpackContent();
                        updateDisplay();
                    });
                    buttonsBox.getChildren().add(equipButton);
                }

                Button dropButton = new Button("Wyrzuć");
                dropButton.getStyleClass().add("dialog-button-secondary");
                dropButton.setOnAction(e -> {
                    playButtonClickSound();
                    showDropItemConfirmation(item, player, backpackStage);
                });

                buttonsBox.getChildren().add(dropButton);

                itemRow.getChildren().addAll(itemLabel, buttonsBox);
                HBox.setHgrow(buttonsBox, Priority.ALWAYS);

                backpackItemsContainer.getChildren().add(itemRow);
            }
        }
    }
    
    public boolean showItemDiscoveryDialog(Item item, Room room) {
        final AtomicBoolean itemWasPickedUp = new AtomicBoolean(false);

        Stage discoveryStage = new Stage();
        discoveryStage.initModality(Modality.APPLICATION_MODAL);
        discoveryStage.initOwner(mainApp.getPrimaryStage());
        discoveryStage.setTitle("Odkryto Przedmiot!");
        discoveryStage.setResizable(false);
        discoveryStage.initStyle(StageStyle.UNDECORATED);
        discoveryStage.initModality(Modality.APPLICATION_MODAL);

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("custom-dialog-background");

        ImageView itemImage;
        String imagePath;
        if (item instanceof Key) {
            imagePath = "/images/item_key.jpg";
        } else {
            imagePath = "/images/item_discovery.jpg";
        }
        itemImage = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath))));
        itemImage.setFitHeight(275);
        itemImage.setPreserveRatio(true);
        

        StackPane imageContainer = new StackPane();
        imageContainer.getChildren().add(itemImage);
        imageContainer.getStyleClass().add("image-frame");
        imageContainer.setPrefWidth(Region.USE_COMPUTED_SIZE);

        VBox textAndButtonsVBox = new VBox(15);
        textAndButtonsVBox.setAlignment(Pos.CENTER);
        textAndButtonsVBox.setPadding(new Insets(0, 20, 30, 20));

        Label localTitle = new Label("🎉 ZNALEZIONO PRZEDMIOT!");
        localTitle.getStyleClass().add("about-title");
        VBox.setMargin(localTitle, new Insets(0, 0, 10, 0));

        String icon = getItemIcon(item);
        Player player = controller.getPlayer();

        Label contentLabel;
        Button pickUpButton = new Button("✅ PODNIEŚ");
        pickUpButton.getStyleClass().add("dialog-button-primary");

        if (player.isInventoryFull()) {
            contentLabel = new Label(
                    "Odnajdujesz: " + icon + " " + item.getName() + "!\n\n" +
                            "Niestety, twój plecak jest pełny (" + player.getInventory().size() + "/" + Player.MAX_INVENTORY_SIZE + ").\n" +
                            "Musisz coś wyrzucić, aby zrobić miejsce."
            );
            pickUpButton.setDisable(true);
        } else {
            contentLabel = new Label(
                    "Odnajdujesz: " + icon + " " + item.getName() + "!\n\n" +
                            "Czy chcesz podnieść ten przedmiot?"
            );
            pickUpButton.setOnAction(e -> {
                playButtonClickSound();
                if (room.getItem() != null) {
                    controller.getPlayer().addItem(item);
                    room.removeItem();
                    itemWasPickedUp.set(true);
                }
                discoveryStage.close();
            });
        }

        contentLabel.getStyleClass().add("about-content");
        contentLabel.setTextAlignment(TextAlignment.CENTER);
        contentLabel.setWrapText(true);

        Button leaveButton = new Button("❌ ZOSTAW");
        leaveButton.getStyleClass().add("dialog-button-secondary");
        leaveButton.setOnAction(e -> {
            playButtonClickSound();
            discoveryStage.close();
        });

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(pickUpButton, leaveButton);
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        textAndButtonsVBox.getChildren().addAll(localTitle, contentLabel, spacer, buttonBox);
        


        HBox mainContentHBox = new HBox(10);
        mainContentHBox.setAlignment(Pos.CENTER);

        imageContainer.getChildren();
        mainContentHBox.getChildren().addAll(imageContainer, textAndButtonsVBox);
        HBox.setHgrow(imageContainer, Priority.SOMETIMES);
        HBox.setHgrow(textAndButtonsVBox, Priority.ALWAYS);

        root.getChildren().addAll(mainContentHBox);

        Scene discoveryScene = new Scene(root, 800, 420);
        discoveryScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
        discoveryStage.setScene(discoveryScene);
        discoveryStage.sizeToScene();

        discoveryStage.setOnShown(e -> {
            Stage primaryStage = mainApp.getPrimaryStage();
            discoveryStage.setX(primaryStage.getX() + (primaryStage.getWidth() - discoveryStage.getWidth()) / 2);
            discoveryStage.setY(primaryStage.getY() + (primaryStage.getHeight() - discoveryStage.getHeight()) / 2);
        });

        discoveryStage.showAndWait();
        return itemWasPickedUp.get();
    }

    public void showTreasureDiscoveryDialog(Item treasureItem, Room treasureRoom, Player player) {
        Stage treasureDiscoveryStage = new Stage();
        treasureDiscoveryStage.initModality(Modality.APPLICATION_MODAL);
        treasureDiscoveryStage.initOwner(mainApp.getPrimaryStage());
        treasureDiscoveryStage.setTitle("Komora Skarbca!");
        treasureDiscoveryStage.setResizable(false);
        treasureDiscoveryStage.initStyle(StageStyle.UNDECORATED);
        treasureDiscoveryStage.initModality(Modality.APPLICATION_MODAL);

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("custom-dialog-background");

        ImageView treasureImage;
        treasureImage = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/chest.jpg"))));
        treasureImage.setFitWidth(350);
        treasureImage.setPreserveRatio(true);
       

        StackPane imageContainer = new StackPane();
        imageContainer.getChildren().add(treasureImage);
        imageContainer.getStyleClass().add("image-frame");
        imageContainer.setPrefWidth(Region.USE_COMPUTED_SIZE);


        VBox textAndButtonsVBox = new VBox(15);
        textAndButtonsVBox.setAlignment(Pos.CENTER);
        textAndButtonsVBox.setPadding(new Insets(0, 0, 0, 0));

        Label localTitle = new Label("💎 TAJEMNICZY SKARBIEC!");
        localTitle.getStyleClass().add("about-title");
        VBox.setMargin(localTitle, new Insets(0, 0, 10, 0));


        Label contentLabel;
        Button openButton;
        Button leaveButton = new Button("❌ OPUŚĆ");
        leaveButton.getStyleClass().add("dialog-button-secondary");
        leaveButton.setOnAction(e -> {
            playButtonClickSound();
            treasureDiscoveryStage.close();
            updateDisplay();
        });
        
        if (player.hasKey()) {
            if (player.isInventoryFull()) {
                contentLabel = new Label(
                        "Masz klucz, ale... Twój plecak jest pełny!\n" +
                                "Wygląda na to, że nawet największy skarb musi poczekać.\n" +
                                "Wróć, gdy zrobisz trochę miejsca w swoim majdaniku.\n" +
                                "(Klucz nie zostanie zużyty)"
                );
                openButton = new Button("🗝 UŻYJ KLUCZA");
                openButton.getStyleClass().add("dialog-button-secondary");
            } else {
                contentLabel = new Label(
                        "A niech mnie drzwi ścisną… Skarbiec! Prawdziwy, zasrany skarbiec!\n" +
                                "Nie beczka z kapustą, nie truchło kozy, nie pułapka z kolcami,\n " +
                                "tylko prawdziwy, ciężki od złota jak sumienie czarodzieja – skarbiec!\n" +
                                "Klucz?! Ty go naprawdę znalazłeś?\n" +
                                "(Klucz zostanie zużyty.)"
                );
                openButton = new Button("🗝 UŻYJ KLUCZA");
                openButton.getStyleClass().add("dialog-button-primary");
                openButton.setOnAction(e -> {
                    playButtonClickSound();
                    player.removeKey();
                    player.addItem(treasureItem);
                    treasureRoom.setItem(null);
                    treasureRoom.setTreasureOpened(true);
                    showTreasureFoundDialog(treasureItem, treasureDiscoveryStage);
                    treasureDiscoveryStage.close();
                    updateDisplay();
                });
            }
        } else {
            contentLabel = new Label(
                    "A niech mnie drzwi ścisną… Skarbiec! Prawdziwy, zasrany skarbiec!\n" +
                            "Nie beczka z kapustą, nie truchło kozy, nie pułapka z kolcami,\n " +
                            "tylko prawdziwy, ciężki od złota jak sumienie czarodzieja – skarbiec!\n" +
                            "No pewnie, jasne. Spróbuj go otworzyć siłą woli, może zadziała.\n" +
                            "Albo kopnij. Złam nogę przy okazji, przynajmniej będzie mniej biegania."
            );
            openButton = new Button("🗝 UŻYJ KLUCZA");
            openButton.getStyleClass().add("dialog-button-secondary");
            openButton.setDisable(true);
        }

        contentLabel.getStyleClass().add("about-content");
        contentLabel.setTextAlignment(TextAlignment.CENTER);
        contentLabel.setWrapText(true);

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(openButton, leaveButton);

        textAndButtonsVBox.getChildren().addAll(localTitle, contentLabel, buttonBox);


        HBox mainContentHBox = new HBox(10);
        mainContentHBox.setAlignment(Pos.CENTER);

        imageContainer.getChildren();
        mainContentHBox.getChildren().addAll(imageContainer, textAndButtonsVBox);
        HBox.setHgrow(textAndButtonsVBox, Priority.ALWAYS);

        root.getChildren().addAll(mainContentHBox);

        Scene discoveryScene = new Scene(root, 900, 450);
        discoveryScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
        
        treasureDiscoveryStage.setScene(discoveryScene);
        treasureDiscoveryStage.sizeToScene();

        treasureDiscoveryStage.setOnShown(e -> {
            Stage primaryStage = mainApp.getPrimaryStage();
            treasureDiscoveryStage.setX(primaryStage.getX() + (primaryStage.getWidth() - treasureDiscoveryStage.getWidth()) / 2);
            treasureDiscoveryStage.setY(primaryStage.getY() + (primaryStage.getHeight() - treasureDiscoveryStage.getHeight()) / 2);
        });

        treasureDiscoveryStage.showAndWait();
    }

    private void showTreasureFoundDialog(Item foundItem, Stage parentStage) {
        Stage treasureFoundStage = new Stage();
        treasureFoundStage.initModality(Modality.APPLICATION_MODAL);
        treasureFoundStage.initOwner(parentStage);
        treasureFoundStage.setTitle("Skarb Znaleziony!");
        treasureFoundStage.setResizable(false);
        treasureFoundStage.initStyle(StageStyle.UNDECORATED);
        treasureFoundStage.initModality(Modality.APPLICATION_MODAL);

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("custom-dialog-background");

        ImageView treasureFoundImage;
       
        treasureFoundImage = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/treasure_found.jpg"))));
        treasureFoundImage.setFitWidth(300);
        treasureFoundImage.setPreserveRatio(true);
       

        StackPane imageContainer = new StackPane();
        imageContainer.getChildren().add(treasureFoundImage);
        imageContainer.getStyleClass().add("image-frame");
        imageContainer.setPrefWidth(Region.USE_COMPUTED_SIZE);


        VBox textAndButtonsVBox = new VBox(15);
        textAndButtonsVBox.setAlignment(Pos.CENTER);
        textAndButtonsVBox.setPadding(new Insets(0, 0, 0, 0));

        Label localTitle = new Label("💰 SKARB ZNALEZIONY!");
        localTitle.getStyleClass().add("about-title");
        VBox.setMargin(localTitle, new Insets(0, 0, 10, 0));

        Label contentLabel = new Label(
                "Otwierasz skrzynię i znajdujesz:\n" +
                        getItemIcon(foundItem) + " " + foundItem.getName() + "!\n\n" +
                        "Zostało to dodane do twojego ekwipunku!"
        );
        contentLabel.getStyleClass().add("about-content");
        contentLabel.setTextAlignment(TextAlignment.CENTER);
        contentLabel.setWrapText(true);

        Button okButton = new Button("OK");
        okButton.getStyleClass().add("dialog-button-primary");
        okButton.setOnAction(e -> {
            playButtonClickSound();
            treasureFoundStage.close();
        });

        textAndButtonsVBox.getChildren().addAll(localTitle, contentLabel, okButton);


        HBox mainContentHBox = new HBox(0);
        mainContentHBox.setAlignment(Pos.CENTER);

        imageContainer.getChildren();
        mainContentHBox.getChildren().addAll(imageContainer, textAndButtonsVBox);
        HBox.setHgrow(textAndButtonsVBox, Priority.ALWAYS);

        root.getChildren().addAll(mainContentHBox);

        Scene scene = new Scene(root, 750, 350);
        try {
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
        } catch (NullPointerException e) {
            System.err.println("Nie można załadować stylów CSS dla dialogu skarbu: " + e.getMessage());
        }
        treasureFoundStage.setScene(scene);
        treasureFoundStage.sizeToScene();

        treasureFoundStage.setOnShown(e -> {
            Stage primaryStage = mainApp.getPrimaryStage();
            treasureFoundStage.setX(primaryStage.getX() + (primaryStage.getWidth() - treasureFoundStage.getWidth()) / 2);
            treasureFoundStage.setY(primaryStage.getY() + (primaryStage.getHeight() - treasureFoundStage.getHeight()) / 2);
        });

        treasureFoundStage.showAndWait();
    }


    private void showDropItemConfirmation(Item item, Player player, Stage backpackOwnerStage) {
        Stage confirmDropStage = new Stage();
        confirmDropStage.initModality(Modality.APPLICATION_MODAL);
        confirmDropStage.initOwner(backpackOwnerStage);
        confirmDropStage.setTitle("Wyrzuć Przedmiot?");
        confirmDropStage.setResizable(false);
        confirmDropStage.initStyle(StageStyle.UNDECORATED);
        confirmDropStage.initModality(Modality.APPLICATION_MODAL);

        VBox root = new VBox(15);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("custom-dialog-background");

        Text title = new Text("NA PEWNO WYRZUCIĆ?");
        title.getStyleClass().add("about-title");

        Label contentLabel = new Label(
                "Nie uczyła cię mama, że nie należy wyrzucać rzeczy bez zastanowienia?\n" +
                        "Przedmiot zostanie utracony bezpowrotnie!"
        );
        contentLabel.getStyleClass().add("about-content");
        contentLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        contentLabel.setWrapText(true);

        Button yesButton = new Button("TAK, WYRZUĆ");
        yesButton.getStyleClass().add("dialog-button-primary");
        yesButton.setOnAction(e -> {
            playButtonClickSound();
            player.getInventory().remove(item);
            System.out.println("Wyrzucono przedmiot: " + item.getName());
            confirmDropStage.close();
            updateBackpackContent();
            updateDisplay();
        });

        Button noButton = new Button("ANULUJ");
        noButton.getStyleClass().add("dialog-button-secondary");
        noButton.setOnAction(e -> {
            playButtonClickSound();
            confirmDropStage.close();
        });

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(yesButton, noButton);

        root.getChildren().addAll(title, contentLabel, buttonBox);

        Scene confirmScene = new Scene(root);
        confirmScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
       
        confirmDropStage.setScene(confirmScene);
        confirmDropStage.sizeToScene();

        confirmDropStage.setOnShown(e -> {
            Stage primaryStage = mainApp.getPrimaryStage();
            confirmDropStage.setX(primaryStage.getX() + (primaryStage.getWidth() - confirmDropStage.getWidth()) / 2);
            confirmDropStage.setY(primaryStage.getY() + (primaryStage.getHeight() - confirmDropStage.getHeight()) / 2);
        });

        confirmDropStage.showAndWait();
    }

    public void showLevelUpDialog() {
        Stage levelUpStage = new Stage();
        levelUpStage.initModality(Modality.APPLICATION_MODAL);
        levelUpStage.initOwner(mainApp.getPrimaryStage());
        levelUpStage.setTitle("Awans na Poziom!");
        levelUpStage.setResizable(false);
        levelUpStage.initStyle(StageStyle.UNDECORATED);
        levelUpStage.initModality(Modality.APPLICATION_MODAL);

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("custom-dialog-background");

        ImageView levelUpImage;
      
        levelUpImage = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/level_up.png"))));
        levelUpImage.setFitWidth(300);
        levelUpImage.setPreserveRatio(true);
      

        StackPane imageContainer = new StackPane();
        imageContainer.getChildren().add(levelUpImage);
        imageContainer.getStyleClass().add("image-frame");
        imageContainer.setPrefWidth(Region.USE_COMPUTED_SIZE);


        VBox textAndButtonsVBox = new VBox(15);
        textAndButtonsVBox.setAlignment(Pos.CENTER);
        textAndButtonsVBox.setPadding(new Insets(0, 0, 0, 0));

        Label localTitle = new Label("✨ LVL UP!");
        localTitle.getStyleClass().add("about-title");
        VBox.setMargin(localTitle, new Insets(0, 0, 10, 0));

        Player player = controller.getPlayer();
        String contentText = String.format(
                "Cholera, %s! Nie dość, że wciąż żyjesz, to jeszcze awansowałeś na POZIOM %d!\n" +
                        "Wszystkie statystyki skaczą w górę – siła, obrona, nawet charyzma! Ale spokojnie,\n" +
                        "twój kumpel w spodniach pewnie wciąż ma kryzys wieku średniego. Nie da się mieć wszystkiego.\n" +
                        "No, teraz to już w ogóle nikt nie będzie się śmiał z twojej zbroi, choć z reszty… to inna spraw.",
                playerName, player.getLevel()
        );

        Label contentLabel = new Label(contentText);
        contentLabel.getStyleClass().add("about-content");
        contentLabel.setTextAlignment(TextAlignment.CENTER);
        contentLabel.setWrapText(true);

        Button okButton = new Button("DOSKONALE!");
        okButton.getStyleClass().add("dialog-button-primary");
        okButton.setOnAction(e -> {
            playButtonClickSound();
            levelUpStage.close();
            updateDisplay();
        });

        textAndButtonsVBox.getChildren().addAll(localTitle, contentLabel, okButton);


        HBox mainContentHBox = new HBox(0);
        mainContentHBox.setAlignment(Pos.CENTER);

        imageContainer.getChildren();
        mainContentHBox.getChildren().addAll(imageContainer, textAndButtonsVBox);
        HBox.setHgrow(textAndButtonsVBox, Priority.ALWAYS);

        root.getChildren().addAll(mainContentHBox);

        Scene scene = new Scene(root, 750, 500);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
      
        levelUpStage.setScene(scene);
        levelUpStage.sizeToScene();

        levelUpStage.setOnShown(e -> {
            Stage primaryStage = mainApp.getPrimaryStage();
            levelUpStage.setX(primaryStage.getX() + (primaryStage.getWidth() - levelUpStage.getWidth()) / 2);
            levelUpStage.setY(primaryStage.getY() + (primaryStage.getHeight() - levelUpStage.getHeight()) / 2);
        });

        levelUpStage.showAndWait();
    }


    private void showItemDetailsPopup(Item item, Label itemLabel) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(mainApp.getPrimaryStage());
        popupStage.setResizable(false);
        popupStage.setTitle("Szczegóły Przedmiotu");
        popupStage.initStyle(StageStyle.UNDECORATED);
        popupStage.initModality(Modality.APPLICATION_MODAL);

        VBox popupLayout = new VBox(10);
        popupLayout.setPadding(new Insets(15));
        popupLayout.getStyleClass().add("custom-dialog-background");

        Label itemNameLabel = new Label(getItemIcon(item) + " " + item.getName().toUpperCase());
        itemNameLabel.getStyleClass().add("dialog-label-bold");

        String description;
        if (item instanceof Weapon) {
            Weapon weapon = (Weapon) item;
            description = String.format("Typ: %s\n" + weapon.getDescription(),
                    weapon.getWeaponType().toString());
        } else if (item instanceof Armor) {
            Armor armor = (Armor) item;
            description = String.format(armor.getDescription(), armor.getArmorType().toString());
        } else if (item instanceof Potion) {
            Potion potion = (Potion) item;
            description = String.format(potion.getDescription());
        }else if (item instanceof Key) {
            Key key = (Key) item;
            description = String.format(key.getDescription());
        } else {
            description = "To tajemniczy przedmiot...";
        }

        Label itemDescriptionLabel = new Label(description);
        itemDescriptionLabel.getStyleClass().add("dialog-label");
        itemDescriptionLabel.setWrapText(true);
        itemDescriptionLabel.setTextAlignment(TextAlignment.CENTER);
        itemDescriptionLabel.setMinWidth(250);
        itemDescriptionLabel.setMaxWidth(300);

        Button closeButton = new Button("OK");
        closeButton.getStyleClass().add("dialog-button-primary");
        closeButton.setOnAction(e -> {
            playButtonClickSound();
            popupStage.close();
        });

        popupLayout.getChildren().addAll(itemNameLabel, itemDescriptionLabel, closeButton);

        Scene popupScene = new Scene(popupLayout);
        popupScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
       
        popupStage.setScene(popupScene);

        double screenX = itemLabel.localToScreen(itemLabel.getBoundsInLocal()).getMinX();
        double screenY = itemLabel.localToScreen(itemLabel.getBoundsInLocal()).getMinY();
        popupStage.setX(screenX + itemLabel.getWidth() + 10);
        popupStage.setY(screenY);

        popupStage.show();
    }

    private String getItemIcon(Item item) {
        if (item instanceof Key) {
            return "🔑";
        } else if (item instanceof Potion) {
            return "💊";
        } else if (item instanceof Weapon) {
            return "⚔";
        } else if (item instanceof Armor) {
            return "🛡";
        }
        return "🎁";
    }

    private void showMenuWindow() {
        Stage menuStage = new Stage();
        menuStage.initModality(Modality.WINDOW_MODAL);
        menuStage.initOwner(mainApp.getPrimaryStage());
        menuStage.setTitle("Menu Gry");
        menuStage.initStyle(StageStyle.UNDECORATED);
        menuStage.initModality(Modality.APPLICATION_MODAL);

        VBox menuLayout = new VBox(15);
        menuLayout.setPadding(new Insets(20));
        menuLayout.setAlignment(Pos.CENTER);
        menuLayout.getStyleClass().add("dialog-dark-background");

        Button helpBtn = new Button("❓ Pomoc");
        helpBtn.getStyleClass().add("dialog-button-primary");
        helpBtn.setMaxWidth(Double.MAX_VALUE);
        helpBtn.setOnAction(e -> {
            playButtonClickSound();
            showHelp();
        });

        Button newGameBtn = new Button("🔄 Nowa Gra");
        newGameBtn.getStyleClass().add("dialog-button-primary");
        newGameBtn.setMaxWidth(Double.MAX_VALUE);
        newGameBtn.setOnAction(e -> {
            playButtonClickSound();
            confirmNewGame(menuStage);
        });

        Button exitToMainMenuBtn = new Button("🚪 Wyjdź do Menu Głównego");
        exitToMainMenuBtn.getStyleClass().add("dialog-button-primary");
        exitToMainMenuBtn.setMaxWidth(Double.MAX_VALUE);
        exitToMainMenuBtn.setOnAction(e -> {
            playButtonClickSound();
            returnToMenu(menuStage);
        });

        Button closeMenuBtn = new Button("❌ Zamknij Menu");
        closeMenuBtn.getStyleClass().add("dialog-button-primary");
        closeMenuBtn.setMaxWidth(Double.MAX_VALUE);
        closeMenuBtn.setOnAction(e -> {
            playButtonClickSound();
            menuStage.close();
        });

        menuLayout.getChildren().addAll(helpBtn, newGameBtn, exitToMainMenuBtn, closeMenuBtn);

        Scene menuDialogScene = new Scene(menuLayout, 350, 300);
        try {
            menuDialogScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
        } catch (NullPointerException e) {
            System.err.println("Nie można załadować stylów CSS dla okna menu: " + e.getMessage());
        }
        menuStage.setScene(menuDialogScene);

        menuStage.setOnShown(e -> {
            Stage primaryStage = mainApp.getPrimaryStage();
            menuStage.setX(primaryStage.getX() + (primaryStage.getWidth() - menuStage.getWidth()) / 2);
            menuStage.setY(primaryStage.getY() + (primaryStage.getHeight() - menuStage.getHeight()) / 2);
        });

        menuStage.showAndWait();
    }

    private void confirmNewGame(Stage menuStageToClose) {
        Stage confirmNewGameStage = new Stage();
        confirmNewGameStage.initModality(Modality.APPLICATION_MODAL);
        confirmNewGameStage.initOwner(menuStageToClose);
        confirmNewGameStage.setTitle("Nowa Gra?");
        confirmNewGameStage.setResizable(false);
        confirmNewGameStage.initStyle(StageStyle.UNDECORATED);
        confirmNewGameStage.initModality(Modality.APPLICATION_MODAL);

        VBox root = new VBox(15);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("custom-dialog-background");

        Text title = new Text("NOWA GRA?!?!?");
        title.getStyleClass().add("about-title");

        Label contentLabel = new Label(
                "Geralt pewnie właśnie wzdycha, Yennefer przewraca oczami,\n" +
                        "a Jaskier pisze balladę o twojej porażce…\n" +
                        "Ale dobra, twój cyrk, twoje utopce."
        );
        contentLabel.getStyleClass().add("about-content");
        contentLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        contentLabel.setWrapText(true);

        Button yesButton = new Button("TAK, NOWA GRA");
        yesButton.getStyleClass().add("dialog-button-primary");
        yesButton.setOnAction(e -> {
            playButtonClickSound();
            confirmNewGameStage.close();
            menuStageToClose.close();
            mainApp.startGame(playerName, false);
        });

        Button noButton = new Button("ANULUJ");
        noButton.getStyleClass().add("dialog-button-secondary");
        noButton.setOnAction(e -> {
            playButtonClickSound();
            confirmNewGameStage.close();
        });

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(yesButton, noButton);

        root.getChildren().addAll(title, contentLabel, buttonBox);

        Scene confirmScene = new Scene(root);
        confirmScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
       
        confirmNewGameStage.setScene(confirmScene);
        confirmNewGameStage.sizeToScene();

        confirmNewGameStage.setOnShown(e -> {
            Stage primaryStage = mainApp.getPrimaryStage();
            confirmNewGameStage.setX(primaryStage.getX() + (primaryStage.getWidth() - confirmNewGameStage.getWidth()) / 2);
            confirmNewGameStage.setY(primaryStage.getY() + (primaryStage.getHeight() - confirmNewGameStage.getHeight()) / 2);
        });

        confirmNewGameStage.showAndWait();
    }


    private void showLegendDialog() {
        Stage legendStage = new Stage();
        legendStage.initModality(Modality.WINDOW_MODAL);
        legendStage.initOwner(mainApp.getPrimaryStage());
        legendStage.setTitle("Legenda Mapy");
        legendStage.setResizable(false);
        legendStage.initStyle(StageStyle.UNDECORATED);
        legendStage.initModality(Modality.APPLICATION_MODAL);

        VBox root = new VBox(8);
        root.getStyleClass().add("custom-dialog-background");
        root.setPadding(new Insets(15));
        root.setAlignment(Pos.TOP_CENTER);

        Label legendTitle = new Label("LEGENDA MAPY");
        legendTitle.getStyleClass().add("section-title");
        HBox titleContainer = new HBox(legendTitle);
        titleContainer.setAlignment(Pos.CENTER);
        VBox.setMargin(titleContainer, new Insets(0, 0, 10, 0));

        VBox column1 = new VBox(8);
        column1.setAlignment(Pos.CENTER_LEFT);

        VBox column2 = new VBox(8);
        column2.setAlignment(Pos.CENTER_LEFT);

        List<HBox> legendItems = new ArrayList<>();
        legendItems.add(createLegendItem(Color.rgb(40, 40, 40), "Smoczysko", "🐉", Color.GREEN));
        legendItems.add(createLegendItem(Color.rgb(40, 40, 40), "Potwór", "💀", Color.WHITE));
        legendItems.add(createLegendItem(Color.rgb(40, 40, 40), "Niespodzianka", "❓", Color.RED));
        legendItems.add(createLegendItem(Color.GOLD, "Gracz (Normalny)", null, Color.BLACK));
        legendItems.add(createLegendItem(Color.GREEN, "Gracz (Zatruty)", null, Color.BLACK));
        legendItems.add(createLegendItem(Color.RED, "Gracz (Wzmocniony)", null, Color.BLACK));
        legendItems.add(createLegendItem(Color.rgb(70, 130, 180), "Przedmiot", null, null));
        legendItems.add(createLegendItem(Color.rgb(87, 12, 89), "Skarb", null, null));
        legendItems.add(createLegendItem(Color.rgb(50, 150, 50), "Start", null, null));
        legendItems.add(createLegendItem(Color.rgb(80, 80, 80), "Pusty Pokój", null, null));
        legendItems.add(createLegendItem(Color.rgb(20, 20, 20), "Ściana", null, null));
        legendItems.add(createLegendItem(Color.rgb(40, 40, 40), "Nieodwiedzone", null, null));

        int midpoint = (int) Math.ceil((double) legendItems.size() / 2.0);

        for (int i = 0; i < legendItems.size(); i++) {
            if (i < midpoint) {
                column1.getChildren().add(legendItems.get(i));
            } else {
                column2.getChildren().add(legendItems.get(i));
            }
        }

        HBox columnsContainer = new HBox(20);
        columnsContainer.setAlignment(Pos.TOP_CENTER);
        columnsContainer.getChildren().addAll(column1, column2);

        Button okButton = new Button("OK");
        okButton.getStyleClass().add("dialog-button-primary");
        okButton.setOnAction(e -> {
            playButtonClickSound();
            legendStage.close();
        });

        root.getChildren().addAll(titleContainer, columnsContainer, okButton);

        Scene legendScene = new Scene(root, 480, 500);
        legendScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
        legendStage.setScene(legendScene);

        legendStage.setOnShown(e -> {
            Stage primaryStage = mainApp.getPrimaryStage();
            legendStage.setX(primaryStage.getX() + (primaryStage.getWidth() - legendStage.getWidth()) / 2);
            legendStage.setY(primaryStage.getY() + (primaryStage.getHeight() - legendStage.getHeight()) / 2);
        });

        legendStage.showAndWait();
    }


    private void returnToMenu(Stage menuStageToClose) {
        Stage confirmStage = new Stage();
        confirmStage.initModality(Modality.WINDOW_MODAL);
        confirmStage.initOwner(mainApp.getPrimaryStage());
        confirmStage.setTitle("Potwierdzenie Wyjścia");
        confirmStage.setResizable(false);
        confirmStage.initStyle(StageStyle.UNDECORATED);
        confirmStage.initModality(Modality.APPLICATION_MODAL);

        VBox root = new VBox(15);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("custom-dialog-background");

        Text title = new Text(" UCIEKASZ?");
        title.getStyleClass().add("about-title");

        Label contentLabel = new Label("HA! Wiedziałem, że cienki z ciebie bolek szkarado, wracaj do mamusi!\n");
        contentLabel.getStyleClass().add("about-content");
        contentLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        contentLabel.setWrapText(true);

        Button yesButton = new Button("TAK, WRÓĆ DO MENU");
        yesButton.getStyleClass().add("dialog-button-primary");
        yesButton.setOnAction(e -> {
            playButtonClickSound();
            confirmStage.close();
            menuStageToClose.close();

            mainApp.showMainMenu();
        });

        Button noButton = new Button("ANULUJ");
        noButton.getStyleClass().add("dialog-button-secondary");
        noButton.setOnAction(e -> {
            playButtonClickSound();
            confirmStage.close();
        });

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(yesButton, noButton);

        root.getChildren().addAll(title, contentLabel, buttonBox);

        Scene confirmScene = new Scene(root);
        confirmScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
        confirmStage.setScene(confirmScene);
        confirmStage.sizeToScene();

        confirmStage.setOnShown(e -> {
            Stage primaryStage = mainApp.getPrimaryStage();
            confirmStage.setX(primaryStage.getX() + (primaryStage.getWidth() - confirmStage.getWidth()) / 2);
            confirmStage.setY(primaryStage.getY() + (primaryStage.getHeight() - confirmStage.getHeight()) / 2);
        });

        confirmStage.showAndWait();
    }
    
    public void showCombatChoiceDialog(Enemy enemy, boolean canFlee) {
        Player player = controller.getPlayer();

        if (combatResultStage != null && combatResultStage.isShowing()) {
            combatResultStage.close();
        }
        if (combatChoiceStage != null && combatChoiceStage.isShowing()) {
            combatChoiceStage.close();
        }

        Image enemySprite = new Image(Objects.requireNonNull(getClass().getResourceAsStream(enemy.getImagePath())));
        double aspectRatio = enemySprite.getWidth() / enemySprite.getHeight();
        double targetImageHeight = 400;
        double targetImageWidth = targetImageHeight * aspectRatio;
        double framePadding = 5.0;
        double finalFrameWidth = targetImageWidth + (2 * framePadding);
        double finalFrameHeight = targetImageHeight + (2 * framePadding);

        combatChoiceStage = new Stage();
        combatChoiceStage.initModality(Modality.APPLICATION_MODAL);
        combatChoiceStage.initOwner(mainApp.getPrimaryStage());
        combatChoiceStage.setResizable(false);
        combatChoiceStage.setTitle("Walka z " + enemy.getName().toUpperCase() + "!");
        combatChoiceStage.setOnCloseRequest(Event::consume);
        combatChoiceStage.initStyle(StageStyle.UNDECORATED);
        combatChoiceStage.initModality(Modality.APPLICATION_MODAL);

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("custom-dialog-background");

        ImageView enemyImage = new ImageView(enemySprite);
        enemyImage.setFitHeight(targetImageHeight);
        enemyImage.setPreserveRatio(true);

        StackPane imageContainer = new StackPane();
        imageContainer.getChildren().add(enemyImage);
        imageContainer.getStyleClass().add("image-frame");
        imageContainer.setPrefSize(finalFrameWidth, finalFrameHeight);
        imageContainer.setMinSize(finalFrameWidth, finalFrameHeight);
        imageContainer.setMaxSize(finalFrameWidth, finalFrameHeight);

        VBox textAndButtonsVBox = new VBox(15);
        textAndButtonsVBox.setAlignment(Pos.CENTER);
        textAndButtonsVBox.setPadding(new Insets(0, 0, 0, 0));

        Label localTitle = new Label("⚔ Natrafiasz na " + enemy.getName().toUpperCase() + "!");
        localTitle.getStyleClass().add("about-title");
        VBox.setMargin(localTitle, new Insets(0, 0, 10, 0));

        TextFlow contentTextFlow = new TextFlow();
        contentTextFlow.setTextAlignment(TextAlignment.CENTER);
        contentTextFlow.getStyleClass().add("about-content-flow");

        Text combatMessage = new Text(
                String.format("PRZECIWNIK: \nŻYCIE: %d\nOBRAŻENIA: %d\n\n",
                        enemy.getHealth(), enemy.getDamage())
        );
        combatMessage.getStyleClass().add("game-over-text");
        Text combatMessagePartTwo = new Text(
                String.format("TWOJE STATYSTYKI:\n ŻYCIE: %d\nOBRAŻENIA: %d\nPANCERZ: %d\n\n",
                        player.getHealth(), player.getDamage(), player.getArmor())
        );
        combatMessagePartTwo.getStyleClass().add("about-content");
        Text combatMessageTwo = new Text(enemy.getEncounterText());
        combatMessageTwo.getStyleClass().add("about-content");
        contentTextFlow.getChildren().addAll(combatMessageTwo, combatMessage, combatMessagePartTwo);

        Button attackButton = new Button("⚔ WALCZ");
        attackButton.getStyleClass().add("dialog-button-primary");
        attackButton.setMaxWidth(Double.MAX_VALUE);
        attackButton.setOnAction(e -> {
            playButtonClickSound();
            combatChoiceStage.close();
            controller.attack();
            if (!player.isAlive()){
                showGameOver();
            }
        });

    
        Button fleeButton;
        if (canFlee) {
            fleeButton = new Button("🏃 UCIEKAJ (Tracisz 20 HP)");
            fleeButton.getStyleClass().add("dialog-button-secondary");
            fleeButton.setOnAction(e -> {
                playButtonClickSound();
                combatChoiceStage.close();
                controller.flee();
            });
            if (player.getHealth() <= 20) {
                fleeButton.setDisable(true);
            }
        } else {
            fleeButton = new Button("❌ NIE MA UCIECZKI");
            fleeButton.getStyleClass().add("dialog-button-secondary");
            fleeButton.setDisable(true);
        }
        fleeButton.setMaxWidth(Double.MAX_VALUE);

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(attackButton, fleeButton);

        textAndButtonsVBox.getChildren().addAll(localTitle, contentTextFlow, buttonBox);

        HBox mainContentHBox = new HBox(10);
        mainContentHBox.setAlignment(Pos.CENTER);
        mainContentHBox.getChildren().addAll(imageContainer, textAndButtonsVBox);
        HBox.setHgrow(textAndButtonsVBox, Priority.ALWAYS);

        root.getChildren().addAll(mainContentHBox);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());

        combatChoiceStage.setScene(scene);

        combatChoiceStage.setOnShown(e -> {
            Stage primaryStage = mainApp.getPrimaryStage();
            combatChoiceStage.setX(primaryStage.getX() + (primaryStage.getWidth() - combatChoiceStage.getWidth()) / 2);
            combatChoiceStage.setY(primaryStage.getY() + (primaryStage.getHeight() - combatChoiceStage.getHeight()) / 2);
        });

        combatChoiceStage.showAndWait();
    }

    public void showCombatResultDialog(Enemy enemy, boolean playerWon, int expGained, boolean levelUp) {
        Player player = controller.getPlayer();
        combatResultStage = new Stage();
        combatResultStage.initModality(Modality.APPLICATION_MODAL);
        combatResultStage.initOwner(mainApp.getPrimaryStage());
        combatResultStage.setTitle("Wynik walki");
        combatResultStage.setResizable(false);
        combatResultStage.setOnCloseRequest(Event::consume);
        combatResultStage.initStyle(StageStyle.UNDECORATED);
        combatResultStage.initModality(Modality.APPLICATION_MODAL);

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("custom-dialog-background");

        ImageView resultImage;
        String imagePath;

        if (playerWon) {
            imagePath = "/images/victory.png";
        } else {
            imagePath = "/images/running.png";
        }

        resultImage = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath))));
        resultImage.setFitHeight(400);
        resultImage.setPreserveRatio(true);

        StackPane imageContainer = new StackPane();
        imageContainer.getChildren().add(resultImage);
        imageContainer.getStyleClass().add("image-frame");
        imageContainer.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        imageContainer.setPrefWidth(Region.USE_COMPUTED_SIZE);

        VBox textAndButtonsVBox = new VBox(15);
        textAndButtonsVBox.setAlignment(Pos.CENTER);
        textAndButtonsVBox.setPadding(new Insets(0, 0, 0, 0));

        Label localTitle;
        String contentText;

        if (playerWon) {
            localTitle = new Label("🎉 ZWYCIĘSTWO!");
            localTitle.getStyleClass().add("about-title");
            contentText = String.format(enemy.getDeathText(), expGained
            );
            if (player.isPoisoned()) {
                contentText += "Ale uwaga, jesteś zatruty!\n Musisz znaleźć antidotum, bo inaczej będzie kiepsko.\n";
            }
        } else {
            localTitle = new Label("🏃 UCIECZKA!");
            localTitle.getStyleClass().add("about-title");
            contentText = "Patrzcie go! Takiego HOJROKA zgrywał, a teraz ucieka jak tchórz!\n" +
                    "Stary VESEMIR pewnie się w grobie przewraca!\n" +
                    "Weź ty się przybłędo zastanów czy to na pewno robota dla ciebie!\n\n" +
                    "ŻYCIE: - 20 HP!";
        }
        VBox.setMargin(localTitle, new Insets(0, 0, 10, 0));

        TextFlow contentTextFlow = new TextFlow();
        contentTextFlow.setTextAlignment(TextAlignment.CENTER);
        contentTextFlow.getStyleClass().add("about-content-flow");

        Text resultMessage = new Text(contentText);
        resultMessage.getStyleClass().add("about-content");
        contentTextFlow.getChildren().add(resultMessage);

        Button okButton = new Button("OK");
        okButton.getStyleClass().add("dialog-button-primary");
        okButton.setOnAction(e -> {
            playButtonClickSound();
            combatResultStage.close();
            if (levelUp) {
                Platform.runLater(this::showLevelUpDialog);
            }
            updateDisplay();
        });

        if (!playerWon) {
            VBox.setMargin(okButton, new Insets(20, 0, 0, 0));
        }


        textAndButtonsVBox.getChildren().addAll(localTitle, contentTextFlow, okButton);

        HBox mainContentHBox = new HBox(10);
        mainContentHBox.setAlignment(Pos.CENTER);

        imageContainer.getChildren();
        mainContentHBox.getChildren().addAll(imageContainer, textAndButtonsVBox);
        HBox.setHgrow(imageContainer, Priority.SOMETIMES);
        HBox.setHgrow(textAndButtonsVBox, Priority.ALWAYS);
        root.getChildren().addAll(mainContentHBox);

        Scene scene = new Scene(root, 800, 500);

        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
        combatResultStage.setScene(scene);

        combatResultStage.setOnShown(e -> {
            Stage primaryStage = mainApp.getPrimaryStage();
            combatResultStage.setX(primaryStage.getX() + (primaryStage.getWidth() - combatResultStage.getWidth()) / 2);
            combatResultStage.setY(primaryStage.getY() + (primaryStage.getHeight() - combatResultStage.getHeight()) / 2);
        });

        combatResultStage.showAndWait();

    }


    public void setupKeyHandlers() {
        gameLayout.setOnKeyPressed(this::handleKeyPress);
        gameLayout.setFocusTraversable(true);
        gameLayout.requestFocus();
    }

    private void handleKeyPress(KeyEvent event) {
        if (controller == null) return;

        if ((combatChoiceStage != null && combatChoiceStage.isShowing()) ||
                (combatResultStage != null && combatResultStage.isShowing())) {
            return;
        }

        switch (event.getCode()) {
            case W:
            case UP:
                controller.movePlayer(0, -1);
                break;
            case S:
            case DOWN:
                controller.movePlayer(0, 1);
                break;
            case A:
            case LEFT:
                controller.movePlayer(-1, 0);
                break;
            case D:
            case RIGHT:
                controller.movePlayer(1, 0);
                break;
        }
    }

    public void updateDisplay() {
        if (controller == null) {
            return;
        }
        updateMap();
        updatePlayerInfo();
        updateStatusEffects();
        Platform.runLater(() -> {
            if (playerStatsLabel != null) {
                playerStatsLabel.setText(playerStatsLabel.getText());
            }
        });
    }

    private void updatePlayerInfo() {
        if (playerStatsLabel == null || controller == null || controller.getPlayer() == null) return;
        Player player = controller.getPlayer();

        String weaponInfo;
        if (player.getEquippedWeapon() != null) {
            weaponInfo = String.format("%s (+%d obrażeń)",
                    player.getEquippedWeapon().getName(),
                    player.getEquippedWeapon().getDamageBonus());
        } else {
            weaponInfo = "Brak";
        }

        String armorInfo;
        if (player.getEquippedArmor() != null) {
            armorInfo = String.format("%s (+%d pancerza)",
                    player.getEquippedArmor().getName(),
                    player.getEquippedArmor().getArmorValue());
        } else {
            armorInfo = "Brak";
        }

        String stats = String.format(
                "POZIOM: %d\n" +
                        "ŻYCIE: %d/%d\n" +
                        "DOŚWIADCZENIE: %d/%d\n" +
                        "OBRAŻENIA: %d\n" +
                        "PANCERZ: %d\n" +
                        "BROŃ: %s\n" +
                        "ZBROJA: %s",
                player.getLevel(),
                player.getHealth(),
                player.getMaxHealth(),
                player.getExperience(),
                player.getLvlUpThreshold(),
                player.getDamage(),
                player.getArmor(),
                weaponInfo,
                armorInfo
        );
        playerStatsLabel.setText(stats);

        if (inventoryLabel == null) return;
        StringBuilder inventoryText = new StringBuilder();
        if (player.getInventory().isEmpty()) {
            inventoryText.append("🎒 Ależ wieje tu pustkami\n\n");
            inventoryText.append("💡 Eksploruj lochy,\n");
            inventoryText.append("aby znaleźć mikstury\n");
            inventoryText.append("i inne skarby!");
        } else {
            java.util.Map<String, Integer> itemCounts = new HashMap<>();
            for (Item item : player.getInventory()) {
                itemCounts.put(item.getName(), itemCounts.getOrDefault(item.getName(), 0) + 1);
            }
            for (java.util.Map.Entry<String, Integer> entry : itemCounts.entrySet()) {
                Item itemForIcon = player.getInventory().stream()
                        .filter(item -> item.getName().equals(entry.getKey()))
                        .findFirst()
                        .orElse(null);

                String icon = itemForIcon != null ? getItemIcon(itemForIcon) : "🎁";
                inventoryText.append(icon).append(" ").append(entry.getKey());
                if (entry.getValue() > 1) {
                    inventoryText.append(" (").append(entry.getValue()).append(")");
                }
                inventoryText.append("\n");
            }
        }
        inventoryLabel.setText(inventoryText.toString().trim());
    }

    private void updateStatusEffects() {
        if (statusEffectsLabel == null || controller == null || controller.getPlayer() == null) {
            return;
        }

        Player player = controller.getPlayer();
        StringBuilder statusText = new StringBuilder();
        boolean hasAnyStatus = false;
        
        if (player.isPoisoned()) {
            statusText.append("ZATRUCIE\n");
            statusText.append("Tracisz HP co turę\n");
            statusText.append("Pozostało: ").append(player.getPoisonTurnsLeft()).append(" tur\n\n");
            hasAnyStatus = true;
        }

        if (player.hasStrengthBonus()) {
            statusText.append("WZMOCNIENIE\n");
            statusText.append("Zwiększone obrażenia\n");
            statusText.append("Pozostało: ").append(player.getStrengthBonusTurnsLeft()).append(" tur\n\n");
            hasAnyStatus = true;
        }

        if (hasAnyStatus) {
            statusEffectsLabel.setText(statusText.toString().trim());
        } else {
            statusEffectsLabel.setText("Brak aktywnych statusów.");
        }
    }
    
    private void showEndGameDialog(String title, String header, String content,
                                   String imagePath, String buttonText, String buttonText2,
                                   double dialogWidth, double dialogHeight) {
        Stage endGameStage = new Stage();
        endGameStage.initModality(Modality.APPLICATION_MODAL);
        endGameStage.initOwner(mainApp.getPrimaryStage());
        endGameStage.setTitle(title);
        endGameStage.setResizable(false);
        endGameStage.setOnCloseRequest(Event::consume);
        endGameStage.initStyle(StageStyle.UNDECORATED);
        endGameStage.initModality(Modality.APPLICATION_MODAL);

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("custom-dialog-background");

        ImageView dialogImage;
       
        dialogImage = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath))));
        dialogImage.setFitHeight(400);
        dialogImage.setPreserveRatio(true);
       

        StackPane imageContainer = new StackPane();
        imageContainer.getChildren().add(dialogImage);
        imageContainer.getStyleClass().add("image-frame-red");
        imageContainer.setPrefWidth(Region.USE_COMPUTED_SIZE);

        VBox textAndButtonsVBox = new VBox(15);
        textAndButtonsVBox.setAlignment(Pos.CENTER);
        textAndButtonsVBox.setPadding(new Insets(0, 0, 0, 0));

        Label localTitle = new Label(header);
        localTitle.getStyleClass().add("game-over-text-bold");
        VBox.setMargin(localTitle, new Insets(0, 0, 10, 0));

        TextFlow contentTextFlow = new TextFlow();
        contentTextFlow.setTextAlignment(TextAlignment.CENTER);
        contentTextFlow.getStyleClass().add("about-content-flow");

        Text contentText = new Text(content);
        contentText.getStyleClass().add("game-over-text-bold");
        contentTextFlow.getChildren().add(contentText);

        Button actionButton = new Button(buttonText);
        actionButton.getStyleClass().add("dialog-button-primary");
        actionButton.setOnAction(e -> {
            playButtonClickSound();
            endGameStage.close();
            mainApp.startGame(playerName, false);
        });

        Button leaveButton = new Button(buttonText2);
        leaveButton.getStyleClass().add("dialog-button-secondary");
        leaveButton.setOnAction(e -> {
            playButtonClickSound();
            endGameStage.close();
            mainApp.showMainMenu();
        });
        
        HBox buttonContainer = new HBox(20);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.getChildren().addAll(actionButton, leaveButton);
        VBox.setMargin(buttonContainer, new Insets(30, 0, 0, 0));
        
        textAndButtonsVBox.getChildren().addAll(localTitle, contentTextFlow, buttonContainer);

        HBox mainContentHBox = new HBox(10);
        mainContentHBox.setAlignment(Pos.CENTER);

        imageContainer.getChildren();
        mainContentHBox.getChildren().addAll(imageContainer, textAndButtonsVBox);
        HBox.setHgrow(textAndButtonsVBox, Priority.ALWAYS);
        root.getChildren().addAll(mainContentHBox);

        Scene scene = new Scene(root, dialogWidth, dialogHeight);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
        
        endGameStage.setScene(scene);
        endGameStage.sizeToScene();

        endGameStage.setOnShown(e -> {
            Stage primaryStage = mainApp.getPrimaryStage();
            endGameStage.setX(primaryStage.getX() + (primaryStage.getWidth() - endGameStage.getWidth()) / 2);
            endGameStage.setY(primaryStage.getY() + (primaryStage.getHeight() - endGameStage.getHeight()) / 2);
        });

        endGameStage.showAndWait();
    }



    public void showGameOver() {
        if (combatChoiceStage != null && combatChoiceStage.isShowing()) {
            combatChoiceStage.close();
        }
        if (combatResultStage != null && combatResultStage.isShowing()) {
            combatResultStage.close();
        }

        Platform.runLater(() -> {
            String title = "KIEPSKIE ZAKOŃCZENIE";
            String header = "💀 NO I KLOPS, UMARŁEŚ! 💀";
            String content = playerName + ", to tak się kończy, jak się łazi po lochach bez rozumu.\n" +
                    "Liczyłeś na chwałę? No, nie tym razem. Twoje kości będą dobrą strawą dla szczurów.\n\n" +
                    "A tyle gadałem, żebyś uważał. Ale dobra, marny wiedźmin ze złymi wyborami.\n" +
                    "Wracasz do nauki na sesję? Albo może spróbujesz znowu, z nadzieją na mniej żałosny koniec?\n" +
                    "Bo przecież nawet z trupa da się wycisnąć jeszcze trochę pecha.";
            String imagePath = "/images/game_over.png";
            String buttonText = "HERE WE GO AGAIN!";
            String buttonText2 = "WRÓĆ DO MENU";

            double customWidth = 850; 
            double customHeight = 450;
            showEndGameDialog(title, header, content, imagePath, buttonText, buttonText2, customWidth, customHeight);
        });
    }

    public void showVictory(Enemy boss, int expGained) {
        if (gameMusicPlayer != null) {
            gameMusicPlayer.stop();
        }
        if (victoryMusicPlayer != null) {
            victoryMusicPlayer.play();
        }
        if (combatChoiceStage != null && combatChoiceStage.isShowing()) {
            combatChoiceStage.close();
        }
        if (combatResultStage != null && combatResultStage.isShowing()) {
            combatResultStage.close();
        }

        String title = "👑 ZWYCIĘSTWO WIEDŹMINA!";
        String header = "🐉 POKONAŁEŚ " + boss.getName().toUpperCase() + "!";
        String content = String.format(boss.getDeathText(), expGained);

        String imagePath = "/images/end_victory.png";
        String buttonText = "NOWA PRZYGODA";
        String buttonText2 = "WRÓĆ DO MENU";

        double victoryWidth = 800;
        double victoryHeight = 550;
        showEndGameDialog(title, header, content, imagePath, buttonText, buttonText2, victoryWidth, victoryHeight);
    }

    private void showHelp() {
        Stage helpStage = new Stage();
        helpStage.initModality(Modality.WINDOW_MODAL);
        helpStage.initOwner(mainApp.getPrimaryStage());
        helpStage.setTitle("Pomoc - Wiedźmin: Lochy Novigradu");
        helpStage.setResizable(false);
        helpStage.initStyle(StageStyle.UNDECORATED);
        helpStage.initModality(Modality.APPLICATION_MODAL);

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("custom-dialog-background");

        Text title = new Text("POMOC");
        title.getStyleClass().add("about-title");

        TextFlow contentTextFlow = new TextFlow();
        contentTextFlow.setTextAlignment(TextAlignment.CENTER);
        contentTextFlow.getStyleClass().add("about-content-flow");

        Text textPart1 = new Text(
                "Oho, znowu trzeba tłumaczyć. Słuchaj uważnie bo nie będe trzeci raz powtarzał.\n" +
                        "Wciel się w wiedźmina. Tak, TY. Ten sam, co nie odróżnia\n" +
                        "miecza srebrnego od łyżki i myśli, że lochy to jakaś tawerna.\n\n" +
                        "Pod Novigradem coś gryzie ludzi. Wielkie. Zębate. I wkur**ne.\n" +
                        "Twoim zadaniem – o ile nie padniesz po trzech krokach –\n" +
                        "jest je znaleźć i ubić. Prosto, prawda? Hah.\n\n"
        );
        textPart1.getStyleClass().add("about-content");

        Text movementHeading = new Text("🎮 STEROWANIE (żebyś znowu nie pytał):\n");
        movementHeading.getStyleClass().add("about-content");
        movementHeading.setStyle(movementHeading.getStyle() + "; -fx-font-weight: bold;");

        Text movementDetails = new Text(
                "• WASD – chodzisz. Jak nie działa, wina leży między krzesłem a klawiaturą.\n" +
                        "• Prawy przycisk myszy - klikasz gdzie popadnie.\n\n"
        );
        movementDetails.getStyleClass().add("about-content");

        Text tipsHeading = new Text("💡 WSKAZÓWKI (i tak je zignorujesz):\n");
        tipsHeading.getStyleClass().add("about-content");
        tipsHeading.setStyle(tipsHeading.getStyle() + "; -fx-font-weight: bold;");

        Text textPart2 = new Text(
                "• Pij mikstury, jakbyś miał rozum – nie masz, ale próbuj.\n" +
                        "• Zabijaj, co się rusza. Tylko nie strażników. *Zwłaszcza* nie strażników.\n" +
                        "• Przeszukuj wszystko – może trafisz coś cennego. Albo zgnijesz od zarazy.\n\n" +
                        "Na Szlak, bohaterze. Przynajmniej śmierć masz pewną. \n\n"
        );
        textPart2.getStyleClass().add("about-content");

        contentTextFlow.getChildren().addAll(textPart1, movementHeading, movementDetails, tipsHeading, textPart2);

        Button closeButton = new Button("🚪 ZROZUMIANO (chyba)");
        closeButton.getStyleClass().add("dialog-button-primary");
        closeButton.setOnAction(e -> {
            playButtonClickSound();
            helpStage.close();
        });

        root.getChildren().addAll(title, contentTextFlow, closeButton);

        Scene helpScene = new Scene(root);
        helpScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());

        helpStage.setScene(helpScene);
        helpStage.sizeToScene();

        helpStage.setOnShown(e -> {
            Stage primaryStage = mainApp.getPrimaryStage();
            helpStage.setX(primaryStage.getX() + (primaryStage.getWidth() - helpStage.getWidth()) / 2);
            helpStage.setY(primaryStage.getY() + (primaryStage.getHeight() - helpStage.getHeight()) / 2);
        });

        helpStage.showAndWait();
    }


    public void disposeResources() {
        if (gameMusicPlayer != null) {
            gameMusicPlayer.stop();
            gameMusicPlayer.dispose();
            gameMusicPlayer = null;
        }
        if (victoryMusicPlayer != null) {
            victoryMusicPlayer.stop();
            victoryMusicPlayer.dispose();
            victoryMusicPlayer = null;
        }
        if (combatChoiceStage != null) {
            combatChoiceStage.close();
            combatChoiceStage = null;
        }
        if (combatResultStage != null) {
            combatResultStage.close();
            combatResultStage = null;
        }
    }
}