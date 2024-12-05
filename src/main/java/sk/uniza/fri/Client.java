package sk.uniza.fri;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class Client {

    private final String ipAddress;
    private final Integer port;
    private final String frameName;
    private final JTextArea textArea;
    private final JTextField textField;
    private final JButton button;

    public Client(Integer rows, Integer columns, String frameName, String ipAddress, Integer port) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.frameName = frameName;


        JFrame frame = new JFrame(frameName);
        this.textArea = new JTextArea(rows, columns);
        this.textArea.setEditable(false);
        this.textField = new JTextField(columns - 10);
        this.button = new JButton("Send");

        JPanel panel = new JPanel();
        panel.add(this.textField);
        panel.add(this.button);

        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(this.textArea), BorderLayout.CENTER);
        frame.add(panel, BorderLayout.SOUTH);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public void startClient() {
        try {
            Socket socket = new Socket(this.ipAddress, this.port);
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            writer.println(this.frameName);
            this.textArea.append("Connected to the server.\n");

            new Thread(() -> {
                String message = null;
                while (true) {
                    try {
                        if ((message = reader.readLine()) == null) {
                            break;
                        }
                    } catch (IOException e) {
                        this.textArea.append("Error reading from server > " + e.getMessage() + "\n");
                    }

                    this.textArea.append(message + "\n");
                }
            }).start();

            this.button.addActionListener(e -> {
                String message = this.textField.getText();
                if (!message.isEmpty()) {
                    writer.println(message);
                    this.textArea.append("You > " + message + "\n");
                    this.textField.setText("");
                }
            });
        } catch (IOException e) {
            this.textArea.append("Client error > " + e.getMessage() + "\n");
        }
    }
}