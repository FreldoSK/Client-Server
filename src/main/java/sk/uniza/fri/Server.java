package sk.uniza.fri;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Server {

    private final JTextArea textArea;
    private String serverIp;
    private final ArrayList<String> history;

    public Server(String frameName, Integer rows, Integer columns) {
        JFrame frame = new JFrame(frameName);

        JButton button = new JButton("History");
        this.history = new ArrayList<>();

        this.textArea = new JTextArea(rows, columns);
        this.textArea.setEditable(false);

        JPanel panel = new JPanel();
        panel.add(button);

        frame.add(panel, BorderLayout.SOUTH);
        frame.add(new JScrollPane(this.textArea), BorderLayout.CENTER);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        button.addActionListener(action -> {
            this.textArea.append("--------History--------\n");
            this.history.forEach(this.textArea::append);
            this.textArea.append("--------------------*\n");
        });


        this.serverIp = null;
    }


    public void startServer(Integer port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            this.serverIp = InetAddress.getLocalHost().getHostAddress();
            this.textArea.append("Server is running on: " + serverIp + ":" + port + "\n");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                this.textArea.append("Client connected: " + clientSocket.getInetAddress() + "\n");

                new Thread(() -> {
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);

                        String message;
                        String clientName = reader.readLine();
                        this.textArea.append(clientName + " has joined\n");

                        while ((message = reader.readLine()) != null) {
                            long time = System.currentTimeMillis();
                            Date date = new Date(time);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss");
                            this.textArea.append(message + "\n");
                            String response = "============================\n" +
                                    "Server > " + message +
                                    " \nACTUAL DATE AND TIME : " + simpleDateFormat.format(date) +
                                    "\nClient name : " + clientName + "\n";

                            writer.println(response);
                            this.history.add(response);
                        }



                    } catch (IOException e) {
                        this.textArea.append("Error > " + e.getMessage() + "\n");
                    }
                }).start();
            }
        } catch (IOException e) {
            this.textArea.append("Server error: " + e.getMessage() + "\n");
        }
    }
}