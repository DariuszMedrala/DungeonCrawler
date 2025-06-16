package org.example.dungeonCrawler.view;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class AnimationUtil {

    public static void playFadeInTransition(Node node, Runnable onFinished) {
        node.setOpacity(0.0);
        node.setTranslateY(20);

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(400), node);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);

        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(400), node);
        translateTransition.setFromY(20);
        translateTransition.setToY(0);

        ParallelTransition parallelTransition = new ParallelTransition(fadeTransition, translateTransition);

        if (onFinished != null) {
            parallelTransition.setOnFinished(e -> onFinished.run());
        }

        parallelTransition.play();
    }
}