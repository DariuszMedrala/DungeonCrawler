package org.example.dungeonCrawler.view;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import java.util.Objects;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import org.example.dungeonCrawler.Main;

public class MainMenuView {
    private final Main mainApp;
    private BorderPane menuLayout;
    private AudioClip buttonClickSound;
    private MediaPlayer menuMusicPlayer;

    public MainMenuView(Main mainApp) {
        this.mainApp = mainApp;
        loadSounds();
        createMenuLayout();
    }

    private void loadSounds() {
        String soundPath = Objects.requireNonNull(getClass().getResource("/sounds/button_click.wav")).toExternalForm();
        buttonClickSound = new AudioClip(soundPath);
        buttonClickSound.setVolume(0.05);

        String menuMusicPath = Objects.requireNonNull(getClass().getResource("/music/main_theme.mp3")).toExternalForm();
        Media menuMusic = new Media(menuMusicPath);
        menuMusicPlayer = new MediaPlayer(menuMusic);
        menuMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        menuMusicPlayer.setVolume(0.1);
    }

    private void playButtonClickSound() {
        if (buttonClickSound != null) {
            buttonClickSound.play();
        }
    }

    public void playMusic() {
        if (menuMusicPlayer != null) {
            menuMusicPlayer.play();
        }
    }


    private void createMenuLayout() {
        menuLayout = new BorderPane();
        menuLayout.getStyleClass().add("menu-root");

        VBox centerContent = new VBox(25);
        centerContent.setAlignment(Pos.CENTER);
        centerContent.setPadding(new Insets(20, 20, 10, 20));

        VBox titleBox = new VBox(5);
        titleBox.setAlignment(Pos.CENTER);
        Text title = new Text("Wiedźmin:\nLochy Novigradu");
        title.getStyleClass().add("menu-title-main");
        title.setTextAlignment(TextAlignment.CENTER);
        titleBox.getChildren().addAll(title);

        VBox buttonBox = new VBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setMaxWidth(300);
        Button newGameButton = new Button("🗡 NOWA GRA");
        newGameButton.getStyleClass().add("menu-button");
        newGameButton.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(newGameButton, new Insets(20, 0, 0, 0));

        Button aboutButton = new Button("📜 O GRZE");
        aboutButton.getStyleClass().add("menu-button");
        aboutButton.setMaxWidth(Double.MAX_VALUE);

        Button exitButton = new Button("🚪 WYJŚCIE");
        exitButton.getStyleClass().add("menu-button");
        exitButton.setMaxWidth(Double.MAX_VALUE);


        buttonBox.getChildren().addAll(newGameButton, aboutButton, exitButton);

        newGameButton.setOnAction(e -> {
            playButtonClickSound();
            startNewGame();
        });
        aboutButton.setOnAction(e -> {
            playButtonClickSound();
            showAbout();
        });
        exitButton.setOnAction(e -> {
            playButtonClickSound();
            Platform.exit();
            System.exit(0);
        });

        centerContent.getChildren().addAll(titleBox, buttonBox);

        Text footer = new Text("© 2025 Szkoła Wiedźmińska Cechu PK");
        footer.getStyleClass().add("menu-footer");

        menuLayout.setCenter(centerContent);
        menuLayout.setBottom(footer);
        BorderPane.setAlignment(footer, Pos.BOTTOM_LEFT);
        BorderPane.setMargin(footer, new Insets(10, 0, 20, 30));
    }

