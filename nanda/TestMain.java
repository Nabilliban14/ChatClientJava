package nanda;

public class TestMain {
    public static void main (String[] args) {
        ClientInfo Nabil = new ClientInfo("Nabil");

        if (Nabil.exists()) {
            System.out.println("YOU HAS AN ACCOUNT");
        }
        else {
            Nabil.makeUser();
            System.out.println("YOU NOW HAS AN ACCOUNT");
        }
    }
}
