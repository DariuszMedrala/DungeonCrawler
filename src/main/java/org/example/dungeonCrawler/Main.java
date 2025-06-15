package org.example.dungeonCrawler;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.dungeonCrawler.view.GameView;
import org.example.dungeonCrawler.view.MainMenuView;

import javafx.animation.FadeTransition;
import javafx.util.Duration;

import java.util.Objects;

public class Main extends Application {

    private Stage primaryStage;
    private StackPane viewContainer;

    private Parent currentView;
    private MainMenuView mainMenuView;
    private GameView gameView;

    private Pane dimmerOverlay;

    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.initStyle(StageStyle.UNDECORATED);

        StackPane rootStack = new StackPane();
        BorderPane mainLayout = new BorderPane();
        HBox customTitleBar = createCustomTitleBar();
        mainLayout.setTop(customTitleBar);

        viewContainer = new StackPane();
        mainLayout.setCenter(viewContainer);

        dimmerOverlay = new Pane();
        dimmerOverlay.setStyle("-fx-background-color: black;");
        dimmerOverlay.setOpacity(0.0);
        dimmerOverlay.setMouseTransparent(true);

        rootStack.getChildren().addAll(mainLayout, dimmerOverlay);

        Scene scene = new Scene(rootStack, 1400, 850);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
        primaryStage.setScene(scene);

        try {
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/witcher_icon.png")));
            primaryStage.getIcons().add(icon);
        } catch (NullPointerException e) {
            System.err.println("Error: Icon image not found.");
        }

        primaryStage.show();

        mainMenuView = new MainMenuView(this);
        currentView = mainMenuView.getViewLayout();
        viewContainer.getChildren().add(currentView);

        FadeTransition initialFadeIn = new FadeTransition(Duration.seconds(1.0), currentView);
        initialFadeIn.setFromValue(0.0);
        initialFadeIn.setToValue(1.0);
        initialFadeIn.play();
        mainMenuView.playMusic();
    }

    private HBox createCustomTitleBar() {
        HBox titleBar = new HBox();
        titleBar.setAlignment(Pos.CENTER_LEFT);
        titleBar.setPadding(new Insets(5, 10, 5, 20));
        titleBar.getStyleClass().add("custom-title-bar");

        Text title = new Text("WIEDŹMIN: LOCHY NOVIGRADU");
        title.getStyleClass().add("custom-title-text");
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button closeButton = new Button("X");
        closeButton.getStyleClass().add("custom-close-button");
        closeButton.setOnAction(e -> {
            if (mainMenuView != null) mainMenuView.disposeResources();
            if (gameView != null) gameView.disposeResources();
            Platform.exit();
            System.exit(0);
        });

        titleBar.getChildren().addAll(title, spacer, closeButton);

        titleBar.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        titleBar.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });
        return titleBar;
    }

    public void transitionToView(Parent newView) {
        if (newView == null || currentView == newView) {
            return;
        }

        dimmerOverlay.setMouseTransparent(false);
        FadeTransition fadeToBlack = new FadeTransition(Duration.seconds(0.5), dimmerOverlay);
        fadeToBlack.setFromValue(0.0);
        fadeToBlack.setToValue(1.0);

        fadeToBlack.setOnFinished(event -> {
            if (currentView != null) {
                if (mainMenuView != null && currentView == mainMenuView.getViewLayout()) mainMenuView.disposeResources();
                else if (gameView != null && currentView == gameView.getViewLayout()) gameView.disposeResources();
            }

            viewContainer.getChildren().clear();
            viewContainer.getChildren().add(newView);
            currentView = newView;

            if (mainMenuView != null && currentView.equals(mainMenuView.getViewLayout())) {
                mainMenuView.playMusic();
            } else if (gameView != null && currentView.equals(gameView.getViewLayout())) {
                gameView.playMusic();
                gameView.setupKeyHandlers();
                gameView.updateDisplay();
            }

            FadeTransition fadeFromBlack = new FadeTransition(Duration.seconds(0.5), dimmerOverlay);
            fadeFromBlack.setFromValue(1.0);
            fadeFromBlack.setToValue(0.0);
            fadeFromBlack.setOnFinished(e -> dimmerOverlay.setMouseTransparent(true));
            fadeFromBlack.play();
        });
        fadeToBlack.play();
    }

    public void showMainMenu() {
        mainMenuView = new MainMenuView(this);
        transitionToView(mainMenuView.getViewLayout());
    }

    public void startGame(String playerName, boolean showWelcomeDialog) {
        gameView = new GameView(this, playerName);
        transitionToView(gameView.getViewLayout());

        if (showWelcomeDialog){
            Platform.runLater(gameView::showWelcomeDialog);
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}