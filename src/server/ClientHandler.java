package server;

import database.DatabaseController;
import shared.User;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket client;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private DatabaseController database;

    public ClientHandler(Socket clientSocket, DatabaseController database) throws IOException {
        this.client = clientSocket;
        this.out = new ObjectOutputStream(client.getOutputStream());
        this.out.flush();
        this.in = new ObjectInputStream(client.getInputStream());
        this.database = database;
    }


    @Override
    public void run() {
        try {
            String request;
            while((request = (String)in.readObject()) != null ) {

                if(request.substring(0, 5).equals("login")) {
                    login(request);
                }

            }
        } catch (IOException e) {
            System.err.println("Exception while getting request from client: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
                client.close();
                in.close();
            } catch (IOException e) {
                System.err.println("Exception while closing connection: " + e.getMessage());
            }
        }
    }

    private void login(String request) {
        String delims = ":";
        String[] tokens = request.split(delims);
        try {
            if (tokens.length != 3) {
                this.out.writeObject("login:failed");
            } else {
                boolean success = database.loginUser(tokens[1], tokens[2]);
                if (success) {
                    this.out.writeObject("login:success");
                    this.out.flush();
                    Thread.sleep(1000);
                    this.out.writeObject(getUserAfterLogin(tokens[1], tokens[2]));
                    this.out.flush();
                } else {
                    this.out.writeObject("login:failed");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private User getUserAfterLogin(String username, String password) {
        return database.getUser(username, password);
    }


}
