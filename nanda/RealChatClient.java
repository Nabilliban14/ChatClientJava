package nanda;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class RealChatClient extends Application {

    private TextArea sentMsgs;
    GridPane gpLayout = new GridPane();
    GridPane gp1 = new GridPane();
    GridPane gp2 = new GridPane();
    GridPane gp3 = new GridPane();

    boolean loggedIn = false;

    static private PrintWriter writer;
    static private BufferedReader reader;

    private TextArea makeMsg;
    private TextArea usernameInput;
    
    public void run() throws Exception {
        setUpNetworking();
    }

    private void setUpNetworking() throws Exception {
        @SuppressWarnings("resource")
        Socket sock = new Socket("127.0.0.1", 4242);
        InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
        reader = new BufferedReader(streamReader);
        writer = new PrintWriter(sock.getOutputStream());
        System.out.println("networking established");
        Thread readerThread = new Thread(new RealChatClient.IncomingReader());
        readerThread.start();
    }

    private void chatClient() {

        Stage chatClientStage = new Stage();
        Scene scene = new Scene (gpLayout, 800, 650);

        gpLayout.setStyle("-fx-background-color: linear-gradient(from 25% 25% to 100% 100%, #1FF9FF, #432a33)");

        Insets insets = new Insets(30, 30, 30, 30);
        //bp.setPadding(insets);

        sentMsgs = new TextArea();
        sentMsgs.setEditable(false);
        sentMsgs.setPrefHeight(575);
        sentMsgs.setPrefWidth(580);
        //sentMsgs.setPrefHeight();

        makeMsg = new TextArea();
        makeMsg.setPrefHeight(75);
        makeMsg.setMaxWidth(480);
        makeMsg.wrapTextProperty().set(true);
        //makeMsg.setStyle("Arial");
        makeMsg.setEditable(true);

        Button sendMsg = new Button("Send");
        sendMsg.setPrefHeight(50);
        sendMsg.setPrefWidth(100);

        sendMsg.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                writer.println(makeMsg.getText());
                writer.flush();
                makeMsg.setText("");
                makeMsg.requestFocus();
            }
        });



        gpLayout.add(sentMsgs,0,0);
        gpLayout.add(makeMsg,0,575);
        gpLayout.add(sendMsg, 1, 575);

        chatClientStage.setTitle("The Best Chat Client Ever");
        chatClientStage.setScene(scene);
        chatClientStage.show();
    }

    public void start(Stage primaryStage) throws Exception{
        run();

        GridPane gpLogin = new GridPane();
        gpLogin.setPadding( new Insets(10, 10, 10, 10));
        gpLogin.setVgap(10);
        gpLogin.setHgap(10);
        
        Label username = new Label("Username:");
        GridPane.setConstraints(username, 8 , 12);
        
        TextField usernameInput = new TextField();
        GridPane.setConstraints(usernameInput, 9, 12);
        
        Label password = new Label("Password:");
        GridPane.setConstraints(password, 8 , 13);
        
        TextField passwordInput = new TextField();
        passwordInput.setPromptText("password");
        GridPane.setConstraints(passwordInput, 9 , 13);

        Button startButton = new Button("Log In");
        GridPane.setConstraints(startButton, 9 , 14);

        gpLogin.getChildren().addAll(username, usernameInput, password, passwordInput, startButton);

        Scene sc = new Scene(gpLogin, 400, 400);
        
        startButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                primaryStage.close();
                chatClient();
            }
        });

        primaryStage.setTitle("The Best Chat Client Ever");
        primaryStage.setScene(sc);
        primaryStage.show();

    }
/*
    public static void main(String[] args) {
        try {
            new RealChatClient().run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/
    class IncomingReader implements Runnable {
        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {

                    sentMsgs.appendText(message + "\n");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
