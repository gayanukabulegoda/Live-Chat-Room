package lk.ijse.liveChatRoom.controller;

import com.jfoenix.controls.JFXTextArea;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import lk.ijse.liveChatRoom.util.Navigation;

import java.io.IOException;

public class ChatRoomFormController {

    @FXML
    private JFXTextArea txtAreaChatRoom;

    @FXML
    private TextField txtMessage;

    @FXML
    void btnAttachOnAction(ActionEvent event) {

    }

    @FXML
    void btnBackOnAction(ActionEvent event) throws IOException {
        Navigation.switchNavigation("loginForm.fxml",event);
    }

    @FXML
    void btnEmojiOnAction(ActionEvent event) {

    }

    @FXML
    void btnExitOnAction(ActionEvent event) {
        Navigation.exit();
    }

    @FXML
    void btnSendOnAction(ActionEvent event) {

    }

    @FXML
    void txtMessageOnAction(ActionEvent event) {

    }
}
