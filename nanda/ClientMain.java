//Nabil and JunMin

package nanda;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;




public class ClientMain extends Application {
	

    ListView <String> names = new ListView<>();
    ListView <String> history = new ListView<>();
    ArrayList<String> friendsList = new ArrayList<>();

    private ClientInfo user;
    private String currentlyTalkingTo = null;

    private String chatHistory = "";
    private String myUsername = null;

    private TextField friendName = new TextField();
    private Button send = new Button ("Add");
    GridPane gpRequest = new GridPane();
    ListView<String> Incomingrequests = new ListView<>();

    boolean gotFriendsListFlag = false;

    private TextArea sentMsgs;
    GridPane gpLogin = new GridPane();
    GridPane gpFriendRequests = new GridPane();
    GridPane gpOutput = new GridPane();
    GridPane gpFriends = new GridPane();
    GridPane gpLayout = new GridPane();
    GridPane gpTypeMsg = new GridPane();
    GridPane gp2 = new GridPane();
    GridPane gp3 = new GridPane();
    GridPane gp1 = new GridPane();
	String newLine = System.getProperty("line.separator");

    
	File file = new File("src\\nanda\\userpswd.txt");
	FileReader upfr = null;
    BufferedReader upbr = null;
    
    Media notification = new Media("file:///C://Users//junmin777//eclipse-workspace//nanda//src//nanda//notification.mp3");
    Media intro = new Media("file:///C://Users//junmin777//eclipse-workspace//nanda//src//nanda//intro.mp3");

    MediaPlayer notificationPlayer; 
    MediaPlayer introPlayer;
    static private PrintWriter writer;
    static private BufferedReader reader;

    private TabPane tabPane = new TabPane();
    private TabPane tpRequests =  new TabPane();

    private TextArea makeMsg;

	FileWriter upwriter = null;
	
	public static void main(String[] args) {
			launch(args);
	}
	
