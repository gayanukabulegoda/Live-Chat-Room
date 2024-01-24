package lk.ijse.liveChatRoom.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lk.ijse.liveChatRoom.util.Navigation;

import java.io.IOException;
import java.util.regex.Pattern;

public class LoginFormController {

    @FXML
    private TextField txtUserName;

    @FXML
    private Label lblUserNameAlert;

    public static String username;

    @FXML
    void btnExitOnAction(ActionEvent event) {
        Navigation.exit();
    }

    @FXML
    void signInBtnOnAction(ActionEvent event) throws IOException {
        if (validateUsername()) {
            username = txtUserName.getText();
            Navigation.switchNavigation("chatRoomForm.fxml", event);
        }
        else {
            lblUserNameAlert.setText("Invalid! Username should have at least\n" +
                    "FOUR characters (except symbols)");
        }
    }

    @FXML
    void txtUserNameOnAction(ActionEvent event) throws IOException {
        signInBtnOnAction(event);
    }

    @FXML
    void txtUserNameOnKeyPressed(KeyEvent event) {
        if ((event.getCode() == KeyCode.BACK_SPACE) || (event.getCode() == KeyCode.DELETE)) {
            lblUserNameAlert.setText(" ");
        }
    }

    private boolean validateUsername() {
        return Pattern.matches("^[a-zA-Z0-9]{4,}$", txtUserName.getText());
    }
}
