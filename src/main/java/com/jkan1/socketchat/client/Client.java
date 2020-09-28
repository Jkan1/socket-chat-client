/**
 *
 * @author kan
 */
package com.jkan1.socketchat.client;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client extends JFrame implements ActionListener {

    String username;
    PrintWriter output;
    BufferedReader input;
    JTextArea chatScreen;
    JTextField chatHeader;
    JButton send, exit;
    Socket serverSocket;
    private static String EXIT_CODE = "<EXIT_SOCKET_CHAT_SERVER>";

    public Client(String username, String serverName, int serverPort) throws IOException {

        super(username);
        this.username = username;
        serverSocket = new Socket(serverName, serverPort);

        input = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
        output = new PrintWriter(serverSocket.getOutputStream(), true);

        output.println(username);
        buildInterface();
        new MessageThread().start();

    }

    public void buildInterface() {

        send = new JButton("send");
        exit = new JButton("exit");
        chatScreen = new JTextArea();
        chatScreen.setRows(30);
        chatScreen.setColumns(50);
        chatScreen.setEditable(false);
        chatHeader = new JTextField(50);
        JScrollPane scrollPane = new JScrollPane(chatScreen, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, "Center");
        JPanel panel = new JPanel(new FlowLayout());
        panel.add(chatHeader);

        panel.add(send);
        panel.add(exit);
        panel.setBackground(Color.LIGHT_GRAY);
        panel.setName("Instant Messenger");
        add(panel, "North");
        send.addActionListener(this);
        exit.addActionListener(this);
        setSize(500, 300);
        setVisible(true);
        pack();

    }

    @Override()
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == exit) {
            output.println(EXIT_CODE);
            System.exit(0);
        } else {
            String message = chatHeader.getText();
            if (message.equals(EXIT_CODE)) {
                message = "\"" + message + "\"";
            }
            output.println(message);
            chatHeader.setText(null);
        }
    }

    public static void main(String args[]) {

        String userName = JOptionPane.showInputDialog(null, "Please Enter Your Name", "Socket Chat", JOptionPane.PLAIN_MESSAGE);
        String SERVER_HOST = System.getenv("SERVER_HOST");
        final int SERVER_PORT = Integer.parseInt(System.getenv("SERVER_PORT"));
        try {
            new Client(userName, SERVER_HOST, SERVER_PORT);
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }

    }

    class MessageThread extends Thread {

        @Override()
        public void run() {
            String messageData;
            try {
                while (true) {
                    messageData = input.readLine();
                    chatScreen.append(messageData + "\n");
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

    }

}
