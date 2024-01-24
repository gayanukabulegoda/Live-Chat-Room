package lk.ijse.liveChatRoom.util;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class TransitionUtil {

    public static void ScaleTransition(Object... arg) {
        Node node = (Node) arg[0];
        if (node instanceof Pane) {
            ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.2), node);
            scaleTransition.setFromX(0);
            scaleTransition.setToX(1);
            scaleTransition.setFromY(0);
            scaleTransition.setToY(1);
            scaleTransition.play();
        }
        else if (node instanceof ImageView) {
            double scale = (double) arg[1];
            node.setScaleX(scale);
            node.setScaleY(scale);
        }
    }

    /*public static void TranslateTransition(Pane pane, String startFrom) {
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.2), pane);
        if (startFrom.equals("right")) {
            translateTransition.setFromX(pane.getWidth()); // Start from the right of the pane
        }
        else if (startFrom.equals("left")) {
            translateTransition.setFromX(-pane.getWidth()); // Start from the left of the pane
        }
        translateTransition.setToX(0);
        translateTransition.play();
    }*/
}
