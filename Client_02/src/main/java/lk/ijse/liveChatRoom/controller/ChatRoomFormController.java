package lk.ijse.liveChatRoom.controller;

import com.jfoenix.controls.JFXButton;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import lk.ijse.liveChatRoom.util.NavigationUtil;
import lk.ijse.liveChatRoom.util.TransitionUtil;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Random;
import java.util.regex.Pattern;

public class ChatRoomFormController {

    @FXML
    private Circle circleCloseIconInner;

    @FXML
    private Circle circleCloseIconOuter;

    @FXML
    private Circle circleMinimizeIconInner;

    @FXML
    private Circle circleMinimizeIconOuter;

    @FXML
    private Circle circleOnlineIndicator;

    @FXML
    private Circle circleDarkTheme;

    @FXML
    private Circle circleLightTheme;

    @FXML
    private ImageView imgBackIcon;

    @FXML
    private ImageView imgCloseIcon;

    @FXML
    private ImageView imgMinimizeIcon;

    @FXML
    private ImageView imgDarkTheme;

    @FXML
    private ImageView imgLightTheme;

    @FXML
    private ImageView imgPlayTechLogo;

    @FXML
    private ImageView imgChatRoomBackground;

    @FXML
    private ImageView imgSendBtn;

    @FXML
    private ImageView imgAttachIcon;

    @FXML
    private ImageView imgEmojiIcon;

    @FXML
    private ImageView imgAnxiousFaceWithSweatEmoji;

    @FXML
    private ImageView imgCryingFaceEmoji;

    @FXML
    private ImageView imgDisappointedFaceEmoji;

    @FXML
    private ImageView imgExpressionlessFaceEmoji;

    @FXML
    private ImageView imgFaceWithTearsOfJoyEmoji;

    @FXML
    private ImageView imgFaceWithTongueEmoji;

    @FXML
    private ImageView imgFearfulFaceEmoji;

    @FXML
    private ImageView imgGrinningFaceEmoji;

    @FXML
    private ImageView imgHushedFaceEmoji;

    @FXML
    private ImageView imgLoudlyCryingEmoji;

    @FXML
    private ImageView imgLoveHeartsEyesEmoji;

    @FXML
    private ImageView imgSleepingFaceEmoji;

    @FXML
    private ImageView imgSmilingFaceEmoji;

    @FXML
    private ImageView imgSmilingFaceWithHaloEmoji;

    @FXML
    private ImageView imgSmilingFaceWithSunglassesEmoji;

    @FXML
    private ImageView imgThumbUpEmoji;

    @FXML
    private ImageView imgThumbsDownEmoji;

    @FXML
    private ImageView imgWinkingFaceEmoji;

    @FXML
    private JFXButton backBtn;

    @FXML
    private Pane themeChangePane;

    @FXML
    private Pane txtMessagePane;

    @FXML
    private Pane messageAreaPane;

    @FXML
    private Pane sendBtnInnerPane;

    @FXML
    private Pane sendBtnOuterPane;

    @FXML
    private Pane emojiPane;

    @FXML
    private Pane exitBtnPane;

    @FXML
    private Pane backBtnPane;

    @FXML
    private Pane minimizeBtnPane;

    @FXML
    private Label lblUsername;

    @FXML
    private Label lblMessageTextAlert;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TextField txtMessage;

    @FXML
    private Text txtBack;

    @FXML
    private Text txtClose;

    @FXML
    private Text txtMinimize;

    @FXML
    private Text txtLogoChatRoom;

    @FXML
    private VBox vBox;

    private Socket remoteSocket;
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

        // Set file path & create a file to save the received image
        int randomNumber = new Random().nextInt(1000000);
        String fileName = "ReceivedImage_" + randomNumber + ".png";
        File directoryPath = new File("Client_02/src/main/resources/receivedImages");
        File receivedImageFile = new File(directoryPath, fileName);

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

