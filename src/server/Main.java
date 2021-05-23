package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import server.MainController;

public class Main {

    private static ArrayList<ClientHandler> clients = new ArrayList<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(10);
    private static final int PORT = 4444;

    public static void main(String[] args) throws IOException {

        System.out.println("[+] Starting the listener on port " + PORT + "!");
        ServerSocket listener = new ServerSocket(PORT);
        Thread mainThread = new Thread(new MainController(listener));
        mainThread.start();

        while(true) {
            Socket client = listener.accept();
            ClientHandler clientThread = new ClientHandler(client);
            clients.add(clientThread);
            pool.execute(clientThread);
        }
    }
}