    private void startNewGame() {
        Stage nameStage = new Stage();
        nameStage.setTitle("Nowa Gra - Wiedźmin: Lochy Novigradu");
        nameStage.initOwner(mainApp.getPrimaryStage());
        nameStage.setResizable(false);

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("name-dialog");
        root.setPadding(new Insets(30));

        ImageView guardImage;
        guardImage = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/guard.jpg"))));
        guardImage.setFitHeight(400);
        guardImage.setPreserveRatio(true);

        StackPane imageContainer = new StackPane();
        imageContainer.getChildren().add(guardImage);
        imageContainer.getStyleClass().add("image-frame");
        imageContainer.setPrefWidth(Region.USE_COMPUTED_SIZE);

        VBox textAndInputVBox = new VBox(15);
        textAndInputVBox.setAlignment(Pos.CENTER);
        textAndInputVBox.setPadding(new Insets(0, 0, 0, 0));

        VBox headerBox = new VBox(5);
        headerBox.setAlignment(Pos.CENTER);
        Text header1 = new Text("Przy wejściu do lochu spotykasz strażnika");
        header1.getStyleClass().add("about-title");
        Text header3 = new Text("co licho na ciebie łypie!");
        header3.getStyleClass().add("about-title");
        headerBox.getChildren().addAll(header1, header3);

        VBox questionBox = new VBox(5);
        questionBox.setAlignment(Pos.CENTER);
        questionBox.setPadding(new Insets(10, 0, 10, 0));
        Text label1 = new Text("Tfu... wiedźmin.");
        label1.getStyleClass().add("guard-text-bold");
        Text label2 = new Text("Jeszcze jeden mutant z mieczem.");
        label2.getStyleClass().add("guard-text-bold");
        Text label3 = new Text("Mów, jak cię zwą!");
        label3.getStyleClass().add("guard-text-bold");
        questionBox.getChildren().addAll(label1, label2, label3);

        TextField nameField = new TextField();
        nameField.setPromptText("Geralt");
        nameField.getStyleClass().add("name-field");
        nameField.setMaxWidth(300);
        VBox.setMargin(nameField, new Insets(30, 0, 0, 0));

        HBox buttonBoxDialog = new HBox(20);
        buttonBoxDialog.setAlignment(Pos.CENTER);
        Button startButton = new Button("⚔ PODAJ IMIĘ");
        VBox.setMargin(startButton, new Insets(30, 0, 20, 0));
        startButton.getStyleClass().add("dialog-button-primary");
        startButton.setDisable(true);

        Button cancelButton = new Button("👊 DAJ MU W ZĘBY");
        VBox.setMargin(cancelButton, new Insets(30, 0, 0, 0));
        cancelButton.getStyleClass().add("dialog-button-secondary");
        buttonBoxDialog.getChildren().addAll(startButton, cancelButton);

        nameField.textProperty().addListener((observable, oldValue, newValue) -> startButton.setDisable(newValue.trim().isEmpty()));

        startButton.setOnAction(e -> {
            playButtonClickSound();
            String name = nameField.getText().trim();
            if (!name.isEmpty()) {
                nameStage.close();
                mainApp.startGame(name, true);
            }
        });

        cancelButton.setOnAction(e -> {
            playButtonClickSound();
            nameStage.close();

            Stage gameOverStage = new Stage();
            gameOverStage.setTitle("Koniec Gry");
            gameOverStage.initOwner(mainApp.getPrimaryStage());
            gameOverStage.setResizable(false);

            VBox gameOverRoot = new VBox(20);
            gameOverRoot.setAlignment(Pos.CENTER);
            gameOverRoot.getStyleClass().add("dialog-dark-background");
            gameOverRoot.setPadding(new Insets(30));

            ImageView gameOverImage;
            gameOverImage = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/prison.png"))));
            gameOverImage.setFitHeight(400);
            gameOverImage.setPreserveRatio(true);

            StackPane gameOverImageContainer = new StackPane();
            gameOverImageContainer.getChildren().add(gameOverImage);
            gameOverImageContainer.getStyleClass().add("image-frame-red");
            gameOverImageContainer.setPrefWidth(Region.USE_COMPUTED_SIZE);

            VBox gameOverTextAndButtonsVBox = new VBox(15);
            gameOverTextAndButtonsVBox.setAlignment(Pos.CENTER);

            Text header = new Text("💀 ZŁA DECYZJA! 💀\n");
            header.getStyleClass().add("game-over-text-bold");

            VBox contentBoxGameOver = new VBox(8);
            contentBoxGameOver.setAlignment(Pos.CENTER);
            contentBoxGameOver.setMaxWidth(450);

            Text line1 = new Text("No i co zrobiłeś, półgłówku?\n" +
                    "Walnąłeś strażnika w mordę.\n" +
                    "Teraz gnijesz w celi, gdzie szczury mają imiona.\n");
            line1.getStyleClass().add("game-over-text");
            line1.setWrappingWidth(400);
            Text line2 = new Text("Bez procesu. Bez kolacji. Bez litości.\n" +
                    "KONIEC TEJ ŻAŁOSNEJ PRZYGODY!");
            line2.getStyleClass().add("game-over-text-mid");
            line2.setWrappingWidth(400);
            contentBoxGameOver.getChildren().addAll(line1, line2);

            Button closeButtonGameOver = new Button("☠ ZAMKNIJ");
            closeButtonGameOver.getStyleClass().add("dialog-button-secondary");
            closeButtonGameOver.setOnAction(ev -> {
                playButtonClickSound();
                gameOverStage.close();
            });

            gameOverTextAndButtonsVBox.getChildren().addAll(header, contentBoxGameOver, closeButtonGameOver);
            VBox.setMargin(closeButtonGameOver, new Insets(50, 0, 0, 0));
            createMainContentHBox(gameOverRoot, gameOverImageContainer, gameOverTextAndButtonsVBox);

            Scene gameOverScene = new Scene(gameOverRoot, 850, 500);
            gameOverScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());

            gameOverStage.setScene(gameOverScene);
            gameOverStage.show();
        });

        nameField.setOnAction(e -> {
            if (!startButton.isDisabled()) {
                playButtonClickSound();
                startButton.fire();
            }
        });

        textAndInputVBox.getChildren().addAll(headerBox, questionBox, nameField, buttonBoxDialog);

