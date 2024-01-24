package lk.ijse.liveChatRoom.util;

import javafx.animation.ScaleTransition;
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
}
