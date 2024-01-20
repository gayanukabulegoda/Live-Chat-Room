package lk.ijse.liveChatRoom;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AppInitializer extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(
                new Scene(
                        FXMLLoader.load(getClass().getResource("/view/loginForm.fxml"))));

        stage.initStyle(StageStyle.DECORATED);
        stage.centerOnScreen();
        stage.show();
    }
}
