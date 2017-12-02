package nanda;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class RealChatClient extends Application {

    ListView <String> names = new ListView<>();
    private TextArea sentMsgs;
    GridPane gpLogin = new GridPane();
    GridPane gpOutput = new GridPane();
    GridPane gpFriends = new GridPane();
    GridPane gpLayout = new GridPane();
    GridPane gpTypeMsg = new GridPane();
    GridPane gp2 = new GridPane();
    GridPane gp3 = new GridPane();


    boolean loggedIn = false;

    static private PrintWriter writer;
    static private BufferedReader reader;

    private MenuBar friendsAndHistory = new MenuBar();
    private TextArea makeMsg;

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
        sendMsg.setPrefHeight(75);
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

        names.getItems().addAll("Nabil", "Jun Min", "Friend #3", "Friend #3", "Friend #3", "Friend #3", "Friend #3", "Friend #3", "Friend #3", "Friend #3", "Friend #3", "Friend #3", "Friend #3", "Friend #3", "Friend #3", "Friend #3", "Friend #3", "Friend #3", "Friend #3");
        names.setMinHeight(550);

        Menu friendsList = new Menu("Friends List");
        Menu chatHistory = new Menu("Chat History");
        friendsAndHistory.getMenus().addAll(friendsList, chatHistory);

        gpLayout.add(gpOutput,0,0);
        gpLayout.add(gpFriends, 1, 0);
        gpLayout.add(gpTypeMsg, 0, 575);

        gpOutput.add(sentMsgs, 0, 0);
        gpFriends.addColumn(0, friendsAndHistory, names);
        gpTypeMsg.addRow(0, makeMsg, sendMsg);
        //gpLayout.add(sendMsg, 480, 575);

        chatClientStage.setTitle("The Best Chat Client Ever");
        chatClientStage.setScene(scene);
        chatClientStage.show();


    }

    public void start(Stage primaryStage) throws Exception{
        run();

        Scene sc = new Scene(gpLogin, 800, 650);

        Button start = new Button("Click to start!");

        gpLogin.add(start, 400, 300);

        start.setOnAction(new EventHandler<ActionEvent>() {
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
