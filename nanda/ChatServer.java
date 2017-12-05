package nanda;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Observable;
import java.util.Scanner;

public class ChatServer extends Observable {
	HashSet<ClientInfo> clients;
	public static void main(String[] args) {
		try {
			new ChatServer().setUpNetworking();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setUpNetworking() throws Exception {
		clients = new HashSet<>();
		@SuppressWarnings("resource")
		ServerSocket serverSock = new ServerSocket(4242);
		while (true) {
			Socket clientSocket = serverSock.accept();
			ClientObserver writer = new ClientObserver(clientSocket.getOutputStream());
			Thread t = new Thread(new ClientHandler(clientSocket));
			t.start();
			this.addObserver(writer);
			System.out.println("got a connection");
		}
	}
	class ClientHandler implements Runnable {
		private BufferedReader reader;

		public ClientHandler(Socket clientSocket) {
			Socket sock = clientSocket;
			try {
				reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			String message;
			try {
				while ((message = reader.readLine()) != null) {
					System.out.println(message.charAt(0));
					//Msg Sent
					if (message.charAt(0) == 'A') {
						String msg = message.substring(1, message.length());
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

						//writes to sender's chatHistories
						for (String receiver: usernames) {
							File file = new File("src/nanda/Users/" + sender + "/ChatHistory/" + receiver + ".txt");
							FileWriter fileWriter = null;
							try {
								fileWriter = new FileWriter(file, true);
								fileWriter.write(msg + "\r\n");
								fileWriter.close();
							}
							catch (IOException e) {
								System.out.println("cannot save chatHistory for " + receiver);
							}
						}

						//writes to receiver's chatHistories
						for (String receiver: usernames) {
							File file = new File("src/nanda/Users/" + receiver + "/ChatHistory/" + sender + ".txt");
							FileWriter fileWriter = null;
							try {
								fileWriter = new FileWriter(file, true);
								fileWriter.write(msg + "\r\n");
								fileWriter.close();
							}
							catch (IOException e) {
								System.out.println("cannot save chatHistory for asdsafgsafgsafg " + receiver);
							}
						}

						setChanged();
						notifyObservers(message);

					}

					//Create user and get friendsList
					else if (message.charAt(0) == 'B') {
						ClientInfo client = new ClientInfo(message.substring(1, message.length()));
						if (!client.exists()) {
							client.makeUser();
						}
						clients.add(client);
						String friends = "B";
						ArrayList<String> friendList = client.getFriendsList();
						for (int i = 0; i < friendList.size(); i++) {
							friends += friendList.get(i) + "|";
						}
						setChanged();
						notifyObservers(friends);
					}

					//load chatHistory
					else if (message.charAt(0) == 'C') {

						message = message.substring(1, message.length());
						ArrayList<String> friendsList = new ArrayList<>();
						String msg = "";
						String username = "";
						for (int i = 0; i < message.length(); i++) {
							if (message.charAt(i) == '|') {
								friendsList.add(username);
								username = "";
							}
							else if (message.charAt(i) == '_') {
								msg = message.substring(i+1, message.length());
								break;
							}
							else {
								username += Character.toString(message.charAt(i));
							}
						}

						String loader = "";
						for (int i = 0; i < msg.length(); i++) {
							if (msg.charAt(i) == '_') {
								msg = msg.substring(i+1, msg.length());
								break;
							}
							else {
								loader += Character.toString(msg.charAt(i));
							}
						}

						String loadedMsg = "";
						File file = new File("src/nanda/Users/" + friendsList.get(0) + "/ChatHistory/" + loader + ".txt");


						//System.out.println(friendsList.get(0) + " " + loader);
						Scanner sc = null;
						try {
							sc = new Scanner(file);
							while(sc.hasNextLine()) {
								loadedMsg += sc.nextLine() + "î";
							}
						}
						catch (FileNotFoundException e) {
							System.out.println("cannot load chatHistory for " + loader);
						}

						setChanged();
						notifyObservers("C" + friendsList.get(0) + "_" + loadedMsg);

					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}


		}
	}
}
