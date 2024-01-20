package lk.ijse.liveChatRoom.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
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
                    String receivedFullMsg = bufferedReader.readLine();
                    String[] splitTheMsg = receivedFullMsg.split(":");
                    String username =  splitTheMsg[0];
                    String message = splitTheMsg[1];

                    //finding the arrived message type
                    String firstCharacter = "";
                    if (username.length() > 3) {
                        firstCharacter = username.substring(0, 3);
                        System.out.println("First Char: "+firstCharacter);
                    }
                    if (firstCharacter.equalsIgnoreCase("img")) {
                        String[] splitMessage = receivedFullMsg.split("img");
                        String path = splitMessage[1];
                        System.out.println("Message Path :"+path);

                        File file = new File(path);
                        Image image = new Image(file.toURI().toString());

                        ImageView imageView =  new ImageView(image);
                        imageView.setFitHeight(300);
                        imageView.setFitWidth(300);

                        HBox hBox = new HBox(10);
                        hBox.setAlignment(Pos.BOTTOM_RIGHT);

                        if (lblUsername.getText().equalsIgnoreCase(username)) {
                            hBox.setAlignment(Pos.TOP_RIGHT);
                            hBox.getChildren().add(imageView);

                            Text text = new Text(": Me ");
                            hBox.getChildren().add(text);
                        }
                        else {
                            vBox.setAlignment(Pos.TOP_LEFT);
                            hBox.setAlignment(Pos.TOP_LEFT);

                            Text text = new Text(" "+username+" :");
                            hBox.getChildren().add(text);
                            hBox.getChildren().add(imageView);
                        }

                        Platform.runLater(() ->
                                vBox.getChildren().addAll(hBox));
                    }
                    else {
                        if (lblUsername.getText().equalsIgnoreCase(username)) {
                            sendMessage(message);
                        } else {
                            receivedMessage(receivedFullMsg);
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                //throw new RuntimeException(e);
            }
        }).start();
    }

    private void sendMessage(String messageToSend) {
        if (!messageToSend.isEmpty()) {
            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER_RIGHT);
            hBox.setPadding(new Insets(5,5,5,10));

            Text text = new Text(messageToSend);
            text.setFont(Font.font(17));
            TextFlow textFlow = new TextFlow(text);

            textFlow.setStyle(
                    "-fx-color: rgb(239,242,255);" +
                    "-fx-background-color: rgb(15,125,242);" +
                    "-fx-background-radius: 20px"
            );

            textFlow.setPadding(new Insets(5,10,5,10));
            text.setFill(Color.color(0.934, 0.945, 0.996));

            hBox.getChildren().add(textFlow);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    vBox.getChildren().add(hBox);
                }
            });
        }
    }

    private void receivedMessage(String receivedMsg) {
        String[] name = receivedMsg.split(":");
        String username = name[0];

        if (!lblUsername.getText().equals(username)) {
            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER_LEFT);
            hBox.setPadding(new Insets(5,5,5,10));

            Text text = new Text(receivedMsg);
            text.setFont(Font.font(17));
            TextFlow textFlow = new TextFlow(text);

            textFlow.setStyle(
                    "-fx-background-color: rgb(233,233,235);" +
                    "-fx-background-radius: 20px"
            );

            textFlow.setPadding(new Insets(5,10,5,10));

            hBox.getChildren().add(textFlow);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    vBox.getChildren().add(hBox);
                }
            });
        }
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
        //dataOutputStream.writeUTF(txtMessage.getText());
       // txtAreaChatRoom.appendText(LoginFormController.username+": "+txtMessage.getText());
        String messageToSend = txtMessage.getText();
        printWriter.println(lblUsername.getText()+": "+messageToSend);
        txtMessage.setEditable(true);
        txtMessage.clear();
        //dataOutputStream.flush();
    }

    @FXML
    void txtMessageOnAction(ActionEvent event) throws IOException {
        btnSendOnAction(event);
    }
}
