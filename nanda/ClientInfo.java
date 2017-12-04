package nanda;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ClientInfo {
    ArrayList<String> friendsList;
    String username;

    public ClientInfo(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public ArrayList<String> getFriendsList() {
        return friendsList;
    }

    public boolean hasFriendsList() {
        Path userPath = Paths.get("src/nanda/Users/" + username);
        if (Files.exists(userPath) && Files.isDirectory(userPath)) {
            return true;
        }

        return false;
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
