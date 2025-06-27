package org.example.dungeonCrawler.view;
import javafx.animation.*;
import javafx.geometry.Bounds;
import javafx.util.Duration;
import org.example.dungeonCrawler.model.items.*;
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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.example.dungeonCrawler.Main;
import org.example.dungeonCrawler.model.items.armors.Armor;
import org.example.dungeonCrawler.model.items.armors.DragonScaleArmor;
import org.example.dungeonCrawler.model.items.keys.Key;
import org.example.dungeonCrawler.model.items.potions.*;
import org.example.dungeonCrawler.model.items.weapons.DragonSword;
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
    private MediaPlayer defeatMusicPlayer;
    private MediaPlayer combatMusicPlayer;
    private PauseTransition tooltipDelay;

    private Stage combatChoiceStage;
    private Stage combatResultStage;
    private Stage interactiveCombatStage;
    private TextFlow combatLogTextFlow;
    private ProgressBar playerHealthBar;
    private Label playerHealthLabel;
    private Label playerDamageLabel;
    private Label playerArmorLabel;
    private ProgressBar enemyHealthBar;
    private Label enemyHealthLabel;
    private Label enemyDamageLabel;
    private Button attackBtn, critAttackBtn, igniBtn, aardBtn, quenBtn, aksjiBtn;
    private Stage customTooltipStage;
    private StackPane playerAvatarContainer;
    private StackPane enemyAvatarContainer;
    private Label turnIndicatorLabel;

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


        String defeatMusicPath = Objects.requireNonNull(getClass().getResource("/music/defeat_music.mp3")).toExternalForm();
        Media defeatMusic = new Media(defeatMusicPath);
        defeatMusicPlayer = new MediaPlayer(defeatMusic);
        defeatMusicPlayer.setVolume(0.3);
        defeatMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);

        String combatMusicPath = Objects.requireNonNull(getClass().getResource("/music/combat_music.mp3")).toExternalForm();
        Media combatMusic = new Media(combatMusicPath);
        combatMusicPlayer = new MediaPlayer(combatMusic);
        combatMusicPlayer.setVolume(0.15);
        combatMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
    }

    private void playButtonClickSound() {
        if (buttonClickSound != null) {
            buttonClickSound.play();
        }
    }

    private void createMainLayout() {
        gameLayout = new BorderPane();
        gameLayout.getStyleClass().add("root-no-radius");

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
        welcomeStage.initStyle(StageStyle.TRANSPARENT);
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
        welcomeScene.setFill(Color.TRANSPARENT);
        welcomeScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());

        welcomeStage.setScene(welcomeScene);

        welcomeStage.setOnShown(e -> {
            Stage primaryStage = mainApp.getPrimaryStage();
            welcomeStage.setX(primaryStage.getX() + (primaryStage.getWidth() - welcomeStage.getWidth()) / 2);
            welcomeStage.setY(primaryStage.getY() + (primaryStage.getHeight() - welcomeStage.getHeight()) / 2);
            AnimationUtil.playFadeInTransition(root, null);
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
                        backgroundColor = Color.RED;
                        borderColor = Color.DARKRED;
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
                if (room.getType() == Room.RoomType.MERCHANT) {
                    java.io.InputStream imageStream = getClass().getResourceAsStream("/images/merchant_icon.png");
                    assert imageStream != null;
                    Image merchantImage = new Image(imageStream);
                    ImageView merchantIconView = new ImageView(merchantImage);
                    merchantIconView.setFitWidth(18);
                    merchantIconView.setFitHeight(18);
                    merchantIconView.setPreserveRatio(true);
                    merchantIconView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.9), 2, 0.0, 0, 1);");
                    cellNode.getChildren().add(merchantIconView);
                }
                else if (room.getEnemy() != null && room.getEnemy().isAlive() && room.getType() != Room.RoomType.EVENT && !(x == player.getX() && y == player.getY())) {
                    String markerSymbol;
                    double markerFontSize;
                    Color symbolFillColor;

                    if (room.getType() == Room.RoomType.BOSS) {
                        markerSymbol = "🐉";
                        markerFontSize = 16;
                        symbolFillColor = Color.GREEN;
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

    private HBox createLegendItemWithImage(Color color, String description, String imagePath) {
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

        try (java.io.InputStream imageStream = getClass().getResourceAsStream(imagePath)) {
            if (imageStream != null) {
                Image iconImage = new Image(imageStream);
                ImageView iconView = new ImageView(iconImage);
                iconView.setFitWidth(20);
                iconView.setFitHeight(20);
                iconView.setPreserveRatio(true);
                iconView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.9), 2, 0.0, 0, 1);");
                symbolSwatchContainer.getChildren().add(iconView);
            }
        } catch (Exception e) {
            System.err.println("Could not load legend image: " + imagePath);
        }

        legendItem.getChildren().add(symbolSwatchContainer);

        Label descLabel = new Label(description);
        descLabel.getStyleClass().add("legend-text");
        HBox.setMargin(descLabel, new Insets(0, 0, 0, 5));

        legendItem.getChildren().add(descLabel);
        return legendItem;
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
        eventStage.initStyle(StageStyle.TRANSPARENT);
        eventStage.initModality(Modality.APPLICATION_MODAL);

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("custom-dialog-background");

        ImageView eventImage;
        eventImage = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/surprise.jpg"))));


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

        Scene eventScene = new Scene(root, 1200, 470);
        eventScene.setFill(Color.TRANSPARENT);
        eventScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
        eventStage.setScene(eventScene);
        eventStage.sizeToScene();

        eventStage.setOnShown(e -> {
            Stage primaryStage = mainApp.getPrimaryStage();
            eventStage.setX(primaryStage.getX() + (primaryStage.getWidth() - eventStage.getWidth()) / 2);
            eventStage.setY(primaryStage.getY() + (primaryStage.getHeight() - eventStage.getHeight()) / 2);
            AnimationUtil.playFadeInTransition(root, null);
        });

        eventStage.showAndWait();
    }


    private void createSidePanel() {
        sidePanel = new VBox();
        sidePanel.setPadding(new Insets(15));
        sidePanel.getStyleClass().add("side-panel");
        sidePanel.setPrefWidth(380);

        Label sidePanelTitleLabel = new Label("WIEDŹMIN: " + playerName);
        sidePanelTitleLabel.getStyleClass().add("status-effects");
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
        statsTitle.getStyleClass().add("status-effects");
        playerStatsLabel = new Label("Ładowanie statystyk...");
        playerStatsLabel.getStyleClass().clear();
        playerStatsLabel.getStyleClass().add("status-effects-green");
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
        statusTitle.getStyleClass().add("status-effects");

        statusEffectsLabel = new Label("Brak aktywnych statusów.");
        statusEffectsLabel.getStyleClass().add("status-effects-green");
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
            backpackStage.initStyle(StageStyle.TRANSPARENT);
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
            backpackScene.setFill(Color.TRANSPARENT);
            backpackScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
            backpackStage.setScene(backpackScene);

            backpackStage.setOnShown(e -> {
                Stage primaryStage = mainApp.getPrimaryStage();
                backpackStage.setX(primaryStage.getX() + (mapScrollPane.getWidth() - backpackStage.getWidth()) / 2);
                backpackStage.setY(primaryStage.getY() + 70);
                AnimationUtil.playFadeInTransition(root, null);
            });
        }

        updateBackpackContent();
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
        discoveryStage.initStyle(StageStyle.TRANSPARENT);
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
        discoveryScene.setFill(Color.TRANSPARENT);
        discoveryScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
        discoveryStage.setScene(discoveryScene);
        discoveryStage.sizeToScene();

        discoveryStage.setOnShown(e -> {
            Stage primaryStage = mainApp.getPrimaryStage();
            discoveryStage.setX(primaryStage.getX() + (primaryStage.getWidth() - discoveryStage.getWidth()) / 2);
            discoveryStage.setY(primaryStage.getY() + (primaryStage.getHeight() - discoveryStage.getHeight()) / 2);
            AnimationUtil.playFadeInTransition(root, null);
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
        treasureDiscoveryStage.initStyle(StageStyle.TRANSPARENT);
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
                openButton.setDisable(true);
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
                    treasureRoom.removeItem();
                    treasureRoom.setTreasureOpened(true);
                    treasureDiscoveryStage.setOnHidden(event -> {
                        showTreasureFoundDialog(treasureItem, mainApp.getPrimaryStage());
                        updateDisplay();
                    });
                    treasureDiscoveryStage.close();
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
        discoveryScene.setFill(Color.TRANSPARENT);
        discoveryScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());

        treasureDiscoveryStage.setScene(discoveryScene);
        treasureDiscoveryStage.sizeToScene();

        treasureDiscoveryStage.setOnShown(e -> {
            Stage primaryStage = mainApp.getPrimaryStage();
            treasureDiscoveryStage.setX(primaryStage.getX() + (primaryStage.getWidth() - treasureDiscoveryStage.getWidth()) / 2);
            treasureDiscoveryStage.setY(primaryStage.getY() + (primaryStage.getHeight() - treasureDiscoveryStage.getHeight()) / 2);
            AnimationUtil.playFadeInTransition(root, null);
        });

        treasureDiscoveryStage.showAndWait();
    }

    private void showTreasureFoundDialog(Item foundItem, Stage parentStage) {
        Stage treasureFoundStage = new Stage();
        treasureFoundStage.initModality(Modality.APPLICATION_MODAL);
        treasureFoundStage.initOwner(parentStage);
        treasureFoundStage.setTitle("Skarb Znaleziony!");
        treasureFoundStage.setResizable(false);
        treasureFoundStage.initStyle(StageStyle.TRANSPARENT);
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
        scene.setFill(Color.TRANSPARENT);
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
            AnimationUtil.playFadeInTransition(root, null);
        });

        treasureFoundStage.showAndWait();
    }


    private void showDropItemConfirmation(Item item, Player player, Stage backpackOwnerStage) {
        Stage confirmDropStage = new Stage();
        confirmDropStage.initModality(Modality.APPLICATION_MODAL);
        confirmDropStage.initOwner(backpackOwnerStage);
        confirmDropStage.setTitle("Wyrzuć Przedmiot?");
        confirmDropStage.setResizable(false);
        confirmDropStage.initStyle(StageStyle.TRANSPARENT);
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
        confirmScene.setFill(Color.TRANSPARENT);
        confirmScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());

        confirmDropStage.setScene(confirmScene);
        confirmDropStage.sizeToScene();

        confirmDropStage.setOnShown(e -> {
            Stage primaryStage = mainApp.getPrimaryStage();
            confirmDropStage.setX(primaryStage.getX() + (primaryStage.getWidth() - confirmDropStage.getWidth()) / 2);
            confirmDropStage.setY(primaryStage.getY() + (primaryStage.getHeight() - confirmDropStage.getHeight()) / 2);
            AnimationUtil.playFadeInTransition(root, null);
        });

        confirmDropStage.showAndWait();
    }

    public void showLevelUpDialog() {
        Stage levelUpStage = new Stage();
        levelUpStage.initModality(Modality.APPLICATION_MODAL);
        levelUpStage.initOwner(mainApp.getPrimaryStage());
        levelUpStage.setTitle("Awans na Poziom!");
        levelUpStage.setResizable(false);
        levelUpStage.initStyle(StageStyle.TRANSPARENT);
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
                "A to ci niespodzianka! %s, wlazłeś na POZIOM %d. Co za wyczyn!\n" +
                        "Twoje uderzenie ma teraz siłę solidnego kichnięcia,\n" +
                        "a pancerz wytrzyma jedno splunięcie ghula więcej. \n\n" +
                        "Nie ciesz się tak, to tylko gra pozorów, żebyś poczuł się lepiej,\n" +
                        "zanim jakiś przebrzydły nekker urwie ci łeb dla zabawy.\n" +
                        "Większy poziom oznacza tylko, że spadasz z wyższego konia.\n\n" +
                        "Ale hej, gratulacje! Przynajmniej umrzesz z ładniejszymi cyferkami w statystykach.\n",
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

        Scene scene = new Scene(root, 900, 500);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());

        levelUpStage.setScene(scene);
        levelUpStage.sizeToScene();

        levelUpStage.setOnShown(e -> {
            Stage primaryStage = mainApp.getPrimaryStage();
            levelUpStage.setX(primaryStage.getX() + (primaryStage.getWidth() - levelUpStage.getWidth()) / 2);
            levelUpStage.setY(primaryStage.getY() + (primaryStage.getHeight() - levelUpStage.getHeight()) / 2);
            AnimationUtil.playFadeInTransition(root, null);
        });

        levelUpStage.showAndWait();
    }


    private void showItemDetailsPopup(Item item, Label itemLabel) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(mainApp.getPrimaryStage());
        popupStage.setResizable(false);
        popupStage.setTitle("Szczegóły Przedmiotu");
        popupStage.initStyle(StageStyle.TRANSPARENT);
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
        popupScene.setFill(Color.TRANSPARENT);
        popupScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());

        popupStage.setScene(popupScene);

        popupStage.setOnShown(e -> {
            double screenX = itemLabel.localToScreen(itemLabel.getBoundsInLocal()).getMinX();
            double screenY = itemLabel.localToScreen(itemLabel.getBoundsInLocal()).getMinY();
            popupStage.setX(screenX + itemLabel.getWidth() + 10);
            popupStage.setY(screenY);
            AnimationUtil.playFadeInTransition(popupLayout, null);
        });

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
        menuStage.initStyle(StageStyle.TRANSPARENT);
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
        menuDialogScene.setFill(Color.TRANSPARENT);
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
            AnimationUtil.playFadeInTransition(menuLayout, null);
        });

        menuStage.showAndWait();
    }

    private void confirmNewGame(Stage menuStageToClose) {
        Stage confirmNewGameStage = new Stage();
        confirmNewGameStage.initModality(Modality.APPLICATION_MODAL);
        confirmNewGameStage.initOwner(menuStageToClose);
        confirmNewGameStage.setTitle("Nowa Gra?");
        confirmNewGameStage.setResizable(false);
        confirmNewGameStage.initStyle(StageStyle.TRANSPARENT);
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
        confirmScene.setFill(Color.TRANSPARENT);
        confirmScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());

        confirmNewGameStage.setScene(confirmScene);
        confirmNewGameStage.sizeToScene();

        confirmNewGameStage.setOnShown(e -> {
            Stage primaryStage = mainApp.getPrimaryStage();
            confirmNewGameStage.setX(primaryStage.getX() + (primaryStage.getWidth() - confirmNewGameStage.getWidth()) / 2);
            confirmNewGameStage.setY(primaryStage.getY() + (primaryStage.getHeight() - confirmNewGameStage.getHeight()) / 2);
            AnimationUtil.playFadeInTransition(root, null);
        });

        confirmNewGameStage.showAndWait();
    }


    private void showLegendDialog() {
        Stage legendStage = new Stage();
        legendStage.initModality(Modality.WINDOW_MODAL);
        legendStage.initOwner(mainApp.getPrimaryStage());
        legendStage.setTitle("Legenda Mapy");
        legendStage.setResizable(false);
        legendStage.initStyle(StageStyle.TRANSPARENT);
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
        legendItems.add(createLegendItemWithImage(Color.rgb(40, 40, 40), "Tajemnicza postać", "/images/merchant_icon.png"));
        legendItems.add(createLegendItem(Color.rgb(40, 40, 40), "Potwór", "💀", Color.WHITE));
        legendItems.add(createLegendItem(Color.rgb(40, 40, 40), "Niespodzianka", "❓", Color.RED));
        legendItems.add(createLegendItem(Color.GOLD, "Gracz (Normalny)", null, Color.BLACK));
        legendItems.add(createLegendItem(Color.GREEN, "Gracz (Zatruty)", null, Color.BLACK));
        legendItems.add(createLegendItem(Color.RED, "Gracz (Wzmocniony)", null, Color.BLACK));
        legendItems.add(createLegendItem(Color.rgb(70, 130, 180), "Przedmiot", null, null));
        legendItems.add(createLegendItem(Color.rgb(87, 12, 89), "Skarb", null, null));
        legendItems.add(createLegendItem(Color.rgb(50, 150, 50), "Start", null, null));
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
        legendScene.setFill(Color.TRANSPARENT);
        legendScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
        legendStage.setScene(legendScene);

        legendStage.setOnShown(e -> {
            Stage primaryStage = mainApp.getPrimaryStage();
            legendStage.setX(primaryStage.getX() + (primaryStage.getWidth() - legendStage.getWidth()) / 2);
            legendStage.setY(primaryStage.getY() + (primaryStage.getHeight() - legendStage.getHeight()) / 2);
            AnimationUtil.playFadeInTransition(root, null);
        });

        legendStage.showAndWait();
    }


    private void returnToMenu(Stage menuStageToClose) {
        Stage confirmStage = new Stage();
        confirmStage.initModality(Modality.WINDOW_MODAL);
        confirmStage.initOwner(mainApp.getPrimaryStage());
        confirmStage.setTitle("Potwierdzenie Wyjścia");
        confirmStage.setResizable(false);
        confirmStage.initStyle(StageStyle.TRANSPARENT);
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
        confirmScene.setFill(Color.TRANSPARENT);
        confirmScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
        confirmStage.setScene(confirmScene);
        confirmStage.sizeToScene();

        confirmStage.setOnShown(e -> {
            Stage primaryStage = mainApp.getPrimaryStage();
            confirmStage.setX(primaryStage.getX() + (primaryStage.getWidth() - confirmStage.getWidth()) / 2);
            confirmStage.setY(primaryStage.getY() + (primaryStage.getHeight() - confirmStage.getHeight()) / 2);
            AnimationUtil.playFadeInTransition(root, null);
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
        combatChoiceStage.initStyle(StageStyle.TRANSPARENT);
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
                String.format("PRZECIWNIK: \nŻYCIE: %d\nOBRAŻENIA: %d\n UMIEJĘTNOŚC SPECJALNA: " + enemy.getSpecialAbility() + "\n\n",
                        enemy.getHealth(), enemy.getDamage())
        );
        combatMessage.getStyleClass().add("enemy-text");
        Text combatMessagePartTwo = new Text(
                String.format("TWOJE STATYSTYKI:\n ŻYCIE: %d\nOBRAŻENIA: %d\nPANCERZ: %d\n\n",
                        player.getHealth(), player.getDamage(), player.getArmor())
        );
        combatMessagePartTwo.getStyleClass().add("player-text");
        Text combatMessageTwo = new Text(enemy.getEncounterText());
        combatMessageTwo.getStyleClass().add("about-content");
        contentTextFlow.getChildren().addAll(combatMessageTwo, combatMessage, combatMessagePartTwo);

        Button attackButton = new Button("⚔ WALCZ");
        attackButton.getStyleClass().add("dialog-button-primary");
        attackButton.setMaxWidth(Double.MAX_VALUE);
        attackButton.setOnAction(e -> {
            playButtonClickSound();
            combatChoiceStage.close();
            controller.beginInteractiveCombat();
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
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());

        combatChoiceStage.setScene(scene);

        combatChoiceStage.setOnShown(e -> {
            Stage primaryStage = mainApp.getPrimaryStage();
            combatChoiceStage.setX(primaryStage.getX() + (primaryStage.getWidth() - combatChoiceStage.getWidth()) / 2);
            combatChoiceStage.setY(primaryStage.getY() + (primaryStage.getHeight() - combatChoiceStage.getHeight()) / 2);
            AnimationUtil.playFadeInTransition(root, null);
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
        combatResultStage.initStyle(StageStyle.TRANSPARENT);
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

            if (playerWon) {
                fadeOutAndStop(combatMusicPlayer, () -> fadeIn(gameMusicPlayer, 0.1));
                if (levelUp) {
                    Platform.runLater(this::showLevelUpDialog);
                }
            }
            Platform.runLater(this::updateDisplay);
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

        Scene scene = new Scene(root, 900, 500);
        scene.setFill(Color.TRANSPARENT);

        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
        combatResultStage.setScene(scene);

        combatResultStage.setOnShown(e -> {
            Stage primaryStage = mainApp.getPrimaryStage();
            combatResultStage.setX(primaryStage.getX() + (primaryStage.getWidth() - combatResultStage.getWidth()) / 2);
            combatResultStage.setY(primaryStage.getY() + (primaryStage.getHeight() - combatResultStage.getHeight()) / 2);
            AnimationUtil.playFadeInTransition(root, null);
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
                controller.movePlayer(0, -1);
                break;
            case S:
                controller.movePlayer(0, 1);
                break;
            case A:
                controller.movePlayer(-1, 0);
                break;
            case D:
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
                        "ZBROJA: %s\n\n" +
                        "NOVIGRADZKIE FLORENY: %d 💰",
                player.getLevel(),
                player.getHealth(),
                player.getMaxHealth(),
                player.getExperience(),
                player.getLvlUpThreshold(),
                player.getDamage(),
                player.getArmor(),
                weaponInfo,
                armorInfo,
                player.getCoins()
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

    private void showEndGameDialog(String title, String header, String content, String imagePath, String buttonText, String frameStyleClass, String imageStyleClass, String textStyle, String titleStyle) {
        VBox contentRoot = new VBox(20);
        contentRoot.setPadding(new Insets(25));
        contentRoot.setAlignment(Pos.CENTER);
        contentRoot.getStyleClass().add("custom-dialog-background");

        ImageView dialogImage = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath))));
        dialogImage.setFitWidth(300);
        dialogImage.setPreserveRatio(true);
        StackPane imageContainer = new StackPane(dialogImage);
        imageContainer.getStyleClass().add(imageStyleClass);

        VBox textAndButtonsVBox = new VBox(15);
        textAndButtonsVBox.setAlignment(Pos.CENTER);

        Label localTitle = new Label(header);
        localTitle.getStyleClass().add(titleStyle);

        Text contentText = new Text(content);
        contentText.getStyleClass().add(textStyle);
        TextFlow contentTextFlow = new TextFlow(contentText);
        contentTextFlow.setTextAlignment(TextAlignment.CENTER);

        Button actionButton = new Button(buttonText);
        actionButton.getStyleClass().add("dialog-button-primary");
        actionButton.setOnAction(e -> {
            playButtonClickSound();
            ((Stage)actionButton.getScene().getWindow()).close();
            mainApp.startGame(playerName, false);
        });

        Button leaveButton = new Button("WRÓĆ DO MENU");
        leaveButton.getStyleClass().add("dialog-button-secondary");
        leaveButton.setOnAction(e -> {
            playButtonClickSound();
            ((Stage)leaveButton.getScene().getWindow()).close();
            mainApp.showMainMenu();
        });

        HBox buttonContainer = new HBox(20, actionButton, leaveButton);
        buttonContainer.setAlignment(Pos.CENTER);
        VBox.setMargin(buttonContainer, new Insets(30, 0, 0, 0));

        textAndButtonsVBox.getChildren().addAll(localTitle, contentTextFlow, buttonContainer);
        HBox mainContentHBox = new HBox(10, imageContainer, textAndButtonsVBox);
        mainContentHBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(textAndButtonsVBox, Priority.ALWAYS);
        contentRoot.getChildren().add(mainContentHBox);

        BorderPane frameRoot = new BorderPane(contentRoot);
        frameRoot.getStyleClass().add(frameStyleClass);

        Stage endGameStage = new Stage();
        endGameStage.initOwner(mainApp.getPrimaryStage());
        configureAndShowDialog(endGameStage, title, frameRoot);
    }

    private void configureAndShowDialog(Stage stage, String title, Parent rootContent) {
        stage.setTitle(title);
        if (stage.getStyle() != StageStyle.TRANSPARENT) {
            stage.initStyle(StageStyle.TRANSPARENT);
        }
        if (stage.getModality() != Modality.APPLICATION_MODAL) {
            stage.initModality(Modality.APPLICATION_MODAL);
        }

        Scene scene = new Scene(rootContent);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());

        stage.setScene(scene);
        stage.sizeToScene();

        stage.setOnShown(e -> {
            Stage owner = (Stage) stage.getOwner();
            if (owner != null) {
                stage.setX(owner.getX() + (owner.getWidth() - stage.getWidth()) / 2);
                stage.setY(owner.getY() + (owner.getHeight() - stage.getHeight()) / 2);
            }
            AnimationUtil.playFadeInTransition(rootContent, null);
        });
        stage.showAndWait();
    }



    public void showGameOver() {
        fadeOutAndStop(combatMusicPlayer, () -> {
            if (defeatMusicPlayer != null) defeatMusicPlayer.play();
        });

        if (gameMusicPlayer != null) gameMusicPlayer.stop();

        if (combatChoiceStage != null && combatChoiceStage.isShowing()) combatChoiceStage.close();
        if (combatResultStage != null && combatResultStage.isShowing()) combatResultStage.close();
        Platform.runLater(() -> {
            String content = "(Ciężkie westchnięcie) Wiedziałem. Po prostu wiedziałem, że tak to się skończy.\n" +
                    "Zawsze pakowałeś się głową naprzód tam, gdzie nawet demony bały się zaglądać.\n\n" +
                    "I wiesz co jest najgorsze? Że przez chwilę, jedną małą, parszywą chwilę, myślałem, że ci się uda.\n" +
                    "Że ten jeden uparty osioł faktycznie dojdzie na sam koniec. Głupi ja.\n" +
                    "A teraz co? Zostawiłeś mnie samego z tym całym bałaganem. Dzięki, naprawdę.\n\n" +
                    "Dobra, koniec tych sentymentów, bo jeszcze pomyślisz, że mi zależy.\n" +
                    "Gotów na kolejną rundę upokorzeń? Czy może wolisz zostać tu na zawsze i robić za dekorację?";

            showEndGameDialog("KIEPSKIE ZAKOŃCZENIE", "💀 MARNA ŚMIERĆ! 💀", content,
                    "/images/game_over.png", "HERE WE GO AGAIN!", "custom-frame-red", "custom-frame-red", "game-over-text", "game-over-text-bold");
        });
    }

    public void showVictory(Enemy boss, int expGained) {
        fadeOutAndStop(combatMusicPlayer, () -> {
            if (victoryMusicPlayer != null) victoryMusicPlayer.play();
        });

        if (gameMusicPlayer != null) gameMusicPlayer.stop();
        if (combatChoiceStage != null && combatChoiceStage.isShowing()) combatChoiceStage.close();
        if (combatResultStage != null && combatResultStage.isShowing()) combatResultStage.close();

        String content = String.format(boss.getDeathText(), expGained);

        showEndGameDialog("👑 ZWYCIĘSTWO WIEDŹMINA!", "🐉 POKONAŁEŚ " + boss.getName().toUpperCase() + "!",
                content, "/images/end_victory.png", "NOWA PRZYGODA", "custom-frame-yellow", "custom-frame-yellow", "about-content", "game-over-text-mid");
    }

    private void showHelp() {
        Stage helpStage = new Stage();
        helpStage.initModality(Modality.WINDOW_MODAL);
        helpStage.initOwner(mainApp.getPrimaryStage());
        helpStage.setTitle("Pomoc - Wiedźmin: Lochy Novigradu");
        helpStage.setResizable(false);
        helpStage.initStyle(StageStyle.TRANSPARENT);
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
                        "• Lewy przycisk myszy - klikasz gdzie popadnie.\n\n"
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
        helpScene.setFill(Color.TRANSPARENT);
        helpScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());

        helpStage.setScene(helpScene);
        helpStage.sizeToScene();

        helpStage.setOnShown(e -> {
            Stage primaryStage = mainApp.getPrimaryStage();
            helpStage.setX(primaryStage.getX() + (primaryStage.getWidth() - helpStage.getWidth()) / 2);
            helpStage.setY(primaryStage.getY() + (primaryStage.getHeight() - helpStage.getHeight()) / 2);
            AnimationUtil.playFadeInTransition(root, null);
        });

        helpStage.showAndWait();
    }

    public void showCoinFoundDialog(int amount) {
        Stage coinStage = new Stage();
        coinStage.initModality(Modality.APPLICATION_MODAL);
        coinStage.initOwner(mainApp.getPrimaryStage());
        coinStage.setTitle("Znaleziono Monety!");
        coinStage.setResizable(false);
        coinStage.initStyle(StageStyle.TRANSPARENT);
        coinStage.initModality(Modality.APPLICATION_MODAL);

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("custom-dialog-background");

        ImageView coinImage = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/coins_found.png"))));
        coinImage.setFitHeight(350);
        coinImage.setPreserveRatio(true);

        StackPane imageContainer = new StackPane(coinImage);
        imageContainer.getStyleClass().add("image-frame");
        imageContainer.setPrefWidth(Region.USE_COMPUTED_SIZE);
        VBox textAndButtonsVBox = new VBox(15);
        textAndButtonsVBox.setAlignment(Pos.CENTER);
        textAndButtonsVBox.setPadding(new Insets(0, 20, 30, 20));

        Label title = new Label("💰 ZNALAZŁEŚ MONETY! 💰");
        title.getStyleClass().add("about-title");
        VBox.setMargin(title, new Insets(0, 0, 10, 0));

        Label contentLabel = new Label("No proszę, proszę... Co my tu mamy? Zguba jakiegoś zapominalskiego alchemika,\n" +
                "czy może łapówka dla trolla, który się rozmyślił w połowie mostu?\n" +
                "Monety! I to nie byle jakie – błyszczą, jakby dopiero co z mennicy wypadły!\n" +
                "No, może trochę zakurzone i śmierdzą stęchlizną, ale złoto to złoto.\n" +
                "Zgarniasz wszystko jak leci, zanim jakiś inny szczęściarz tu trafi.\n" +
                "W końcu, ile razy trafia się sakiewka leżąca odłogiem?\n\n" +
                "ZDOBYWASZ " + amount + " NOVIGRADZKIE FLORENY\n");
        contentLabel.getStyleClass().add("about-content");
        contentLabel.setTextAlignment(TextAlignment.CENTER);
        contentLabel.setWrapText(true);

        Button okButton = new Button("Zabierz");
        okButton.getStyleClass().add("dialog-button-primary");
        okButton.setOnAction(e -> {
            playButtonClickSound();
            coinStage.close();
        });

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        textAndButtonsVBox.getChildren().addAll(title, contentLabel, spacer, okButton);
        HBox mainContentHBox = new HBox(10);
        mainContentHBox.setAlignment(Pos.CENTER);
        mainContentHBox.getChildren().addAll(imageContainer, textAndButtonsVBox);
        HBox.setHgrow(textAndButtonsVBox, Priority.ALWAYS);

        root.getChildren().add(mainContentHBox);

        Scene scene = new Scene(root, 1000, 520);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
        coinStage.setScene(scene);
        coinStage.sizeToScene();

        coinStage.setOnShown(e -> {
            Stage primaryStage = mainApp.getPrimaryStage();
            coinStage.setX(primaryStage.getX() + (primaryStage.getWidth() - coinStage.getWidth()) / 2);
            coinStage.setY(primaryStage.getY() + (primaryStage.getHeight() - coinStage.getHeight()) / 2);
            AnimationUtil.playFadeInTransition(root, null);
        });

        coinStage.showAndWait();
    }

    public void showMerchantDialog(Player player) {
        Stage introductoryStage = new Stage();
        introductoryStage.initModality(Modality.APPLICATION_MODAL);
        introductoryStage.initOwner(mainApp.getPrimaryStage());
        introductoryStage.setTitle("Tajemniczy Kupiec");
        introductoryStage.setResizable(false);
        introductoryStage.initStyle(StageStyle.TRANSPARENT);

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("custom-dialog-background");
        ImageView merchantImage = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/merchant.png"))));
        merchantImage.setFitHeight(400);
        merchantImage.setPreserveRatio(true);

        StackPane imageContainer = new StackPane(merchantImage);
        imageContainer.getStyleClass().add("image-frame");
        imageContainer.setPrefWidth(Region.USE_COMPUTED_SIZE);

        VBox textAndButtonsVBox = new VBox(15);
        textAndButtonsVBox.setAlignment(Pos.CENTER);
        textAndButtonsVBox.setPadding(new Insets(0, 20, 30, 20));

        Label title = new Label("SHUPE - HANDLARZ?");
        title.getStyleClass().add("about-title");
        VBox.setMargin(title, new Insets(0, 0, 10, 0));

        Label contentLabel = new Label(
                "A to co? Ogr, co się w kupca bawi. Chyba za długo na słońcu siedział i mu się klepki \n" +
                        "w tej wielkiej łepetynie poluzowały. Uważaj, bo zamiast reszty wyda ci lepę na ucho.\n\n" +

                        "\"Ty. Wiedźmin. Patrz.\" – odzywa się stwór głosem, który brzmi jak tarcie o siebie dwóch głazów.\n" +
                        "Wskazuje wielkim paluchem na rozłożone na ziemi towary. – \"Shupe mieć dużo rzeczy. Dobre rzeczy.\n" +
                        "Ty dać żółte kamienie, Shupe tobie dać. Chcesz patrzeć? Kupować?\"\n\n" +

                        "Słyszysz go? 'Żółte kamienie'. Ten tytan intelektu nawet nie wie, co sprzedaje.\n" +
                        "Możesz zrobić interes życia, albo stracić łeb, bo źle policzysz. \n" +
                        "No, gadasz z nim, czy czekasz aż cię zlicytuje jakiemuś trollowi za beczkę grochu?"
        );
        contentLabel.getStyleClass().add("about-content");
        contentLabel.setTextAlignment(TextAlignment.CENTER);
        contentLabel.setWrapText(true);

        Button tradeButton = new Button("HANDLUJ");
        tradeButton.getStyleClass().add("dialog-button-primary");
        tradeButton.setOnAction(e -> {
            playButtonClickSound();
            introductoryStage.close();
            Platform.runLater(() -> showActualMerchantStore(player));
        });

        Button leaveButton = new Button("WYJDŹ");
        leaveButton.getStyleClass().add("dialog-button-secondary");
        leaveButton.setOnAction(e -> {
            playButtonClickSound();
            introductoryStage.close();
        });

        HBox buttonContainer = new HBox(20, tradeButton, leaveButton);
        buttonContainer.setAlignment(Pos.CENTER);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        textAndButtonsVBox.getChildren().addAll(title, contentLabel, spacer, buttonContainer);
        HBox mainContentHBox = new HBox(10);
        mainContentHBox.setAlignment(Pos.CENTER);
        mainContentHBox.getChildren().addAll(imageContainer, textAndButtonsVBox);
        HBox.setHgrow(textAndButtonsVBox, Priority.ALWAYS);

        root.getChildren().add(mainContentHBox);

        Scene scene = new Scene(root, 1200, 520);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
        introductoryStage.setScene(scene);
        introductoryStage.sizeToScene();

        introductoryStage.setOnShown(e -> {
            Stage primaryStage = mainApp.getPrimaryStage();
            introductoryStage.setX(primaryStage.getX() + (primaryStage.getWidth() - introductoryStage.getWidth()) / 2);
            introductoryStage.setY(primaryStage.getY() + (primaryStage.getHeight() - introductoryStage.getHeight()) / 2);
            AnimationUtil.playFadeInTransition(root, null);
        });

        introductoryStage.showAndWait();
    }

    private void showActualMerchantStore(Player player) {
        Stage merchantStage = new Stage();
        merchantStage.initModality(Modality.APPLICATION_MODAL);
        merchantStage.initOwner(mainApp.getPrimaryStage());
        merchantStage.setTitle("Kupiec");
        merchantStage.setResizable(false);
        merchantStage.initStyle(StageStyle.TRANSPARENT);

        HBox mainLayout = new HBox(15);
        mainLayout.getStyleClass().add("custom-dialog-background");
        mainLayout.setPadding(new Insets(20));
        mainLayout.setAlignment(Pos.CENTER);
        ImageView merchantImage = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/merchant.png"))));
        merchantImage.setFitHeight(400);
        merchantImage.setPreserveRatio(true);

        StackPane imageContainer = new StackPane(merchantImage);
        imageContainer.getStyleClass().add("image-frame");
        imageContainer.setAlignment(Pos.CENTER);
        imageContainer.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        VBox storePanel = new VBox(15);
        storePanel.setAlignment(Pos.CENTER);

        Label title = new Label("📜 SKŁADZIK SHUPE'A 📜");
        title.getStyleClass().add("about-title");
        Label playerCoinsLabel = new Label("TWOJE OSZCZĘDNOŚCI EMERYTALNE: " + player.getCoins() + " 💰");
        playerCoinsLabel.getStyleClass().add("status-effects-green");

        VBox headerBox = new VBox(5, title, playerCoinsLabel);
        headerBox.setAlignment(Pos.CENTER);

        java.util.Map<Item, Integer> stock = new HashMap<>();
        stock.put(new SmallHealthPotion(), 25);
        stock.put(new MediumHealthPotion(), 50);
        stock.put(new AntidotePotion(), 25);
        stock.put(new StrengthPotion(), 25);
        stock.put(new DragonSword(), 100);
        stock.put(new DragonScaleArmor(), 100);

        VBox itemsForSaleContainer = new VBox(10);
        itemsForSaleContainer.setPadding(new Insets(10));

        List<Runnable> buttonUpdaters = new ArrayList<>();

        for (java.util.Map.Entry<Item, Integer> entry : stock.entrySet()) {
            Item item = entry.getKey();
            Integer price = entry.getValue();

            boolean isUnique = !(item instanceof Potion);
            if (isUnique && player.getInventory().stream().anyMatch(invItem -> invItem.getName().equals(item.getName()))) {
                continue;
            }

            HBox itemRow = new HBox(10);
            itemRow.setAlignment(Pos.CENTER_LEFT);

            Label itemLabel = new Label(getItemIcon(item) + " " + item.getName() + " (" + price + " 💰)");
            itemLabel.getStyleClass().add("inventory");
            itemLabel.setPrefWidth(350);
            HBox buttonsBox = new HBox(5);
            buttonsBox.setAlignment(Pos.CENTER_RIGHT);

            Button buyButton = new Button("Kup");
            buyButton.getStyleClass().add("dialog-button-primary");
            buttonsBox.getChildren().add(buyButton);
            HBox.setHgrow(buttonsBox, Priority.ALWAYS);
            itemRow.getChildren().addAll(itemLabel, buttonsBox);

            Runnable updateButtonState = () -> {
                if (player.isInventoryFull()) {
                    buyButton.setDisable(true);
                    buyButton.setText("Pełny Ekwipunek");
                } else if (player.getCoins() < price) {
                    buyButton.setDisable(true);
                    buyButton.setText("Brak środków");
                } else {
                    buyButton.setDisable(false);
                    buyButton.setText("Kup");
                }
            };

            buttonUpdaters.add(updateButtonState);

            buyButton.setOnAction(e -> {
                playButtonClickSound();
                if (player.getCoins() >= price && !player.isInventoryFull()) {
                    player.spendCoins(price);
                    Item purchasedItem = createNewItemInstance(item);
                    if (purchasedItem != null) {
                        player.addItem(purchasedItem);
                    }
                    playerCoinsLabel.setText("Twoje monety: " + player.getCoins() + " 💰");
                    updateDisplay();

                    if (isUnique) {
                        itemsForSaleContainer.getChildren().remove(itemRow);
                        buttonUpdaters.remove(updateButtonState);
                    }

                    for (Runnable updater : buttonUpdaters) {
                        updater.run();
                    }
                }
            });

            itemsForSaleContainer.getChildren().add(itemRow);
        }

        for (Runnable updater : buttonUpdaters) {
            updater.run();
        }

        ScrollPane scrollPane = new ScrollPane(itemsForSaleContainer);
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        Button closeButton = new Button("Wyjdź");
        closeButton.getStyleClass().add("dialog-button-secondary");
        closeButton.setOnAction(e -> {
            playButtonClickSound();
            merchantStage.close();
            updateDisplay();
        });

        storePanel.getChildren().addAll(headerBox, scrollPane, closeButton);
        mainLayout.getChildren().addAll(imageContainer, storePanel);
        HBox.setHgrow(storePanel, Priority.ALWAYS);

        Scene merchantScene = new Scene(mainLayout, 900, 600);
        merchantScene.setFill(Color.TRANSPARENT);
        merchantScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
        merchantStage.setScene(merchantScene);

        merchantStage.setOnShown(e -> {
            Stage primaryStage = mainApp.getPrimaryStage();
            merchantStage.setX(primaryStage.getX() + (primaryStage.getWidth() - merchantStage.getWidth()) / 2 - 200);
            merchantStage.setY(primaryStage.getY() + (primaryStage.getHeight() - merchantStage.getHeight()) / 2);
            AnimationUtil.playFadeInTransition(mainLayout, null);
        });

        merchantStage.showAndWait();
    }
    private Item createNewItemInstance(Item item) {
        if (item instanceof SmallHealthPotion) return new SmallHealthPotion();
        if (item instanceof MediumHealthPotion) return new MediumHealthPotion();
        if (item instanceof AntidotePotion) return new AntidotePotion();
        if (item instanceof DragonSword) return new DragonSword();
        if (item instanceof DragonScaleArmor) return new DragonScaleArmor();
        if (item instanceof StrengthPotion) return new StrengthPotion();
        return null;
    }

    public void showInteractiveCombatScreen(Enemy enemy) {
        fadeOutAndPause(gameMusicPlayer, () -> fadeIn(combatMusicPlayer, 0.15));

        if (interactiveCombatStage != null && interactiveCombatStage.isShowing()) {
            return;
        }

        interactiveCombatStage = new Stage();
        interactiveCombatStage.initModality(Modality.APPLICATION_MODAL);
        interactiveCombatStage.initOwner(mainApp.getPrimaryStage());
        interactiveCombatStage.setTitle("WALKA: " + enemy.getName().toUpperCase());
        interactiveCombatStage.initStyle(StageStyle.TRANSPARENT);
        interactiveCombatStage.setOnCloseRequest(Event::consume);

        AnchorPane root = new AnchorPane();
        root.getStyleClass().add("custom-dialog-background");
        Label vsLabel = new Label("VS");
        vsLabel.getStyleClass().add("vs-text");

        turnIndicatorLabel = new Label();
        turnIndicatorLabel.getStyleClass().add("turn-indicator-text");
        turnIndicatorLabel.setVisible(false);

        VBox centerLabelsVBox = new VBox(10, vsLabel, turnIndicatorLabel);
        centerLabelsVBox.setAlignment(Pos.CENTER);
        centerLabelsVBox.setMouseTransparent(true);

        StackPane centerPane = new StackPane(centerLabelsVBox);
        AnchorPane.setTopAnchor(centerPane, 0.0);
        AnchorPane.setBottomAnchor(centerPane, 0.0);
        AnchorPane.setLeftAnchor(centerPane, 0.0);
        AnchorPane.setRightAnchor(centerPane, 0.0);

        Player player = controller.getPlayer();
        VBox playerInfoBox = createPlayerInfoBox(player);
        VBox enemyInfoBox = createEnemyInfoBox(enemy);
        GridPane actionGrid = createActionGrid();
        ScrollPane combatLogScrollPane = createCombatLog();

        root.getChildren().addAll(centerPane, playerInfoBox, enemyInfoBox, actionGrid, combatLogScrollPane);

        double margin = 15.0;
        AnchorPane.setBottomAnchor(playerInfoBox, margin);
        AnchorPane.setLeftAnchor(playerInfoBox, margin);
        AnchorPane.setTopAnchor(enemyInfoBox, margin);
        AnchorPane.setRightAnchor(enemyInfoBox, margin);
        AnchorPane.setTopAnchor(actionGrid, margin);
        AnchorPane.setLeftAnchor(actionGrid, margin);
        AnchorPane.setBottomAnchor(combatLogScrollPane, margin);
        AnchorPane.setRightAnchor(combatLogScrollPane, margin);

        Scene scene = new Scene(root, 900, 700);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());

        interactiveCombatStage.setScene(scene);

        interactiveCombatStage.setOnShown(e -> {
            Stage owner = mainApp.getPrimaryStage();
            if (owner != null) {
                double y = owner.getY() + (owner.getHeight() - interactiveCombatStage.getHeight()) / 2;
                double x = owner.getX() + 50;

                interactiveCombatStage.setX(x);
                interactiveCombatStage.setY(y);
            }
            AnimationUtil.playFadeInTransition(root, null);
        });

        interactiveCombatStage.show();
        refreshCombatScreenState(enemy);
    }

    private GridPane createActionGrid() {
        GridPane actionGrid = new GridPane();
        actionGrid.setAlignment(Pos.CENTER);
        actionGrid.setHgap(10);
        actionGrid.setVgap(10);

        attackBtn = new Button("⚔ Atak");
        critAttackBtn = new Button("💥 Atak Krytyczny");
        igniBtn = new Button("🔥 Znak Igni");
        aardBtn = new Button("💨 Znak Aard");
        quenBtn = new Button("🛡 Znak Quen");
        aksjiBtn = new Button("🌀 Znak Aksji");

        Button[] actionButtons = {attackBtn, critAttackBtn, igniBtn, aardBtn, quenBtn, aksjiBtn};
        for (Button btn : actionButtons) {
            btn.getStyleClass().add("dialog-button-primary");
            btn.setPrefWidth(220);
        }
        attackBtn.setOnAction(e -> { playButtonClickSound(); controller.handlePlayerAction("ATTACK"); });
        critAttackBtn.setOnAction(e -> { playButtonClickSound(); controller.handlePlayerAction("CRITICAL_ATTACK"); });
        igniBtn.setOnAction(e -> { playButtonClickSound(); controller.handlePlayerAction("IGNI"); });
        aardBtn.setOnAction(e -> { playButtonClickSound(); controller.handlePlayerAction("AARD"); });
        quenBtn.setOnAction(e -> { playButtonClickSound(); controller.handlePlayerAction("QUEN"); });
        aksjiBtn.setOnAction(e -> { playButtonClickSound(); controller.handlePlayerAction("AKSJI"); });

        customTooltipStage = new Stage();
        customTooltipStage.initOwner(mainApp.getPrimaryStage());
        customTooltipStage.initStyle(StageStyle.TRANSPARENT);
        customTooltipStage.setResizable(false);

        installCustomTooltip(attackBtn, "Standardowy, niezawodny atak mieczem.");
        installCustomTooltip(critAttackBtn, "Potężny cios z 50% szansą na trafienie.\nJeśli się powiedzie, zadaje podwójne obrażenia.");
        installCustomTooltip(igniBtn, "Wiedźmiński Znak zadający obrażenia od ognia.\nMoże być użyty raz na walkę.");
        installCustomTooltip(aardBtn, "Fala telekinetyczna, która zadaje niewielkie obrażenia i ogłusza przeciwnika na 1 turę.\nMoże być użyty raz na walkę.");
        installCustomTooltip(quenBtn, "Tworzy ochronną tarczę, która całkowicie zablokuje następny wrogi atak.\nMoże być użyty raz na walkę.");
        installCustomTooltip(aksjiBtn, "Wpływa na umysł przeciwnika, ogłuszając go na 2 tury.\nMoże być użyty raz na walkę.");

        actionGrid.add(attackBtn, 0, 0);
        actionGrid.add(critAttackBtn, 1, 0);
        actionGrid.add(igniBtn, 0, 1);
        actionGrid.add(aardBtn, 1, 1);
        actionGrid.add(quenBtn, 0, 2);
        actionGrid.add(aksjiBtn, 1, 2);

        return actionGrid;
    }

    public void setActionButtonsDisabled(boolean disabled) {
        if (attackBtn != null) {
            attackBtn.setDisable(disabled);
            critAttackBtn.setDisable(disabled);
            igniBtn.setDisable(disabled || controller.getPlayer().hasUsedIgni());
            aardBtn.setDisable(disabled || controller.getPlayer().hasUsedAard());
            quenBtn.setDisable(disabled || controller.getPlayer().hasUsedQuen() || controller.getPlayer().isQuenActive());
            aksjiBtn.setDisable(disabled || controller.getPlayer().hasUsedAksji());
        }
    }

    private void installCustomTooltip(Control control, String text) {
        if (tooltipDelay == null) {
            tooltipDelay = new PauseTransition(Duration.millis(500));
        }

        control.setOnMouseEntered(event -> {
            tooltipDelay.setOnFinished(e -> {

                Text tooltipText = new Text(text);
                tooltipText.setWrappingWidth(300);
                tooltipText.setTextAlignment(TextAlignment.CENTER);
                tooltipText.setStyle(
                        "-fx-fill: white; "
                                + "-fx-font-size: 14px; "
                                + "-fx-font-weight: bold;"
                );

                StackPane tooltipPane = new StackPane(tooltipText);
                tooltipPane.setPadding(new Insets(10));
                tooltipPane.setStyle(
                        "-fx-background-color: rgba(0, 0, 0, 0.85); "
                                + "-fx-background-radius: 6px;"
                );

                Scene scene = new Scene(tooltipPane);
                scene.setFill(Color.TRANSPARENT);
                customTooltipStage.setScene(scene);

                customTooltipStage.show();

                double stageWidth = customTooltipStage.getWidth();
                double stageHeight = customTooltipStage.getHeight();
                Bounds controlBounds = control.localToScreen(control.getBoundsInLocal());

                double finalX = controlBounds.getMinX() + (controlBounds.getWidth() - stageWidth) / 2;
                double finalY = controlBounds.getMinY() - stageHeight - 5;
                customTooltipStage.setX(finalX);
                customTooltipStage.setY(finalY);
            });
            tooltipDelay.playFromStart();
        });

        control.setOnMouseExited(event -> {
            tooltipDelay.stop();
            if (customTooltipStage != null) {
                customTooltipStage.hide();
            }
        });
    }

    private ScrollPane createCombatLog() {
        ScrollPane scrollPane = new ScrollPane();
        Text initialText = new Text("Walka się rozpoczyna!\n");
        initialText.getStyleClass().add("custom-title-text");

        this.combatLogTextFlow = new TextFlow(initialText);
        this.combatLogTextFlow.setPadding(new Insets(5));
        combatLogTextFlow.getStyleClass().add("custom-title-text");

        scrollPane.setContent(this.combatLogTextFlow);
        scrollPane.getStyleClass().add("image-frame");
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefSize(475, 200);
        this.combatLogTextFlow.heightProperty().addListener((observable) -> scrollPane.setVvalue(1.0));
        return scrollPane;
    }


    public void refreshCombatScreenState(Enemy enemy) {
        Player player = controller.getPlayer();

        animateHealthUpdate(playerHealthBar, playerHealthLabel, player.getHealth(), player.getMaxHealth());
        animateHealthUpdate(enemyHealthBar, enemyHealthLabel, enemy.getHealth(), enemy.getMaxHealth());

        playerDamageLabel.setText("⚔ Obrażenia: " + player.getDamage());
        playerArmorLabel.setText("🛡 Pancerz: " + player.getArmor());
        enemyDamageLabel.setText("⚔ Obrażenia: " + enemy.getDamage());
    }

    public void updateCombatLog(String message, String styleClass) {
        Text text = new Text(message + "\n");
        text.getStyleClass().add(styleClass);
        if (combatLogTextFlow != null) {
            combatLogTextFlow.getChildren().add(text);
        }
    }

    public void closeCombatScreen() {
        if (interactiveCombatStage != null && interactiveCombatStage.isShowing()) {
            interactiveCombatStage.close();
        }
    }

    private VBox createPlayerInfoBox(Player player) {
        VBox pane = new VBox(8);
        pane.setAlignment(Pos.CENTER);
        pane.setPadding(new Insets(15));
        pane.setPrefWidth(300);

        Label nameLabel = new Label("Wiedźmin " + playerName);
        nameLabel.getStyleClass().add("about-title");

        ImageView playerAvatar = new ImageView();
        InputStream avatarStream = getClass().getResourceAsStream("/images/victory.png");
        if (avatarStream != null) {
            playerAvatar.setImage(new Image(avatarStream));
            playerAvatar.setFitHeight(250);
            playerAvatar.setFitWidth(200);
        } else {
            System.err.println("Ostrzeżenie: Nie znaleziono awatara gracza pod ścieżką /images/player_avatar.png.");
        }

        StackPane imageContainer = new StackPane(playerAvatar);
        imageContainer.getStyleClass().add("image-frame");
        imageContainer.setAlignment(Pos.CENTER);
        imageContainer.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        this.playerAvatarContainer = imageContainer;

        this.playerHealthBar = new ProgressBar((double) player.getHealth() / player.getMaxHealth());
        this.playerHealthBar.setMaxWidth(Double.MAX_VALUE);
        this.playerHealthBar.getStyleClass().add("player-health-bar");

        this.playerHealthLabel = new Label("❤ " + player.getHealth() + " / " + player.getMaxHealth());
        this.playerHealthLabel.getStyleClass().add("player-text");

        this.playerDamageLabel = new Label("⚔ Obrażenia: " + player.getDamage());
        this.playerArmorLabel = new Label("🛡 Pancerz: " + player.getArmor());
        this.playerDamageLabel.getStyleClass().add("player-text");
        this.playerArmorLabel.getStyleClass().add("player-text");

        pane.getChildren().addAll(imageContainer, playerHealthBar, playerHealthLabel, playerDamageLabel, playerArmorLabel);
        return pane;
    }

    private VBox createEnemyInfoBox(Enemy enemy) {
        VBox pane = new VBox(8);
        pane.setAlignment(Pos.CENTER);
        pane.setPadding(new Insets(15));
        pane.setPrefWidth(300);

        Label nameLabel = new Label(enemy.getName());
        nameLabel.getStyleClass().add("about-title");

        ImageView enemyAvatar = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(enemy.getImagePath()))));
        enemyAvatar.setFitHeight(250);
        enemyAvatar.setFitWidth(200);

        StackPane imageContainer = new StackPane(enemyAvatar);
        imageContainer.getStyleClass().add("image-frame-red");
        imageContainer.setAlignment(Pos.CENTER);
        imageContainer.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        this.enemyAvatarContainer = imageContainer;

        this.enemyHealthBar = new ProgressBar((double) enemy.getHealth() / enemy.getMaxHealth());
        this.enemyHealthBar.setMaxWidth(Double.MAX_VALUE);
        this.enemyHealthBar.getStyleClass().add("enemy-health-bar");

        this.enemyHealthLabel = new Label("❤ " + enemy.getHealth() + " / " + enemy.getMaxHealth());
        this.enemyHealthLabel.getStyleClass().add("enemy-text");

        this.enemyDamageLabel = new Label("⚔ Obrażenia: " + enemy.getDamage());
        this.enemyDamageLabel.getStyleClass().add("enemy-text");

        pane.getChildren().addAll(imageContainer, enemyHealthBar, enemyHealthLabel, enemyDamageLabel);
        return pane;
    }

    public void enableActionButtons() {
        Platform.runLater(() -> {
            if (controller.getPlayer().isAlive() && controller.getEnemy().isAlive()) {
                refreshCombatScreenState(controller.getEnemy());
            }
        });
    }

    private void fadeIn(MediaPlayer mediaPlayer, double targetVolume) {
        if (mediaPlayer == null) return;
        mediaPlayer.setVolume(0);
        mediaPlayer.play();

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1.5),
                        new KeyValue(mediaPlayer.volumeProperty(), targetVolume))
        );
        timeline.play();
    }

    private void fadeOutAndPause(MediaPlayer mediaPlayer, Runnable onFinished) {
        if (mediaPlayer == null || mediaPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
            if (onFinished != null) onFinished.run();
            return;
        }

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1.5),
                        new KeyValue(mediaPlayer.volumeProperty(), 0))
        );
        timeline.setOnFinished(event -> {
            mediaPlayer.pause();
            if (onFinished != null) onFinished.run();
        });
        timeline.play();
    }

    private void fadeOutAndStop(MediaPlayer mediaPlayer, Runnable onFinished) {
        if (mediaPlayer == null || mediaPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
            if (onFinished != null) onFinished.run();
            return;
        }
        double currentVolume = mediaPlayer.getVolume();
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1.5),
                        new KeyValue(mediaPlayer.volumeProperty(), 0))
        );
        timeline.setOnFinished(event -> {
            mediaPlayer.stop();
            mediaPlayer.setVolume(currentVolume);
            if (onFinished != null) onFinished.run();
        });
        timeline.play();
    }

    private void animateHealthUpdate(ProgressBar healthBar, Label healthLabel, int newHealth, int maxHealth) {
        double targetHealth = Math.max(0, newHealth);

        double targetProgress = (maxHealth > 0) ? (targetHealth / maxHealth) : 0.0;

        Timeline timeline = new Timeline();

        KeyValue keyValue = new KeyValue(healthBar.progressProperty(), targetProgress);

        KeyFrame keyFrame = new KeyFrame(Duration.millis(400), keyValue);
        timeline.getKeyFrames().add(keyFrame);
        healthLabel.setText("❤ " + (int)targetHealth + " / " + maxHealth);
        timeline.play();
    }

    public void showFloatingText(String text, String targetCharacter, String type) {
        Platform.runLater(() -> {
            StackPane targetContainer;
            String styleClass;

            if ("PLAYER".equalsIgnoreCase(targetCharacter)) {
                targetContainer = playerAvatarContainer;
            } else {
                targetContainer = enemyAvatarContainer;
            }

            switch (type.toUpperCase()) {
                case "DAMAGE": styleClass = "damage-text"; break;
                case "CRIT": styleClass = "crit-text"; break;
                case "HEAL": styleClass = "heal-text"; break;
                default: styleClass = "info-text"; break;
            }

            if (targetContainer == null || interactiveCombatStage == null || !interactiveCombatStage.isShowing()) {
                return;
            }

            AnchorPane combatRoot = (AnchorPane) interactiveCombatStage.getScene().getRoot();
            Label floatingLabel = new Label(text);
            floatingLabel.getStyleClass().add(styleClass);

            Bounds targetBoundsInScreen = targetContainer.localToScreen(targetContainer.getBoundsInLocal());
            Bounds rootBoundsInScreen = combatRoot.localToScreen(combatRoot.getBoundsInLocal());

            if (targetBoundsInScreen == null || rootBoundsInScreen == null) return;

            floatingLabel.setPrefWidth(targetBoundsInScreen.getWidth());
            floatingLabel.setAlignment(Pos.CENTER);

            double startX = targetBoundsInScreen.getMinX() - rootBoundsInScreen.getMinX();
            double startY = targetBoundsInScreen.getMinY() - rootBoundsInScreen.getMinY();

            floatingLabel.setLayoutX(startX);
            floatingLabel.setLayoutY(startY);

            combatRoot.getChildren().add(floatingLabel);

            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(2500), floatingLabel);
            translateTransition.setByY(-70);
            translateTransition.setCycleCount(1);

            FadeTransition fadeTransition = new FadeTransition(Duration.millis(2500), floatingLabel);
            fadeTransition.setFromValue(1.0);
            fadeTransition.setToValue(0.0);
            fadeTransition.setCycleCount(1);

            ParallelTransition parallelTransition = new ParallelTransition(translateTransition, fadeTransition);
            parallelTransition.setOnFinished(event -> combatRoot.getChildren().remove(floatingLabel));
            parallelTransition.play();
        });
    }

    public void showTurnIndicator(String text, Runnable onFinished) {
        Platform.runLater(() -> {
            if (turnIndicatorLabel == null || interactiveCombatStage == null || !interactiveCombatStage.isShowing()) {
                if(onFinished != null) onFinished.run();
                return;
            }
            turnIndicatorLabel.setText(text);
            turnIndicatorLabel.setVisible(true);
            turnIndicatorLabel.setOpacity(0.0);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(400), turnIndicatorLabel);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);

            PauseTransition hold = new PauseTransition(Duration.millis(1500));

            FadeTransition fadeOut = new FadeTransition(Duration.millis(600), turnIndicatorLabel);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            SequentialTransition sequence = new SequentialTransition(fadeIn, hold, fadeOut);
            sequence.setOnFinished(e -> {
                turnIndicatorLabel.setVisible(false);
                if (onFinished != null) {
                    onFinished.run();
                }
            });
            sequence.play();
        });
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
        if (defeatMusicPlayer != null) {
            defeatMusicPlayer.stop();
            defeatMusicPlayer.dispose();
            defeatMusicPlayer = null;
        }
        if (combatChoiceStage != null) {
            combatChoiceStage.close();
            combatChoiceStage = null;
        }
        if (combatResultStage != null) {
            combatResultStage.close();
            combatResultStage = null;
        }
        if (combatMusicPlayer != null) {
            combatMusicPlayer.stop();
            combatMusicPlayer.dispose();
        }
    }
}
