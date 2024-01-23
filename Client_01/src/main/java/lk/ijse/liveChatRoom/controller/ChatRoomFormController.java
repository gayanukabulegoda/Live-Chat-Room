package lk.ijse.liveChatRoom.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;

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
    private File file;
    private String base64Image;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    private String finalName;

    public void initialize() {
        lblUsername.setText(LoginFormController.username);
        new Thread(()->{
            try {
                remoteSocket = new Socket("localhost", 3400);
                System.out.println("Client: "+lblUsername.getText()+" Connected!");

                bufferedReader = new BufferedReader(new InputStreamReader(remoteSocket.getInputStream()));
                printWriter = new PrintWriter(remoteSocket.getOutputStream(),true); //like dataOutputStream

                printWriter.println("joi"+lblUsername.getText()+": joining...");

                while (true) {
                    //reading the response
                    String receivedFullMsg = bufferedReader.readLine();
                    String[] splitTheMsg = receivedFullMsg.split(":");
                    String username =  splitTheMsg[0];
                    String message = splitTheMsg[1];

                    //finding the arrived message type
                    String firstCharacter = findingTheArrivedMessageType(username);

                    //remove the username prefixes (if available)
                    removePrefixes(username, firstCharacter);

                    //if an Image arrived
                    if (firstCharacter.equalsIgnoreCase("img")) {
                        setImage(receivedFullMsg);
                    }

                    // display new client who join the chat
                    else if (firstCharacter.equalsIgnoreCase("joi")){
                        HBox hBox = new HBox();
                        if (lblUsername.getText().equals(finalName)) {
                            Label joinText = new Label("You have joined the Chat");
                            joinText.getStyleClass().add("join-text");

                            hBox.getChildren().add(joinText);
                            hBox.setAlignment(Pos.CENTER);
                            hBox.setPadding(new Insets(5,5,5,10));
                        }
                        else {
                            Label joinText = new Label(finalName+" has joined the Chat");
                            joinText.getStyleClass().add("join-text");
                            hBox.getChildren().add(joinText);
                            hBox.setAlignment(Pos.CENTER);
                            hBox.setPadding(new Insets(5,5,5,10));
                        }

                        Platform.runLater(() ->
                                vBox.getChildren().addAll(hBox));
                    }
                    // display the client who left the chat
                    else if (firstCharacter.equalsIgnoreCase("lef")) {
                        Label leftText = new Label(finalName + " has left the Chat");
                        leftText.getStyleClass().add("left-text");

                        HBox hBox = new HBox();
                        hBox.getChildren().add(leftText);
                        hBox.setAlignment(Pos.CENTER);
                        hBox.setPadding(new Insets(5, 5, 5, 10));

                        Platform.runLater(() ->
                                vBox.getChildren().add(hBox));
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
            }
        }).start();
    }

    private HBox setTime() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime now = LocalDateTime.now();

        Text time = new Text(dateTimeFormatter.format(now));
        time.setFont(Font.font(10));
        time.getStyleClass().add("time-text");

        HBox timeBox = new HBox();
        timeBox.getChildren().add(time);
        timeBox.setAlignment(Pos.BOTTOM_RIGHT);

        return timeBox;
    }

    private void removePrefixes(String username, String firstCharacter) {
        if (firstCharacter.equalsIgnoreCase("img")) {
            String[] name = username.split("img"); //to remove prefix "img"
            finalName = name[1];
        }
        else if (firstCharacter.equalsIgnoreCase("joi")) {
            String[] name = username.split("joi"); //to remove prefix "joi"
            finalName = name[1];
        }
        else if (firstCharacter.equalsIgnoreCase("lef")) {
            String[] name = username.split("lef"); //to remove prefix "lef"
            finalName = name[1];
        }
    }

    private String findingTheArrivedMessageType(String username) {
        String firstCharacter = "";
        if (username.length() > 3) {
            firstCharacter = username.substring(0, 3);
            System.out.println("First Char: " + firstCharacter);
        }
        return firstCharacter;
    }

    private File decodeReceivedImage(String path) throws IOException {
        byte[] imageBytes = Base64.getDecoder().decode(path); // Decode Base64 image data

        // Create a file to save the received image
        String fileName = "Received Image File";
        File receivedImageFile = new File(fileName);

        // Write the image bytes to the file
        try (FileOutputStream fos = new FileOutputStream(receivedImageFile)) {
            fos.write(imageBytes);
        }
        return receivedImageFile;
    }

    private void setImage(String receivedFullMsg) throws IOException {
        String[] splitMessage = receivedFullMsg.split(":");
        String path = splitMessage[1];
        System.out.println("Message Path :" + path);

        File receivedImageFile = decodeReceivedImage(path);

        Image receivedImage = new Image(receivedImageFile.toURI().toString());

        ImageView imageView =  new ImageView(receivedImage);
        imageView.setFitHeight(300);
        imageView.setFitWidth(300);

        HBox imageBox = new HBox(imageView);
        imageBox.setPadding(new Insets(5));

        HBox hBox = new HBox(10);
        hBox.setAlignment(Pos.BOTTOM_RIGHT);

        // Created an inner HBox to contain both the imageHBox and timeHBox
        HBox innerHBox = new HBox();
        innerHBox.setPadding(new Insets(5,10,5,10));

        // Add time
        HBox timeBox = setTime();

        //if received Image selected by me
        if (lblUsername.getText().equalsIgnoreCase(finalName)) {
            // Set color for the Text within the timeBox
            for (Node node : timeBox.getChildren()) {
                if (node instanceof Text) {
                    ((Text) node).setFill(Color.color(0.934, 0.945, 0.996));
                }
            }

            innerHBox.setStyle(
                    "-fx-background-color: rgb(15,125,242);" +
                            "-fx-background-radius: 20px"
            );

            innerHBox.getChildren().addAll(imageBox,timeBox);

            hBox.getChildren().add(innerHBox);
            hBox.setAlignment(Pos.TOP_RIGHT);
            hBox.setPadding(new Insets(5,5,5,10)); //set space between images
        }

        //if received Image selected by another client
        else {
            innerHBox.setStyle(
                    "-fx-background-color: rgb(233,233,235);" +
                            "-fx-background-radius: 20px"
            );

            vBox.setAlignment(Pos.TOP_LEFT);
            hBox.setAlignment(Pos.TOP_LEFT);

            Text text = new Text(" " + finalName + " :");
            text.setFont(Font.font(12.5));

            innerHBox.getChildren().addAll(text,imageBox,timeBox);
            hBox.getChildren().add(innerHBox);
            hBox.setPadding(new Insets(5,5,5,10));
        }

        Platform.runLater(() ->
                vBox.getChildren().addAll(hBox));
    }

    private void sendMessage(String messageToSend) {
        if (!messageToSend.isEmpty()) {
            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER_RIGHT);
            hBox.setPadding(new Insets(5,10,5,10));

            // Created an inner HBox to contain both the message and time
            HBox innerHBox = new HBox();
            innerHBox.setPadding(new Insets(2,10,2.5,10));

            Text text = new Text(messageToSend);
            text.setFont(Font.font(17));
            TextFlow textFlow = new TextFlow(text);

            innerHBox.setStyle(
                    "-fx-background-color: rgb(15,125,242);" +
                    "-fx-background-radius: 20px"
            );

            textFlow.setPadding(new Insets(5,10,5,10));
            text.setFill(Color.color(0.934, 0.945, 0.996));

            // Add time
            HBox timeBox = setTime();

            // Set color for the Text within the timeBox
            for (Node node : timeBox.getChildren()) {
                if (node instanceof Text) {
                    ((Text) node).setFill(Color.color(0.934, 0.945, 0.996));
                }
            }

            innerHBox.getChildren().addAll(textFlow, timeBox);
            hBox.getChildren().add(innerHBox);

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
        String message = name[1];

        if (!lblUsername.getText().equals(username)) {
            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER_LEFT);
            hBox.setPadding(new Insets(5,5,5,10));

            // Created an inner HBox to contain both the message and time
            HBox innerHBox = new HBox();
            innerHBox.setPadding(new Insets(2,10,2.5,5));

            Text txtUsername = new Text(username + ": ");
            txtUsername.setFont(Font.font(12.5));

            Text txtMessage = new Text(message);
            txtMessage.setFont(Font.font(17));

            TextFlow textFlow = new TextFlow(txtUsername, txtMessage);

            innerHBox.setStyle(
                    "-fx-background-color: rgb(233,233,235);" +
                    "-fx-background-radius: 20px"
            );

            textFlow.setPadding(new Insets(5,10,5,10));

            // Add time
            HBox timeBox = setTime();

            innerHBox.getChildren().addAll(textFlow, timeBox);
            hBox.getChildren().add(innerHBox);

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

            // Read image file into byte array
            byte[] imageBytes = Files.readAllBytes(file.toPath());
            base64Image = Base64.getEncoder().encodeToString(imageBytes);

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
        printWriter.println("lef" + lblUsername.getText() + ": leaving");
        Navigation.exit();
    }

    @FXML
    void btnSendOnAction(ActionEvent event) {
        if (!txtMessage.getText().isEmpty()){
            if (file != null) {
                printWriter.println("img"+lblUsername.getText() +":"+ base64Image);
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