            if (circleLightTheme.isVisible()) {
                innerHBox.setStyle(
                        "-fx-background-color: rgb(15,125,242);" +
                                "-fx-background-radius: 20px"
                );
            } else {
                innerHBox.setStyle(
                        "-fx-background-color: rgb(8, 78, 153);" +
                                "-fx-background-radius: 20px"
                );
            }

            innerHBox.getChildren().addAll(imageBox,timeBox);

            hBox.getChildren().add(innerHBox);
            hBox.setAlignment(Pos.TOP_RIGHT);
            hBox.setPadding(new Insets(5,5,5,10)); //set space between images
        }

        //if received Image selected by another client
        else {
            if (circleLightTheme.isVisible()) {
                innerHBox.setStyle(
                        "-fx-background-color: rgb(233,233,235);" +
                                "-fx-background-radius: 20px"
                );
            } else {
                innerHBox.setStyle(
                        "-fx-background-color: #A9A9A9;" +
                                "-fx-background-radius: 20px"
                );
            }

            vBox.setAlignment(Pos.TOP_LEFT);
            hBox.setAlignment(Pos.TOP_LEFT);

            Text text = new Text(" " + finalName + " :");
            text.setFont(Font.font(12.5));

            innerHBox.getChildren().addAll(text,imageBox,timeBox);
            hBox.getChildren().add(innerHBox);
            hBox.setPadding(new Insets(5,5,5,10));
        }

        Platform.runLater(() -> {
                vBox.getChildren().add(hBox);
                scrollPane.layout(); // Ensure layout is updated
                scrollPane.setVvalue(1.0); // Scroll down to the bottom
        });
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

            if (circleLightTheme.isVisible()) {
                innerHBox.setStyle(
                        "-fx-background-color: rgb(15,125,242);" +
                                "-fx-background-radius: 20px"
                );
            } else {
                innerHBox.setStyle(
                        "-fx-background-color: rgb(8, 78, 153);" +
                                "-fx-background-radius: 20px"
                );
            }

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
                    scrollPane.layout(); // Ensure layout is updated
                    scrollPane.setVvalue(1.0); // Scroll down to the bottom
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

            if (circleLightTheme.isVisible()) {
                innerHBox.setStyle(
                        "-fx-background-color: rgb(233,233,235);" +
                                "-fx-background-radius: 20px"
                );
            } else {
                innerHBox.setStyle(
                        "-fx-background-color: #A9A9A9;" +
                                "-fx-background-radius: 20px"
                );
            }

            textFlow.setPadding(new Insets(5,10,5,10));

            // Add time
            HBox timeBox = setTime();

            innerHBox.getChildren().addAll(textFlow, timeBox);
            hBox.getChildren().add(innerHBox);

            Platform.runLater(() -> {
                vBox.getChildren().add(hBox);
                scrollPane.layout();
                scrollPane.setVvalue(1.0);
            });
        }
    }

    @FXML
    void btnAttachOnAction(ActionEvent event) throws IOException {
        lblMessageTextAlert.setText(" ");
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
            imgAttachIcon.setImage(new Image("assests/icon/attachmentIcon.png"));
        }
    }

    @FXML
    void btnAttachOnMouseEntered(MouseEvent event) {
        imgAttachIcon.setImage(new Image("assests/icon/attachmentIconBlue.png"));
    }

    @FXML
    void btnAttachOnMouseExited(MouseEvent event) {
        imgAttachIcon.setImage(new Image("assests/icon/attachmentIcon.png"));
    }

    @FXML
    void btnEmojiOnAction(ActionEvent event) {
        lblMessageTextAlert.setText(" ");
        emojiPane.setVisible(!emojiPane.isVisible());
        if (emojiPane.isVisible()) {
            imgEmojiIcon.setImage(new Image("assests/icon/emojiIconBlue.png"));
        } else {
            imgEmojiIcon.setImage(new Image("assests/icon/emojiIcon.png"));
        }
    }

    @FXML
    void btnEmojiOnMouseEntered(MouseEvent event) {
        imgEmojiIcon.setImage(new Image("assests/icon/emojiIconBlue.png"));
    }

    @FXML
    void btnEmojiOnMouseExited(MouseEvent event) {
        if (!emojiPane.isVisible()) {
            imgEmojiIcon.setImage(new Image("assests/icon/emojiIcon.png"));
        }
    }

    @FXML
    void btnBackOnAction(ActionEvent event) throws IOException {
        printWriter.println("lef" + lblUsername.getText() + ": leaving");
        NavigationUtil.switchNavigation("loginForm.fxml",event);
    }

    @FXML
    void btnExitOnAction(ActionEvent event) {
        printWriter.println("lef" + lblUsername.getText() + ": leaving");
        NavigationUtil.exit();
    }

    @FXML
    void btnMinimizeOnAction(ActionEvent event) {
        NavigationUtil.minimize();
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
    void btnBackOnMouseEntered(MouseEvent event) {
        backBtnPane.setVisible(true);
        TransitionUtil.ScaleTransition(backBtnPane);
        if (circleLightTheme.isVisible()) {
            backBtn.setStyle(
                    "-fx-border-color: white;" +
                            "-fx-border-width: 3px;" +
                            "-fx-border-radius: 50px");
        } else {
            backBtn.setStyle(
                    "-fx-border-color: #CBCBCB;" +
                            "-fx-border-width: 3px;" +
                            "-fx-border-radius: 50px");
        }
    }

    @FXML
    void btnBackOnMouseExited(MouseEvent event) {
        backBtn.setStyle("-fx-border-color: transparent");
        backBtnPane.setVisible(false);
    }

    @FXML
    void btnSendOnAction(ActionEvent event) {
        if (validateMessage()){
            if (file != null) {
                printWriter.println("img"+lblUsername.getText() +":"+ base64Image);
                file = null;
            } else {
                printWriter.println(lblUsername.getText() + ": " + txtMessage.getText());
            }
            txtMessage.setEditable(true);
            emojiPane.setVisible(false);
            imgEmojiIcon.setImage(new Image("assests/icon/emojiIcon.png"));
            txtMessage.clear();
        }
        else {
            lblMessageTextAlert.setText("Message should have at least a character!!");
        }
    }

    private boolean validateMessage() {
        return Pattern.matches("^.+$", txtMessage.getText());
    }

    @FXML
    void btnSendOnMouseEntered(MouseEvent event) {
        TransitionUtil.ScaleTransition(imgSendBtn, 1.1);
        if (circleLightTheme.isVisible()) {
            sendBtnInnerPane.setStyle(
                    "-fx-background-color: #018DE7;" +
                            "-fx-background-radius: 25px");
        } else {
            sendBtnInnerPane.setStyle(
                    "-fx-background-color: #001727;" +
                            "-fx-background-radius: 25px");
        }
    }

    @FXML
    void btnSendOnMouseExited(MouseEvent event) {
        TransitionUtil.ScaleTransition(imgSendBtn, 1.0);
        if (circleLightTheme.isVisible()) {
            sendBtnInnerPane.setStyle(
                    "-fx-background-color: #009CFF;" +
                            "-fx-background-radius: 25px");
        } else {
            sendBtnInnerPane.setStyle(
                    "-fx-background-color: #001F3F;" +
                            "-fx-background-radius: 25px");
        }
    }

    @FXML
    void txtMessageOnAction(ActionEvent event) {
        btnSendOnAction(event);
    }

    @FXML
    void txtMessageOnKeyPressed(KeyEvent event) {
        if (!txtMessage.getText().isEmpty()) {
            lblMessageTextAlert.setText(" ");
        }
    }

    @FXML
    void imgAnxiousFaceWithSweatEmojiOnMouseClicked(MouseEvent event) {
        txtMessage.appendText("\uD83D\uDE30"); //😰
        finalizeEmojiSelection(event);
    }

    @FXML
    void imgCryingFaceEmojiOnMouseClicked(MouseEvent event) {
        txtMessage.appendText("\uD83D\uDE22"); //😢
        finalizeEmojiSelection(event);
    }

    @FXML
    void imgDisappointedFaceEmojiOnMouseClicked(MouseEvent event) {
        txtMessage.appendText("\uD83D\uDE1E"); //😞
        finalizeEmojiSelection(event);
    }

    @FXML
    void imgFaceWithTearsOfJoyEmojiOnMouseClicked(MouseEvent event) {
        txtMessage.appendText("\uD83D\uDE02"); //😂
        finalizeEmojiSelection(event);
    }

    @FXML
    void imgFaceWithTongueEmojiOnMouseClicked(MouseEvent event) {
        txtMessage.appendText("\uD83D\uDE1B"); //😛
        finalizeEmojiSelection(event);
    }

    @FXML
    void imgFearfulFaceEmojiOnMouseClicked(MouseEvent event) {
        txtMessage.appendText("\uD83D\uDE28"); //😦
        finalizeEmojiSelection(event);
    }

    @FXML
    void imgGrinningFaceEmojiOnMouseClicked(MouseEvent event) {
        txtMessage.appendText("\uD83D\uDE00"); //😀
        finalizeEmojiSelection(event);
    }

    @FXML
    void imgHushedFaceEmojiOnMouseClicked(MouseEvent event) {
        txtMessage.appendText("\uD83D\uDE2F"); //😯
        finalizeEmojiSelection(event);
    }

    @FXML
    void imgLoudlyCryingEmojiOnMouseClicked(MouseEvent event) {
        txtMessage.appendText("\uD83D\uDE2D"); //😭
        finalizeEmojiSelection(event);
    }

    @FXML
    void imgLoveHeartsEyesEmojiOnMouseClicked(MouseEvent event) {
        txtMessage.appendText("\uD83D\uDE0D"); //😍
        finalizeEmojiSelection(event);
    }

    @FXML
    void imgExpressionlessFaceEmojiOnMouseClicked(MouseEvent event) {
        txtMessage.appendText("\uD83D\uDE11"); //😑
        finalizeEmojiSelection(event);
    }

    @FXML
    void imgSleepingFaceEmojiOnMouseClicked(MouseEvent event) {
        txtMessage.appendText("\uD83D\uDE34"); //😴
        finalizeEmojiSelection(event);
    }

    @FXML
    void imgSmilingFaceWithSunglassesEmojiOnMouseClicked(MouseEvent event) {
        txtMessage.appendText("\uD83D\uDE0E"); //😎
        finalizeEmojiSelection(event);
    }

    @FXML
    void imgSmilingFaceEmojiOnMouseClicked(MouseEvent event) {
        txtMessage.appendText("\uD83D\uDE0A"); //😊
        finalizeEmojiSelection(event);
    }

    @FXML
    void imgSmilingFaceWithHaloEmojiOnMouseClicked(MouseEvent event) {
        txtMessage.appendText("\uD83D\uDE07"); //😇
        finalizeEmojiSelection(event);
    }

    @FXML
    void imgThumbsDownEmojiOnMouseClicked(MouseEvent event) {
        txtMessage.appendText("\uD83D\uDC4E"); //👎
        finalizeEmojiSelection(event);
    }

    @FXML
    void imgThumbUpEmojiOnMouseClicked(MouseEvent event) {
        txtMessage.appendText("\uD83D\uDC4D"); //👍
        finalizeEmojiSelection(event);
    }

    @FXML
    void imgWinkingFaceEmojiOnMouseClicked(MouseEvent event) {
        txtMessage.appendText("\uD83D\uDE09"); //😉
        finalizeEmojiSelection(event);
    }

    private void finalizeEmojiSelection(MouseEvent event) {
        emojiPane.setVisible(false);
        btnEmojiOnMouseExited(event);
        txtMessage.requestFocus();
    }

    @FXML
    void imgAnxiousFaceWithSweatEmojiOnMouseEntered(MouseEvent event) {
        TransitionUtil.ScaleTransition(imgAnxiousFaceWithSweatEmoji, 1.2);
    }

    @FXML
    void imgAnxiousFaceWithSweatEmojiOnMouseExited(MouseEvent event) {
        TransitionUtil.ScaleTransition(imgAnxiousFaceWithSweatEmoji, 1.0);
    }

    @FXML
    void imgCryingFaceEmojiOnMouseEntered(MouseEvent event) {
        TransitionUtil.ScaleTransition(imgCryingFaceEmoji, 1.2);
    }

    @FXML
    void imgCryingFaceEmojiOnMouseExited(MouseEvent event) {
        TransitionUtil.ScaleTransition(imgCryingFaceEmoji, 1.0);
    }

    @FXML
    void imgDisappointedFaceEmojiOnMouseEntered(MouseEvent event) {
        TransitionUtil.ScaleTransition(imgDisappointedFaceEmoji, 1.2);
    }

    @FXML
    void imgDisappointedFaceEmojiOnMouseExited(MouseEvent event) {
        TransitionUtil.ScaleTransition(imgDisappointedFaceEmoji, 1.0);
    }

    @FXML
    void imgExpressionlessFaceEmojiOnMouseEntered(MouseEvent event) {
        TransitionUtil.ScaleTransition(imgExpressionlessFaceEmoji, 1.2);
    }

    @FXML
    void imgExpressionlessFaceEmojiOnMouseExited(MouseEvent event) {
        TransitionUtil.ScaleTransition(imgExpressionlessFaceEmoji, 1.0);
    }

    @FXML
    void imgFaceWithTearsOfJoyEmojiOnMouseEntered(MouseEvent event) {
        TransitionUtil.ScaleTransition(imgFaceWithTearsOfJoyEmoji, 1.2);
    }

    @FXML
    void imgFaceWithTearsOfJoyEmojiOnMouseExited(MouseEvent event) {
        TransitionUtil.ScaleTransition(imgFaceWithTearsOfJoyEmoji, 1.0);
    }

    @FXML
    void imgFaceWithTongueEmojiOnMouseEntered(MouseEvent event) {
        TransitionUtil.ScaleTransition(imgFaceWithTongueEmoji, 1.2);
    }

    @FXML
    void imgFaceWithTongueEmojiOnMouseExited(MouseEvent event) {
        TransitionUtil.ScaleTransition(imgFaceWithTongueEmoji, 1.0);
    }

    @FXML
    void imgFearfulFaceEmojiOnMouseEntered(MouseEvent event) {
        TransitionUtil.ScaleTransition(imgFearfulFaceEmoji, 1.2);
    }

    @FXML
    void imgFearfulFaceEmojiOnMouseExited(MouseEvent event) {
        TransitionUtil.ScaleTransition(imgFearfulFaceEmoji, 1.0);
    }

    @FXML
    void imgGrinningFaceEmojiOnMouseEntered(MouseEvent event) {
        TransitionUtil.ScaleTransition(imgGrinningFaceEmoji, 1.2);
    }

    @FXML
    void imgGrinningFaceEmojiOnMouseExited(MouseEvent event) {
        TransitionUtil.ScaleTransition(imgGrinningFaceEmoji, 1.0);
    }

    @FXML
    void imgHushedFaceEmojiOnMouseEntered(MouseEvent event) {
        TransitionUtil.ScaleTransition(imgHushedFaceEmoji, 1.2);
    }

    @FXML
    void imgHushedFaceEmojiOnMouseExited(MouseEvent event) {
        TransitionUtil.ScaleTransition(imgHushedFaceEmoji, 1.0);
    }

    @FXML
    void imgLoudlyCryingEmojiOnMouseEntered(MouseEvent event) {
        TransitionUtil.ScaleTransition(imgLoudlyCryingEmoji, 1.2);
    }

    @FXML
    void imgLoudlyCryingEmojiOnMouseExited(MouseEvent event) {
        TransitionUtil.ScaleTransition(imgLoudlyCryingEmoji, 1.0);
    }

    @FXML
    void imgLoveHeartsEyesEmojiOnMouseEntered(MouseEvent event) {
        TransitionUtil.ScaleTransition(imgLoveHeartsEyesEmoji, 1.2);
    }

    @FXML
    void imgLoveHeartsEyesEmojiOnMouseExited(MouseEvent event) {
        TransitionUtil.ScaleTransition(imgLoveHeartsEyesEmoji, 1.0);
    }

    @FXML
    void imgSleepingFaceEmojiOnMouseEntered(MouseEvent event) {
        TransitionUtil.ScaleTransition(imgSleepingFaceEmoji, 1.2);
    }

    @FXML
    void imgSleepingFaceEmojiOnMouseExited(MouseEvent event) {
        TransitionUtil.ScaleTransition(imgSleepingFaceEmoji, 1.0);
    }

    @FXML
    void imgSmilingFaceEmojiOnMouseEntered(MouseEvent event) {
        TransitionUtil.ScaleTransition(imgSmilingFaceEmoji, 1.2);
    }

    @FXML
    void imgSmilingFaceEmojiOnMouseExited(MouseEvent event) {
        TransitionUtil.ScaleTransition(imgSmilingFaceEmoji, 1.0);
    }

    @FXML
    void imgSmilingFaceWithHaloEmojiOnMouseEntered(MouseEvent event) {
        TransitionUtil.ScaleTransition(imgSmilingFaceWithHaloEmoji, 1.2);
    }

    @FXML
    void imgSmilingFaceWithHaloEmojiOnMouseExited(MouseEvent event) {
        TransitionUtil.ScaleTransition(imgSmilingFaceWithHaloEmoji, 1.0);
    }

    @FXML
    void imgSmilingFaceWithSunglassesEmojiOnMouseEntered(MouseEvent event) {
        TransitionUtil.ScaleTransition(imgSmilingFaceWithSunglassesEmoji, 1.2);
    }

    @FXML
    void imgSmilingFaceWithSunglassesEmojiOnMouseExited(MouseEvent event) {
        TransitionUtil.ScaleTransition(imgSmilingFaceWithSunglassesEmoji, 1.0);
    }

    @FXML
    void imgThumbUpEmojiOnMouseEntered(MouseEvent event) {
        TransitionUtil.ScaleTransition(imgThumbUpEmoji, 1.2);
    }

    @FXML
    void imgThumbUpEmojiOnMouseExited(MouseEvent event) {
        TransitionUtil.ScaleTransition(imgThumbUpEmoji, 1.0);
    }

    @FXML
    void imgThumbsDownEmojiOnMouseEntered(MouseEvent event) {
        TransitionUtil.ScaleTransition(imgThumbsDownEmoji, 1.2);
    }

    @FXML
    void imgThumbsDownEmojiOnMouseExited(MouseEvent event) {
        TransitionUtil.ScaleTransition(imgThumbsDownEmoji, 1.0);
    }

    @FXML
    void imgWinkingFaceEmojiOnMouseEntered(MouseEvent event) {
        TransitionUtil.ScaleTransition(imgWinkingFaceEmoji, 1.2);
    }

    @FXML
    void imgWinkingFaceEmojiOnMouseExited(MouseEvent event) {
        TransitionUtil.ScaleTransition(imgWinkingFaceEmoji, 1.0);
    }

    private void setLightTheme() {
        imgChatRoomBackground.setImage(new Image("assests/image/chatRoomBackground.png"));
        imgPlayTechLogo.setImage(new Image("assests/image/playTechLogoChatRoom.png"));
        imgDarkTheme.setImage(new Image("assests/icon/nightThemeIcon.png"));
        imgLightTheme.setImage(new Image("assests/icon/lightThemeIcon.png"));
        imgSendBtn.setImage(new Image("assests/icon/sendBtnIcon.png"));
        imgEmojiIcon.setImage(new Image("assests/icon/emojiIcon.png"));
        imgCloseIcon.setImage(new Image("assests/icon/closeIcon.png"));
        imgMinimizeIcon.setImage(new Image("assests/icon/minimizeIcon.png"));
        imgBackIcon.setImage(new Image("assests/icon/backBtnIcon.png"));

        // Apply styles directly to vertical scroll bar
        scrollPane.lookup(".scroll-bar:vertical .thumb").
                setStyle("-fx-background-color: #C0C0C0; -fx-background-radius: 5em;");
        scrollPane.lookup(".scroll-bar:vertical .track").
                setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        scrollPane.lookup(".increment-button").
                setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        scrollPane.lookup(".decrement-button").
                setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        themeChangePane.setStyle("-fx-background-color: white;" +
                "-fx-background-radius: 25px");

        txtLogoChatRoom.setStyle("-fx-fill: white");
        lblUsername.setStyle("-fx-text-fill: white");
        circleOnlineIndicator.setStyle("-fx-fill:  #009CFF;" +
                "-fx-stroke: white;" +
                "-fx-stroke-width: 3px");

        vBox.setStyle("-fx-background-color: white");
        scrollPane.setStyle("-fx-background-color: white");
        messageAreaPane.setStyle("-fx-background-color: white;" +
                "-fx-background-radius: 16px");

        txtMessage.setStyle("-fx-background-color: white");
        txtMessagePane.setStyle("-fx-background-color: white;" +
                "-fx-background-radius: 25px");

        sendBtnInnerPane.setStyle("-fx-background-color: #009CFF;" +
                "-fx-background-radius: 25px");
        sendBtnOuterPane.setStyle("-fx-background-color: white;" +
                "-fx-background-radius: 25px");

        emojiPane.setStyle("-fx-background-color: white;" +
                "-fx-border-color:  #009CFF;" +
                "-fx-background-radius: 18px;" +
                "-fx-border-radius: 18px");

        circleCloseIconOuter.setStyle("-fx-fill: white");
        circleCloseIconInner.setStyle("-fx-fill: #009CFF;" +
                "-fx-stroke: #009CFF");

        circleMinimizeIconOuter.setStyle("-fx-fill: white");
        circleMinimizeIconInner.setStyle("-fx-fill: #009CFF;" +
                "-fx-stroke: #009CFF");

        backBtnPane.setStyle("-fx-background-color: white;" +
                "-fx-background-radius: 20px");
        exitBtnPane.setStyle("-fx-background-color: white;" +
                "-fx-background-radius: 20px");
        minimizeBtnPane.setStyle("-fx-background-color: white;" +
                "-fx-background-radius: 20px");

        txtBack.setStyle("-fx-fill: #009CFF");
        txtClose.setStyle("-fx-fill: #009CFF");
        txtMinimize.setStyle("-fx-fill: #009CFF");

        emojiPane.setVisible(false);
        circleDarkTheme.setVisible(false);
        circleLightTheme.setVisible(true);
    }

    private void setDarkTheme() {
        imgChatRoomBackground.setImage(new Image("assests/image/chatRoomBackgroundDark.png"));
        imgPlayTechLogo.setImage(new Image("assests/image/playTechLogoChatRoomDark.png"));
        imgDarkTheme.setImage(new Image("assests/icon/nightThemeIconSilver.png"));
        imgLightTheme.setImage(new Image("assests/icon/lightThemeIconSilver.png"));
        imgSendBtn.setImage(new Image("assests/icon/sendBtnIconSilver.png"));
        imgEmojiIcon.setImage(new Image("assests/icon/emojiIcon.png"));
        imgCloseIcon.setImage(new Image("assests/icon/closeIconSilver.png"));
        imgMinimizeIcon.setImage(new Image("assests/icon/minimizeIconSilver.png"));
        imgBackIcon.setImage(new Image("assests/icon/backBtnIconSilver.png"));

        // Apply styles directly to vertical scroll bar
        scrollPane.lookup(".scroll-bar:vertical .thumb").
                setStyle("-fx-background-color: #A9A9A9; -fx-background-radius: 5em;");
        scrollPane.lookup(".scroll-bar:vertical .track").
                setStyle("-fx-background-color: #001F3F; -fx-border-color: #001F3F;");
        scrollPane.lookup(".increment-button").
                setStyle("-fx-background-color: #001F3F; -fx-border-color: #001F3F;");
        scrollPane.lookup(".decrement-button").
                setStyle("-fx-background-color: #001F3F; -fx-border-color: #001F3F;");

        themeChangePane.setStyle("-fx-background-color: #A9A9A9;" +
                "-fx-background-radius: 25px");

        txtLogoChatRoom.setStyle("-fx-fill: #A9A9A9");
        lblUsername.setStyle("-fx-text-fill: #cbcbcb");
        circleOnlineIndicator.setStyle("-fx-fill:  #001F3F;" +
                "-fx-stroke: #cbcbcb;" +
                "-fx-stroke-width: 3px");

        vBox.setStyle("-fx-background-color: #001F3F");
        scrollPane.setStyle("-fx-background-color: #001F3F;" +
                "-fx-control-inner-background: #001F3F;" +
                "-fx-background: #001F3F;");

        messageAreaPane.setStyle("-fx-background-color: #001F3F;" +
                "-fx-background-radius: 16px");

        txtMessage.setStyle("-fx-background-color: #001F3F;" +
                "-fx-control-inner-background: #F0F0F0;" +
                "-fx-background: #001F3F;" +
                "-fx-text-fill: #F0F0F0;");

        txtMessagePane.setStyle("-fx-background-color: #001F3F;" +
                "-fx-background-radius: 25px");

        sendBtnInnerPane.setStyle("-fx-background-color: #001F3F;" +
                "-fx-background-radius: 25px");
        sendBtnOuterPane.setStyle("-fx-background-color: #A9A9A9;" +
                "-fx-background-radius: 25px");

        emojiPane.setStyle("-fx-background-color: #001F3F;" +
                "-fx-border-color:  #A9A9A9;" +
                "-fx-background-radius: 18px;" +
                "-fx-border-radius: 18px");

        circleCloseIconOuter.setStyle("-fx-fill: #A9A9A9");
        circleCloseIconInner.setStyle("-fx-fill: #001F3F;" +
                "-fx-stroke: #001F3F");

        circleMinimizeIconOuter.setStyle("-fx-fill: #A9A9A9");
        circleMinimizeIconInner.setStyle("-fx-fill: #001F3F;" +
                "-fx-stroke: #001F3F");

        backBtnPane.setStyle("-fx-background-color: #CBCBCB;" +
                "-fx-background-radius: 20px");
        exitBtnPane.setStyle("-fx-background-color: #CBCBCB;" +
                "-fx-background-radius: 20px");
        minimizeBtnPane.setStyle("-fx-background-color: #CBCBCB;" +
                "-fx-background-radius: 20px");

        txtBack.setStyle("-fx-fill: #001F3F");
        txtClose.setStyle("-fx-fill: #001F3F");
        txtMinimize.setStyle("-fx-fill: #001F3F");

        emojiPane.setVisible(false);
        circleLightTheme.setVisible(false);
        circleDarkTheme.setVisible(true);
    }

    private void changeTheme() {
        if (circleLightTheme.isVisible()) {
            setDarkTheme();
        } else {
            setLightTheme();
        }
    }

    @FXML
    void themeChangeBtnOnAction(ActionEvent event) {
        changeTheme();
    }
}
