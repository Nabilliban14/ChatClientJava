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
    ListView <String> history = new ListView<>();

    private ClientInfo user;

    private TextArea sentMsgs;
    GridPane gpLogin = new GridPane();
    GridPane gpOutput = new GridPane();
    GridPane gpFriends = new GridPane();
    GridPane gpLayout = new GridPane();
    GridPane gpTypeMsg = new GridPane();
    GridPane gp2 = new GridPane();
    GridPane gp3 = new GridPane();



    static private PrintWriter writer;
    static private BufferedReader reader;

    private TabPane tabPane = new TabPane();

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
        sentMsgs.setPrefHeight(577);
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

        history.getItems().addAll("Nerd Squad", "ECE homies", "Sports club");
        history.setMinHeight(550);

        Tab friendsList = new Tab("Friends List");
        friendsList.setClosable(false);
        friendsList.setContent(names);
        friendsList.setStyle("-fx-font-size: 13pt;");
        Tab chatHistory = new Tab("Group Chats");
        chatHistory.setClosable(false);
        chatHistory.setContent(history);
        chatHistory.setStyle("-fx-font-size: 13pt;");

        tabPane.getTabs().addAll(friendsList,chatHistory);

        gpLayout.add(gpOutput,0,0);
        gpLayout.add(gpFriends, 1, 0);
        gpLayout.add(gpTypeMsg, 0, 575);

        gpOutput.add(sentMsgs, 0, 0);
        gpFriends.addColumn(0, tabPane);
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
