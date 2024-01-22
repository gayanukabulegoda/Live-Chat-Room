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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import lk.ijse.liveChatRoom.util.Navigation;

import java.io.*;
import java.net.Socket;

public class ChatRoomFormController {

    @FXML
    private Pane emojiPane;

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
                System.out.println("1");
                printWriter = new PrintWriter(remoteSocket.getOutputStream(),true);
                System.out.println("3");
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

                    //if an Image arrived
                    if (firstCharacter.equalsIgnoreCase("img")) {
                        String[] splitMessage = receivedFullMsg.split(":");
                        String path = splitMessage[1];
                        System.out.println("Message Path :"+path);

                        File file = new File(path);
                        Image image = new Image(file.toURI().toString());

                        ImageView imageView =  new ImageView(image);
                        imageView.setFitHeight(300);
                        imageView.setFitWidth(300);

                        HBox hBox = new HBox(10);
                        hBox.setAlignment(Pos.BOTTOM_RIGHT);

                        //to remove prefix "img"
                        String[] name = username.split("img");
                        String finalName = name[1];

                        //if received Image selected by me
                        if (lblUsername.getText().equalsIgnoreCase(finalName)) {
                            hBox.setAlignment(Pos.TOP_RIGHT);
                            hBox.getChildren().add(imageView);
                            hBox.setPadding(new Insets(5,5,5,10)); //set space between images

                            Text text = new Text(": Me ");
                            hBox.getChildren().add(text);
                        }

                        //if received Image selected by another client
                        else {
                            vBox.setAlignment(Pos.TOP_LEFT);
                            hBox.setAlignment(Pos.TOP_LEFT);

                            Text text = new Text(" "+finalName+" :");
                            hBox.getChildren().add(text);
                            hBox.getChildren().add(imageView);
                            hBox.setPadding(new Insets(5,5,5,10));
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
            hBox.setPadding(new Insets(5,10,5,10));

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
    void btnAttachOnAction(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an Image!!");

        FileChooser.ExtensionFilter imageFilter =
                new FileChooser.ExtensionFilter("Image Files","*.jpg","*.jpeg","*.png","*.gif");

        fileChooser.getExtensionFilters().add(imageFilter);
        file = fileChooser.showOpenDialog(txtMessage.getScene().getWindow());

        if (file != null) {
            txtMessage.setText("01 Image Selected");
            txtMessage.setEditable(false);
            btnSendOnAction(event);
        }
    }

    @FXML
    void btnEmojiOnAction(ActionEvent event) {
        emojiPane.setVisible(!emojiPane.isVisible());
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
        /*String messageToSend = txtMessage.getText();
        printWriter.println(lblUsername.getText()+": "+messageToSend);
        txtMessage.setEditable(true);
        emojiPane.setVisible(false);
        txtMessage.clear();*/
        //dataOutputStream.flush();
        if (!txtMessage.getText().isEmpty()){
            if (file != null) {
                printWriter.println("img"+lblUsername.getText()+":"+file.getPath());
                file = null;
            } else {
                printWriter.println(lblUsername.getText() + ": " + txtMessage.getText());
            }
            txtMessage.setEditable(true);
            emojiPane.setVisible(false);
            txtMessage.clear();
        }
    }

    @FXML
    void txtMessageOnAction(ActionEvent event) throws IOException {
        btnSendOnAction(event);
    }

    @FXML
    void imgAnxiousFaceWithSweatEmojiOnMouseClicked(MouseEvent event) {
        txtMessage.appendText("\uD83D\uDE30"); //😰
        emojiPane.setVisible(false);
        txtMessage.requestFocus();
    }

    @FXML
    void imgCryingFaceEmojiOnMouseClicked(MouseEvent event) {
        txtMessage.appendText("\uD83D\uDE22"); //😢
        emojiPane.setVisible(false);
        txtMessage.requestFocus();
    }

    @FXML
    void imgDisappointedFaceEmojiOnMouseClicked(MouseEvent event) {
        txtMessage.appendText("\uD83D\uDE1E"); //😞
        emojiPane.setVisible(false);
        txtMessage.requestFocus();
    }

    @FXML
    void imgFaceWithTearsOfJoyEmojiOnMouseClicked(MouseEvent event) {
        txtMessage.appendText("\uD83D\uDE02"); //😂
        emojiPane.setVisible(false);
        txtMessage.requestFocus();
    }

    @FXML
    void imgFaceWithTongueEmojiOnMouseClicked(MouseEvent event) {
        txtMessage.appendText("\uD83D\uDE1B"); //😛
        emojiPane.setVisible(false);
        txtMessage.requestFocus();
    }

    @FXML
    void imgFearfulFaceEmojiOnMouseClicked(MouseEvent event) {
        txtMessage.appendText("\uD83D\uDE28"); //😦
        emojiPane.setVisible(false);
        txtMessage.requestFocus();
    }

    @FXML
    void imgGrinningFaceEmojiOnMouseClicked(MouseEvent event) {
        txtMessage.appendText("\uD83D\uDE00"); //😀
        emojiPane.setVisible(false);
        txtMessage.requestFocus();
    }

    @FXML
    void imgHushedFaceEmojiOnMouseClicked(MouseEvent event) {
        txtMessage.appendText("\uD83D\uDE2F"); //😯
        emojiPane.setVisible(false);
        txtMessage.requestFocus();
    }

    @FXML
    void imgLoudlyCryingEmojiOnMouseClicked(MouseEvent event) {
        txtMessage.appendText("\uD83D\uDE2D"); //😭
        emojiPane.setVisible(false);
        txtMessage.requestFocus();
    }

    @FXML
    void imgLoveHeartsEyesEmojiOnMouseClicked(MouseEvent event) {
        txtMessage.appendText("\uD83D\uDE0D"); //😍
        emojiPane.setVisible(false);
        txtMessage.requestFocus();
    }

    @FXML
    void imgExpressionlessFaceEmojiOnMouseClicked(MouseEvent event) {
        txtMessage.appendText("\uD83D\uDE11"); //😑
        emojiPane.setVisible(false);
        txtMessage.requestFocus();
    }

    @FXML
    void imgSleepingFaceEmojiOnMouseClicked(MouseEvent event) {
        txtMessage.appendText("\uD83D\uDE34"); //😴
        emojiPane.setVisible(false);
        txtMessage.requestFocus();
    }

    @FXML
    void imgSmilingFaceWithSunglassesEmojiOnMouseClicked(MouseEvent event) {
        txtMessage.appendText("\uD83D\uDE0E"); //😎
        emojiPane.setVisible(false);
        txtMessage.requestFocus();
    }

    @FXML
    void imgSmilingFaceEmojiOnMouseClicked(MouseEvent event) {
        txtMessage.appendText("\uD83D\uDE0A"); //😊
        emojiPane.setVisible(false);
        txtMessage.requestFocus();
    }

    @FXML
    void imgSmilingFaceWithHaloEmojiOnMouseClicked(MouseEvent event) {
        txtMessage.appendText("\uD83D\uDE07"); //😇
        emojiPane.setVisible(false);
        txtMessage.requestFocus();
    }

    @FXML
    void imgThumbsDownEmojiOnMouseClicked(MouseEvent event) {
        txtMessage.appendText("\uD83D\uDC4E"); //👎
        emojiPane.setVisible(false);
        txtMessage.requestFocus();
    }

    @FXML
    void imgThumbUpEmojiOnMouseClicked(MouseEvent event) {
        txtMessage.appendText("\uD83D\uDC4D"); //👍
        emojiPane.setVisible(false);
        txtMessage.requestFocus();
    }

    @FXML
    void imgWinkingFaceEmojiOnMouseClicked(MouseEvent event) {
        txtMessage.appendText("\uD83D\uDE09"); //😉
        emojiPane.setVisible(false);
        txtMessage.requestFocus();
    }
}
