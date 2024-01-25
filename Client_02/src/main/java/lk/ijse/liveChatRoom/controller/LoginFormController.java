package lk.ijse.liveChatRoom.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import lk.ijse.liveChatRoom.util.NavigationUtil;
import lk.ijse.liveChatRoom.util.TransitionUtil;

import java.io.IOException;
import java.util.regex.Pattern;

public class LoginFormController {

    @FXML
    private TextField txtUserName;

    @FXML
    private Label lblUserNameAlert;

    @FXML
    private Pane signInBtnPane;

    @FXML
    private Pane exitBtnPane;

    @FXML
    private Pane minimizeBtnPane;

    public static String username;

    @FXML
    void signInBtnOnAction(ActionEvent event) throws IOException {
        if (validateUsername()) {
            username = txtUserName.getText();
            NavigationUtil.switchNavigation("chatRoomForm.fxml", event);
        }
        else {
            lblUserNameAlert.setText("Invalid! Username should have at least\n" +
                    "FOUR characters (except Symbols & Spaces)");
        }
    }

    @FXML
    void signInBtnOnMouseEntered(MouseEvent event) {
        signInBtnPane.setStyle(
                "-fx-background-color: #018DE7;" +
                "-fx-background-radius: 15px");
    }

    @FXML
    void signInBtnOnMouseExited(MouseEvent event) {
        signInBtnPane.setStyle(
                "-fx-background-color: #009CFF;" +
                "-fx-background-radius: 15px");
    }

    @FXML
    void txtUserNameOnAction(ActionEvent event) throws IOException {
        signInBtnOnAction(event);
    }

    @FXML
    void btnMinimizeOnAction(ActionEvent event) {
        NavigationUtil.minimize();
    }

    @FXML
    void btnExitOnAction(ActionEvent event) {
        NavigationUtil.exit();
    }

    @FXML
    void btnExitOnMouseEntered(MouseEvent event) {
        exitBtnPane.setVisible(true);
        TransitionUtil.ScaleTransition(exitBtnPane);
    }

    @FXML
    void btnExitOnMouseExited(MouseEvent event) {
        exitBtnPane.setVisible(false);
    }

    @FXML
    void btnMinimizeOnMouseEntered(MouseEvent event) {
        minimizeBtnPane.setVisible(true);
        TransitionUtil.ScaleTransition(minimizeBtnPane);
    }

    @FXML
    void btnMinimizeOnMouseExited(MouseEvent event) {
        minimizeBtnPane.setVisible(false);
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
