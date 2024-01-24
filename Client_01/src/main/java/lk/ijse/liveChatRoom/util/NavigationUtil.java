package lk.ijse.liveChatRoom.util;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lk.ijse.liveChatRoom.AppInitializer;

import java.io.IOException;

public class NavigationUtil {

    private static Stage stage;

    public static void switchNavigation(String link, ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(NavigationUtil.class.getResource("/view/" + link));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public static void exit() {
        System.exit(0);
    }

    public static void minimize() {
        if (stage != null) {
            stage.setIconified(true);
        }
        else if (AppInitializer.loginStage != null) {
            AppInitializer.loginStage.setIconified(true);
        }
    }
}
