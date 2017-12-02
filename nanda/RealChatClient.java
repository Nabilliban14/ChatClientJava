package nanda;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class RealChatClient extends Application {

	private String username;
	private String password;
	
    private TextArea sentMsgs;
    BorderPane bp = new BorderPane();
    GridPane gp1 = new GridPane();
    GridPane gp2 = new GridPane();
    static private PrintWriter writer;
    static private BufferedReader reader;
    private RealChatClient rcc;

    private TextArea makeMsg;

    public void run() throws Exception {
        setUpNetworking();
    }

    private void setUpNetworking() throws Exception {
        @SuppressWarnings("resource")
        Socket sock = new Socket("10.148.216.65", 4242);
        InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
        reader = new BufferedReader(streamReader);
        writer = new PrintWriter(sock.getOutputStream());
        System.out.println("networking established");
        Thread readerThread = new Thread(new RealChatClient.IncomingReader());
        readerThread.start();
    }

    public void start(Stage primaryStage) throws Exception{

        run();

        Scene scene = new Scene (bp, 800, 650);
        bp.setRight(gp1);
        bp.setLeft(gp2);

        bp.setStyle("-fx-background-color: linear-gradient(from 25% 25% to 100% 100%, #1FF9FF, #432a33)");

        Insets insets = new Insets(30, 30, 30, 30);
        //bp.setPadding(insets);
        gp1.setPadding(insets);

        sentMsgs = new TextArea();
        sentMsgs.setEditable(false);
        sentMsgs.setMinHeight(600);
        //sentMsgs.setPrefHeight();

        makeMsg = new TextArea();
        makeMsg.setMinHeight(100);
        makeMsg.wrapTextProperty().set(true);
        //makeMsg.setStyle("Arial");
        makeMsg.setEditable(true);

        Button sendMsg = new Button("Send");

        sendMsg.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                writer.println(makeMsg.getText());
                writer.flush();
                makeMsg.setText("");
                makeMsg.requestFocus();
            }
        });



        gp2.add(sentMsgs,0,0);
        gp2.add(makeMsg,0,601);
        gp2.add(sendMsg, 400, 550);

        primaryStage.setTitle("The Best Chat Client Ever");
        primaryStage.setScene(scene);
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
