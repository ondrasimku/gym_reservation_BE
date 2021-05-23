package server;

import database.DatabaseController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private DatabaseController database;

    public ClientHandler(Socket clientSocket, DatabaseController database) throws IOException {
        this.client = clientSocket;
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream(), true);
        this.database = database;
    }


    @Override
    public void run() {
        try {
            String request;
            while((request = in.readLine()) != null ) {

                if(request.substring(0, 5).equals("login")) {
                    login(request);
                }

            }
        } catch (IOException e) {
            System.err.println("Exception while getting request from client: " + e.getMessage());
        } finally {
            out.close();
            try {
                client.close();
                in.close();
            } catch (IOException e) {
                System.err.println("Exception while closing connection: " + e.getMessage());
            }
        }
    }

    public void login(String request) {
        String delims = ":";
        String[] tokens = request.split(delims);
        if(tokens.length != 3) {
            this.out.println("login:failed");
        } else {
            boolean success = database.loginUser(tokens[1], tokens[2]);
            if(success) {
                this.out.println("login:success");
            } else {
                this.out.println("login:failed");
            }

        }
    }


}
