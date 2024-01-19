package lk.ijse.liveChatRoom.controller;

import com.jfoenix.controls.JFXTextArea;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import lk.ijse.liveChatRoom.util.Navigation;

import java.io.*;
import java.net.Socket;

public class ChatRoomFormController {

    @FXML
    private Label lblUsername;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TextField txtMessage;

    @FXML
    private VBox vBox;

    Socket remoteSocket;
    DataOutputStream dataOutputStream;
    String message = "";
    private File file;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;

    public void initialize() {
        lblUsername.setText(LoginFormController.username);
        new Thread(()->{
            try {
                remoteSocket = new Socket("localhost", 3400);
                System.out.println("Client: "+lblUsername.getText()+" Connected!");

                bufferedReader = new BufferedReader(new InputStreamReader(remoteSocket.getInputStream()));
                printWriter = new PrintWriter(remoteSocket.getOutputStream(),true);
                //dataOutputStream = new DataOutputStream(remoteSocket.getOutputStream());
                //printWriter.println(lblUsername.getText()+" joining...");

                while (true) {
                    //reading the response
                    String receivedMsg = bufferedReader.readLine();
                    String[] splitTheMsg = receivedMsg.split(":");
                    String username =  splitTheMsg[0];
                    String message = splitTheMsg[1];

                    //finding the arrived message type

                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @FXML
    void btnAttachOnAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an Image!!");

        FileChooser.ExtensionFilter imageFilter =
                new FileChooser.ExtensionFilter("Image Files","*.jpg","*.jpeg","*.png","*.gif");

        fileChooser.getExtensionFilters().add(imageFilter);
        file = fileChooser.showOpenDialog(txtMessage.getScene().getWindow());

        if (file != null) {
            txtMessage.setText("01 Image Selected");
            txtMessage.setEditable(false);
        }
    }

    @FXML
    void btnEmojiOnAction(ActionEvent event) {

    }

    @FXML
    void btnBackOnAction(ActionEvent event) throws IOException {
        Navigation.switchNavigation("loginForm.fxml",event);
    }

    @FXML
    void btnExitOnAction(ActionEvent event) {
        Navigation.exit();
    }

    @FXML
    void btnSendOnAction(ActionEvent event) throws IOException {
        dataOutputStream.writeUTF(txtMessage.getText());

       // txtAreaChatRoom.appendText(LoginFormController.username+": "+txtMessage.getText());
        txtMessage.setEditable(true);
        txtMessage.clear();
        dataOutputStream.flush();
    }

    @FXML
    void txtMessageOnAction(ActionEvent event) throws IOException {
        btnSendOnAction(event);
    }
}
