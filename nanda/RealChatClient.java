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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

public class RealChatClient extends Application {

    private TextArea sentMsgs;
    GridPane gpLayout = new GridPane();
    GridPane gp1 = new GridPane();
    GridPane gp2 = new GridPane();
    GridPane gp3 = new GridPane();
	PrintWriter upwriter = null;

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
        logIn();

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
    
    private void logIn() {
    	 Stage logInStage = new Stage();
    	
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
         
         Label register = new Label("New? Click register!");
         GridPane.setConstraints(register, 9 , 19);

         Button registerButton = new Button("Register");
         GridPane.setConstraints(registerButton,  9,  20);
         
         gpLogin.getChildren().addAll(username, usernameInput, password, passwordInput, register, startButton, registerButton);

         Scene sc = new Scene(gpLogin, 400, 400);
         
         startButton.setOnAction(new EventHandler<ActionEvent>() {
             @Override
             public void handle(ActionEvent event) {
                 logInStage.close();
                 chatClient();
             }
         });

         registerButton.setOnAction(new EventHandler<ActionEvent>() {
             @Override
             public void handle(ActionEvent event) {
                 logInStage.close();
                 register();
             }
         });        
         logInStage.setTitle("The Best Chat Client Ever");
         logInStage.setScene(sc);
         logInStage.show();
    	
    }

    
    private void register() {
    	Stage regStage = new Stage();
        GridPane gpReg = new GridPane();
        gpReg.setPadding( new Insets(10, 10, 10, 10));
        gpReg.setVgap(10);
        gpReg.setHgap(6);
        
        Label username = new Label("Enter desired username:");
        GridPane.setConstraints(username, 8 , 12);
        
        TextField usernameInput = new TextField();
        GridPane.setConstraints(usernameInput, 9, 12);
        
        Label password = new Label("Enter desired password:");
        GridPane.setConstraints(password, 8 , 13);
        
        TextField passwordInput = new TextField();
        GridPane.setConstraints(passwordInput, 9 , 13);
        
        Label confirmPassword = new Label("Confirm password:");
        GridPane.setConstraints(confirmPassword, 8 , 14);
        
        TextField confirmPasswordInput = new TextField();
        GridPane.setConstraints(confirmPasswordInput, 9 , 14);

        Button regButton = new Button("Register");
        GridPane.setConstraints(regButton, 9 , 15);
        
        gpReg.getChildren().addAll(username, usernameInput, password, passwordInput, confirmPassword,confirmPasswordInput, regButton);

        Scene sc = new Scene(gpReg, 400, 400);
        
        regStage.setTitle("Registering");
        regStage.setScene(sc);
        regStage.show();
        
        String userpswd = usernameInput.getText() + "//" + passwordInput.getText();
        System.out.println(userpswd);
        File file = new File("C:\\Users\\junmin777\\eclipse-workspace\\nanda\\src\\nanda\\userpswd.txt");
        try {
			 upwriter = new PrintWriter(file, "UTF-8");
		} catch (FileNotFoundException e) {
			System.out.println("file not found");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			System.out.println("idk what to do lmao");
			e.printStackTrace();
		}

        regButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	upwriter.println(userpswd);
            	regStage.close();
                logIn();
            }
        });   
        
        
        
    	
    }
}
