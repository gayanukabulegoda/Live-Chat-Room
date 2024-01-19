package lk.ijse.liveChatRoom.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import lk.ijse.liveChatRoom.util.Navigation;

import java.io.IOException;

public class LoginFormController {

    @FXML
    private TextField txtUserName;
    public static String username;

    @FXML
    void btnExitOnAction(ActionEvent event) {
        Navigation.exit();
    }

    @FXML
    void signInBtnOnAction(ActionEvent event) throws IOException {
        username = txtUserName.getText();
        Navigation.switchNavigation("chatRoomForm.fxml",event);
    }

    @FXML
    void txtUserNameOnAction(ActionEvent event) throws IOException {
        signInBtnOnAction(event);
    }
}