	@Override
    public  void start(Stage primaryStage) throws Exception{
    	run();
        introPlayer = new MediaPlayer(intro);
        introPlayer.setAutoPlay(true);
    	logIn();

    }
	
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
        Thread readerThread = new Thread(new ClientMain.IncomingReader());
        readerThread.start();
    }

    private void chatClient() {
    	System.out.println("129");
        Stage chatClientStage = new Stage();
        System.out.println("130");
        Scene scene = new Scene (gpLayout, 800, 650);

        gpLayout.setStyle("-fx-background-color: linear-gradient(from 25% 25% to 100% 100%, #1FF9FF, #432a33)");

        Insets insets = new Insets(30, 30, 30, 30);
        //bp.setPadding(insets);

        sentMsgs = new TextArea();
        sentMsgs.setEditable(false);
        sentMsgs.setPrefHeight(577);
        sentMsgs.setPrefWidth(580);
        sentMsgs.setStyle("-fx-base: #2356A9");
        //sentMsgs.setPrefHeight();

        makeMsg = new TextArea();
        makeMsg.setPrefHeight(75);
        makeMsg.setMaxWidth(480);
        makeMsg.wrapTextProperty().set(true);
        //makeMsg.setStyle("Arial");
        makeMsg.setEditable(true);

        Button sendMsg = new Button("Send");
        sendMsg.setStyle("-fx-base: #1156A9");
        sendMsg.setPrefHeight(75);
        sendMsg.setPrefWidth(100);

        sendMsg.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String msg = "A"; //indicates a sent msg

                msg += myUsername + "|";

                msg += currentlyTalkingTo + "|";


                msg += "_" + makeMsg.getText();

                writer.println(msg);
                writer.flush();
                makeMsg.setText("");
                makeMsg.requestFocus();
            }
        });

        while (!gotFriendsListFlag){};
        names.getItems().addAll(friendsList);
        names.setMinHeight(550);

        names.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (oldValue == null) {
                    writer.println("C" + myUsername + "|" + "_" + newValue + "_" + chatHistory);
                    writer.flush();
                }
                else {
                    writer.println("C" + myUsername + "|" + oldValue + "|" + "_" + newValue + "_" + chatHistory);
                    writer.flush();
                }

                currentlyTalkingTo = newValue;
                chatHistory = "";
            }
        });

        send.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String name = "";
                name += friendName.getText();
                if (name != "") {
                    writer.println("D" + myUsername + "|" + name);
                    writer.flush();
                }
            }
        });

        Incomingrequests.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                String msg = "F" + myUsername + "|" + newValue;
                writer.print(msg);
                writer.flush();
            }
        });
        history.getItems().addAll("Nerd Squad", "ECE homies", "Sports club");
        history.setMinHeight(550);

        Tab friends = new Tab("Friends List");
        friends.setClosable(false);
        friends.setContent(names);
        friends.setStyle("-fx-font-size: 13pt;");
        friends.setStyle("-fx-base: #FCCD5F");
        Tab chatHistory = new Tab("Group Chats");
        chatHistory.setClosable(false);
        chatHistory.setContent(history);
        chatHistory.setStyle("-fx-font-size: 13pt;");
        chatHistory.setStyle("-fx-base: #FCCD5F");
        
        tabPane.getTabs().addAll(friends,chatHistory);
        tabPane.setStyle("-fx-base: #B4CEF8");
        friendName.setPrefSize(160, 50);
        send.setPrefSize(75, 50);
        send.setStyle("-fx-base: #1156A9");

        gpRequest.addRow(0, friendName, send);

        Tab add = new Tab("Add Friends");
        add.setClosable(false);
        add.setContent(gpRequest);
        add.setStyle("-fx-font-size: 11.2pt;");
        add.setStyle("-fx-base: #FCCD5F");
        
        friendName.setStyle("-fx-base: #FFFFFF");


        Incomingrequests.setPrefSize(235, 50);
        Incomingrequests.getItems().addAll("test");



        Tab requests = new Tab("Friend Requests");
        requests.setClosable(false);
        requests.setContent(Incomingrequests);
        requests.setStyle("-fx-font-size: 11.2pt;");
        requests.setStyle("-fx-base: #FCCD5F");


        tpRequests.getTabs().addAll(add,requests);
        tpRequests.setMinWidth(220);
        tpRequests.setStyle("-fx-base: #B4CEF8");


        gpLayout.add(gpOutput,0,0);
        gpLayout.add(gpFriends, 1, 0);
        gpLayout.add(gpTypeMsg, 0, 575);
        gpLayout.add(gpFriendRequests, 1, 575);

        gpOutput.add(sentMsgs, 0, 0);
        gpFriends.addColumn(0, tabPane);
        gpTypeMsg.addRow(0, makeMsg, sendMsg);
        gpFriendRequests.add(tpRequests, 0, 0);
        //gpLayout.add(sendMsg, 480, 575);

        chatClientStage.setTitle("The Best Chat Client Ever");
        chatClientStage.setScene(scene);
        System.out.println("278");
        chatClientStage.show();

        
        makeMsg.setOnKeyPressed(new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent ke)
            {
            	if (ke.getCode().equals(KeyCode.ENTER))
                {
                    String msg = "A"; //indicates a sent msg

                    msg += myUsername + "|";

                    msg += currentlyTalkingTo + "|";


                    System.out.println(msg);
                    msg += "_" + makeMsg.getText();

                    writer.println(msg);
                    writer.flush();
                    makeMsg.setText("");
                    makeMsg.requestFocus();
                	ke.consume();
                }
                
            }
        });
    }

    private void incomingMessage(String message) {
        String msg = message;
        HashSet<String> usernames = new HashSet<>();
        String username = "";
        String sender = "";
        boolean foundSender = false;
        for (int i = 0; i < msg.length(); i++) {
            if (msg.charAt(i) == '|' && foundSender == false) {
                sender = username;
                username = "";
                foundSender = true;
            }
            else if (msg.charAt(i) == '|') {
                usernames.add(username);
                username = "";
            }
            else if (msg.charAt(i) == '_') {
                msg = msg.substring(i+1, msg.length());
                break;
            }
            else {
                username += Character.toString(msg.charAt(i));
            }
        }
        
        if(!(myUsername.equals(sender)) && (usernames.contains(myUsername))) {
    	notificationPlayer = new MediaPlayer(notification);
    	notificationPlayer.setAutoPlay(true);
        }

        System.out.println(myUsername);
        System.out.println(sender);

        if (sender.equals(myUsername) && usernames.contains(currentlyTalkingTo) ||
                (sender.equals(currentlyTalkingTo) & usernames.contains(myUsername))) {
            sentMsgs.appendText(msg + "\r\n");
        }
    }

    private void setFriendsList(String message) {
        String username = "";
        for (int i = 0; i < message.length(); i++) {
            if (message.charAt(i) == '|') {
                friendsList.add(username);
                username = "";
            }
            else {
                username += Character.toString(message.charAt(i));
            }
        }
        gotFriendsListFlag = true;
    }

    private void outputMsg(String message) {
        String client = "";
        for (int i = 0; i < message.length(); i++) {
            if (message.charAt(i) == '_') {
                message = message.substring(i + 1, message.length());
                break;
            }
            else {
                client += message.charAt(i);
            }
        }

        if (myUsername.equals(client)) {
            sentMsgs.clear();
            String outputMsg = "";
            for (int i = 0; i < message.length(); i++) {
                if (message.charAt(i) == 'î') {
                    outputMsg += "\r\n";
                }
                else {
                    outputMsg += message.charAt(i);
                }
            }
            sentMsgs.appendText(outputMsg);
        }
    }

    private void loadFriendRequests(String receiver, ArrayList<String> requesters) {
        if (myUsername.equals(receiver)) {
            Incomingrequests.getItems().clear();
            Incomingrequests.getItems().addAll(requesters);
        }
    }

    private void getFriendRequests(String receiver) {
        writer.println("E" + receiver);
        writer.flush();
    }

    private void sortRequests (String message) {
        String receiver = "";
        boolean foundReceiver = false;
        ArrayList<String> requesters = new ArrayList<>();
        String username = "";
        for (int i = 0; i < message.length(); i++) {
            if (message.charAt(i) == '|' && foundReceiver == false) {
                receiver = username;
                foundReceiver = true;
                username = "";
            }
            else if (message.charAt(i) == '|') {
                requesters.add(username);
                username = "";
            }
            else {
                username += message.charAt(i);
            }
        }
        loadFriendRequests(receiver, requesters);
    }

    private void updateFriends(String message) {
        String acceptor = "";
        String requestor = "";
        for (int i = 0; i < message.length(); i++) {
            if (message.charAt(i) == '|') {
                requestor = message.substring(i+1, message.length());
                break;
            }
            else {
                acceptor += message.charAt(i);
            }
        }
        names.getItems().add(requestor);
        getFriendRequests(acceptor);
    }


    class IncomingReader implements Runnable {
        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null ) {

                    //indicates incoming message
                    if (message.charAt(0) == 'A') {
                        incomingMessage(message.substring(1, message.length()));
                    }
                    //create friendsList
                    else if (message.charAt(0) == 'B') {
                        setFriendsList(message.substring(1, message.length()));
                    }
                    //load Message
                    else if (message.charAt(0) == 'C') {
                        outputMsg(message.substring(1, message.length()));
                    }
                    //update Friend Requests
                    else if (message.charAt(0) == 'D') {
                        getFriendRequests(message.substring(1, message.length()));
                    }
                    else if (message.charAt(0) == 'E') {
                        sortRequests(message.substring(1, message.length()));
                    }
                    else if (message.charAt(0) == 'F') {
                        updateFriends(message.substring(1, message.length()));
                    }
                }
            }
            catch (IOException ex) {
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
         gpLogin.setStyle("-fx-background-color: linear-gradient(from 25% 25% to 100% 100%, #5AA9FF, #432a33)");

         
         Label username = new Label("Username:");
         GridPane.setConstraints(username, 8 , 12);
         
         TextField usernameInput = new TextField();
         GridPane.setConstraints(usernameInput, 9, 12);
         
         Label password = new Label("Password:");
         GridPane.setConstraints(password, 8 , 13);
         
         PasswordField passwordInput = new PasswordField();
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
            	 myUsername = usernameInput.getText();
            	 try {
            		boolean match = false;
					Scanner upscanner = new Scanner(file);
	            	String enteredup = usernameInput.getText() + "//" + passwordInput.getText();
	            	while (upscanner.hasNextLine()) {
	            		String tempup = upscanner.nextLine();
	            		if(tempup.equals(enteredup)) {match = true;}
	            	}
	            	if(match) {
	            	    writer.println("B" + myUsername);
	            	    writer.flush();
	            	    System.out.println("528");
	                    logInStage.close();
	                    System.out.println("531");
	                    int j = 0;
	                    for (int i = 0; i < 100000; i++) {
	                    	j++;
	                    }
	                    chatClient();
	                    System.out.println("537");
	            	}
	            	else {
	            		Label warning = new Label("username/pswd not found");
	                    GridPane.setConstraints(warning, 9 , 17);
	                    gpLogin.getChildren().add(warning);
	            	}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					System.out.println("file not found");
					e.printStackTrace();
				}
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
    
         passwordInput.setOnKeyPressed(new EventHandler<KeyEvent>()
         {
             @Override
             public void handle(KeyEvent ke)
             {
             	if (ke.getCode().equals(KeyCode.ENTER))
                 {
               	 myUsername = usernameInput.getText();
               	 try {
               		boolean match = false;
   					Scanner upscanner = new Scanner(file);
   	            	String enteredup = usernameInput.getText() + "//" + passwordInput.getText();
   	            	while (upscanner.hasNextLine()) {
   	            		String tempup = upscanner.nextLine();
   	            		if(tempup.equals(enteredup)) {match = true;}
   	            	}
   	            	if(match) {
   	            	    writer.println("B" + myUsername);
   	            	    writer.flush();
   	                    logInStage.close();
   	                    chatClient();
   	                    ke.consume();
   	            	}
   	            	else {
   	            		Label warning = new Label("username/pswd not found");
   	                    GridPane.setConstraints(warning, 9 , 17);
   	                    gpLogin.getChildren().add(warning);
   	            	}
   				} catch (FileNotFoundException e) {
   					// TODO Auto-generated catch block
   					System.out.println("file not found");
   					e.printStackTrace();
   				}
                 }
                 
             }
         });
    }

    
    private void register()  {
    	Stage regStage = new Stage();
        GridPane gpReg = new GridPane();
        gpReg.setPadding( new Insets(10, 10, 10, 10));
        gpReg.setVgap(10);
        gpReg.setHgap(6);
        gpReg.setStyle("-fx-background-color: linear-gradient(from 25% 25% to 100% 100%, #5AA9FF, #432a33)");

        
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


        regButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	Scanner upscanner = null;
				try {
					upscanner = new Scanner (file);
					boolean exists = false;
					upwriter = new FileWriter(file, true);
	                String userpswd = usernameInput.getText() + "//" + passwordInput.getText();
	                while(upscanner.hasNextLine()) {
	                	String info = upscanner.nextLine();
	                	if(info.equals(userpswd)) {
	                		exists = true;
	                	}
	                }
	                if(exists) {
	                	Label warning = new Label("Username already exists");
	                    GridPane.setConstraints(warning, 9 , 16);
	                    gpReg.getChildren().add(warning);
	                }
	                else {
	                    try {
							upwriter.write(userpswd);
							upwriter.write(newLine);
		                    upwriter.flush();
		                    upwriter.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
	                	regStage.close();
	                    logIn();
	                	
	                }
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
        			System.out.println("file not found");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            	

            }
        });   
        
        
    	
    }
}



