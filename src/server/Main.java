package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sun.jdi.connect.Connector;
import database.DatabaseController;
import server.MainController;

import javax.xml.crypto.Data;

public class Main {

    private static ArrayList<ClientHandler> clients = new ArrayList<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(10);
    private static final int PORT = 4444;

    public static void main(String[] args) {
        DatabaseController database = null;
        try {
            database = new DatabaseController("jdbc:mysql://localhost:3306/main_database", "root", "");
        } catch (Exception e) {
            System.err.println("Exception while connecting to database" + e.getMessage());
            return;
        }

        System.out.println("[+] Starting the listener on port " + PORT + "!");
        ServerSocket listener = null;
        Thread mainThread = null;
        try {
            listener = new ServerSocket(PORT);
            mainThread = new Thread(new MainController(listener));
        } catch (IOException e) {
            System.err.println("Error opening socket: " + e.getMessage());
            database.closeConnection();
            return;
        }

        mainThread.start();

        while(true) {
            Socket client = null;
            ClientHandler clientThread = null;
            try {
                client = listener.accept();
                clientThread = new ClientHandler(client, database);
                clients.add(clientThread);
                pool.execute(clientThread);
            } catch (IOException e) {
                System.err.println("Error accepting client connection: " + e.getMessage());
            }
        }
    }
}