        createMainContentHBox(root, imageContainer, textAndInputVBox);

        Scene scene = new Scene(root, 1400, 500);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
        nameStage.setScene(scene);
        nameStage.show();
        nameField.requestFocus();
    }

    private void createMainContentHBox(VBox root, StackPane imageContainer, VBox textAndInputVBox) {
        HBox mainContentHBox = new HBox(10);
        mainContentHBox.setAlignment(Pos.CENTER);

        imageContainer.getChildren();
        mainContentHBox.getChildren().addAll(imageContainer, textAndInputVBox);
        HBox.setHgrow(imageContainer, Priority.NEVER);
        HBox.setHgrow(textAndInputVBox, Priority.ALWAYS);

        root.getChildren().addAll(mainContentHBox);
    }

    private void showAbout() {
        Stage aboutStage = new Stage();
        aboutStage.setTitle("O Grze - Wiedźmin: Lochy Novigradu");
        aboutStage.initOwner(mainApp.getPrimaryStage());
        aboutStage.setResizable(false);

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("about-dialog");
        root.setPadding(new Insets(25));

        Text title = new Text("⚔ WIEDŹMIN: LOCHY NOVIGRADU ⚔");
        title.getStyleClass().add("about-title");

        TextFlow contentTextFlow = new TextFlow();
        contentTextFlow.setTextAlignment(TextAlignment.CENTER);
        contentTextFlow.setPrefWidth(540);
        contentTextFlow.setMaxWidth(540);

        Text textPart1 = new Text(
                "Wciel się w wiedźmina. Tak, TY. Ten sam, co nie odróżnia\n" +
                        "miecza srebrnego od łyżki i myśli, że lochy to jakaś tawerna.\n\n" +
                        "Pod Novigradem coś gryzie ludzi. Wielkie. Zębate. I wkur**ne.\n" +
                        "Twoim zadaniem – o ile nie padniesz po trzech krokach –\n" +
                        "jest je znaleźć i ubić. Prosto, prawda? Hah.\n\n"
        );
        textPart1.getStyleClass().add("about-content");

        Text sterowanieHeading = new Text("🎮 STEROWANIE (żebyś znowu nie pytał):\n");
        sterowanieHeading.getStyleClass().add("about-content");
        sterowanieHeading.setStyle(sterowanieHeading.getStyle() + "; -fx-font-weight: bold;");

        Text sterowanieDetails = new Text(
                "• WASD – chodzisz. Jak nie działa, wina leży między krzesłem a klawiaturą.\n" +
                        "• Prawy przycisk myszy - klikasz gdzie popadnie.\n\n"
        );
        sterowanieDetails.getStyleClass().add("about-content");

        Text wskazowkiHeading = new Text("💡 WSKAZÓWKI (i tak je zignorujesz):\n");
        wskazowkiHeading.getStyleClass().add("about-content");
        wskazowkiHeading.setStyle(wskazowkiHeading.getStyle() + "; -fx-font-weight: bold;");

        Text textPart2 = new Text(
                "• Pij mikstury, jakbyś miał rozum – nie masz, ale próbuj.\n" +
                        "• Zabijaj, co się rusza. Tylko nie strażników. *Zwłaszcza* nie strażników.\n" +
                        "• Przeszukuj wszystko – może trafisz na coś cennego. Albo zgnijesz od zarazy.\n\n" +
                        "Na Szlak, bohaterze. Przynajmniej śmierć masz pewną. \n\n" +
                        "Autorzy (Michał Kowal, Jerzy Kufel, Dariusz Mędrala) mieli prosty wybór:\n" +
                        " stać się legendami lub programować grę w Javie. \n" +
                        "No i masz babo placek.\n" +
                        "Właściciel assetów użytych w grze jest CD Projekt Red.\n"
        );
        textPart2.getStyleClass().add("about-content");

        contentTextFlow.getChildren().addAll(textPart1, sterowanieHeading, sterowanieDetails, wskazowkiHeading,textPart2);

        Button closeButtonAbout = new Button("🚪 ZAMKNIJ");
        closeButtonAbout.getStyleClass().add("dialog-button-primary");
        closeButtonAbout.setOnAction(e -> {
            playButtonClickSound();
            aboutStage.close();
        });

        root.getChildren().addAll(title, contentTextFlow, closeButtonAbout);

        Scene scene = new Scene(root, 600, 700);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
        aboutStage.setScene(scene);
        aboutStage.show();
    }

    public Parent getViewLayout() {
        return menuLayout;
    }

    public void disposeResources() {
        if (menuMusicPlayer != null) {
            menuMusicPlayer.stop();
            menuMusicPlayer.dispose();
            menuMusicPlayer = null;
        }
        if (buttonClickSound != null) {
            buttonClickSound.stop();
            buttonClickSound = null;
        }
    }
}