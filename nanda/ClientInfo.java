package nanda;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientInfo {
    ArrayList<String> friendsList;
    String username;

    public ClientInfo(String username) {
        this.username = username;

        if (this.exists()) {
            this.friendsList = makeFriends();
        }
        else {
            this.friendsList = new ArrayList<>();
        }
    }

    public String getUsername() {
        return username;
    }

    public ArrayList<String> getFriendsList() {
        return friendsList;
    }

    public boolean exists() {
        Path userPath = Paths.get("src/nanda/Users/" + username);
        if (Files.exists(userPath) && Files.isDirectory(userPath)) {
            return true;
        }
        return false;
    }

    private ArrayList<String> makeFriends() {
        ArrayList<String> friends = new ArrayList<>();
        File file = new File("src/nanda/Users/" + username + "/friendsList.txt");
        Scanner sc = null;
        try {
            sc = new Scanner(file);
        }
        catch (FileNotFoundException e) {
            System.out.println("friendsList.txt not found");
        }
        while (sc.hasNextLine()) {
            friends.add(sc.nextLine());
        }
        return friends;
    }

    public void makeUser() {

        /* Make user folder */
        File user = new File("src/nanda/Users/" + username);
        user.mkdir();

        /* Make friends list file */
        File friendsList = new File("src/nanda/Users/" + username + "/friendsList.txt");
        friendsList.mkdir();

        /* Make ChatHistory folder */
        File chatHistory = new File("src/nanda/Users/" + username + "/ChatHistory");
        chatHistory.mkdir();
    }

}
