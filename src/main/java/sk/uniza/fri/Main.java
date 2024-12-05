package sk.uniza.fri;

public class Main {
    public static void main(String[] args) {
        String serverName = "Server";
        Integer rows = 20;
        Integer columns = 50;
        Integer port = 82;


        new Thread(() -> {
            Server server = new Server(serverName, rows, columns);
            server.startServer(port);
        }).start();

        new Thread(() -> {
            Client client = new Client(rows, columns, "KolokPeter", "192.168.56.1", port);
            client.startClient();
        }).start();

        new Thread(() -> {
            Client client = new Client(rows, columns, "Jožo", "192.168.56.1", port);
            client.startClient();
        }).start();
    }
}