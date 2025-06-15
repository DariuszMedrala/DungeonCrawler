package org.example.dungeonCrawler;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.example.dungeonCrawler.view.GameView;
import org.example.dungeonCrawler.view.MainMenuView;

import javafx.animation.FadeTransition;
import javafx.util.Duration;

import java.util.Objects;

public class Main extends Application {

    private Stage primaryStage;
    private StackPane rootStack;
    private Parent currentView;
    private MainMenuView mainMenuView;
    private GameView gameView;

    private Pane dimmerOverlay;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        rootStack = new StackPane();
        Scene scene = new Scene(rootStack, 1400, 850);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());

        dimmerOverlay = new Pane();
        dimmerOverlay.setStyle("-fx-background-color: black;");
        dimmerOverlay.setOpacity(0.0);
        dimmerOverlay.setMouseTransparent(true);
        rootStack.getChildren().add(dimmerOverlay);

        primaryStage.setTitle("Wiedźmin: Lochy Novigradu");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            if (mainMenuView != null) {
                mainMenuView.disposeResources();
            }
            if (gameView != null) {
                gameView.disposeResources();
            }
            Platform.exit();
            System.exit(0);
        });

        mainMenuView = new MainMenuView(this);
        rootStack.getChildren().setAll(mainMenuView.getViewLayout(), dimmerOverlay);
        currentView = mainMenuView.getViewLayout();
        FadeTransition initialFadeIn = new FadeTransition(Duration.seconds(1.0), currentView);
        initialFadeIn.setFromValue(0.0);
        initialFadeIn.setToValue(1.0);
        initialFadeIn.play();
        mainMenuView.playMusic();
    }

    public void transitionToView(Parent newView) {
        if (newView == null) {
            return;
        }
        if (currentView == newView) {
            return;
        }

        dimmerOverlay.setMouseTransparent(false);

        FadeTransition fadeToBlack = new FadeTransition(Duration.seconds(0.5), dimmerOverlay);
        fadeToBlack.setFromValue(0.0);
        fadeToBlack.setToValue(1.0);
        fadeToBlack.setOnFinished(event -> {

            if (currentView != null) {
                if (mainMenuView != null && currentView == mainMenuView.getViewLayout()) {
                    mainMenuView.disposeResources();
                } else if (gameView != null && currentView == gameView.getViewLayout()) {
                    gameView.disposeResources();
                }
            }

            rootStack.getChildren().remove(currentView);
            rootStack.getChildren().add(0, newView);

            currentView = newView;

            if (mainMenuView != null && currentView == mainMenuView.getViewLayout()) {
                mainMenuView.playMusic();
            } else if (gameView != null && currentView == gameView.getViewLayout()) {
                gameView.playMusic();
                gameView.setupKeyHandlers();
                gameView.updateDisplay();
            }

            FadeTransition fadeFromBlack = new FadeTransition(Duration.seconds(0.5), dimmerOverlay);
            fadeFromBlack.setFromValue(1.0);
            fadeFromBlack.setToValue(0.0);
            fadeFromBlack.setOnFinished(e -> {
                dimmerOverlay.setMouseTransparent(true);
            });
            fadeFromBlack.play();
        });
        fadeToBlack.play();
    }

    public void showMainMenu() {
        if (gameView != null) {
            gameView.disposeResources();
            gameView = null;
        }

        if (mainMenuView != null) {
            mainMenuView.disposeResources();
        }
        mainMenuView = new MainMenuView(this);

        transitionToView(mainMenuView.getViewLayout());
        mainMenuView.playMusic();
    }

    public void startGame(String playerName, boolean showWelcomeDialog) {
        if (gameView != null) {
            gameView.disposeResources();
            gameView = null;
        }
        if (mainMenuView != null) {
            mainMenuView.disposeResources();
        }
        gameView = new GameView(this, playerName);

        transitionToView(gameView.getViewLayout());
        gameView.playMusic();
        gameView.setupKeyHandlers();
        gameView.updateDisplay();
        if (showWelcomeDialog){
            gameView.showWelcomeDialog();
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}