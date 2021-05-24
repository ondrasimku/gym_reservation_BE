package server;

import database.DatabaseController;
import shared.User;
import shared.Lesson;

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
                } else if (request.substring(0, 8).equals("register")) {
                    register(request);
                } else if(request.substring(0, 7).equals("bookout")) {
                    bookout(request);
                } else if (request.substring(0, 6).equals("bookin")) {
                    bookin(request);
                } else if(request.substring(0, 6).equals("delete")) {
                    delete(request);
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
                this.out.flush();
            } else {
                boolean success = database.loginUser(tokens[1], tokens[2]);
                if (success) {
                    this.out.writeObject("login:success");
                    this.out.flush();
                    Thread.sleep(1000);
                    this.out.writeObject(getUser(tokens[1], tokens[2]));
                    this.out.flush();
                } else {
                    this.out.writeObject("login:failed");
                    this.out.flush();
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void register(String request) {
        String delims = ":";
        String[] tokens = request.split(delims);
        try {
            if (tokens.length != 4) {
                this.out.writeObject("register:failed");
                this.out.flush();
            } else {
                boolean success = database.registerUser(tokens[1], tokens[2]);
                if (success) {
                    this.out.writeObject("register:success");
                    this.out.flush();
                } else {
                    this.out.writeObject("register:failed");
                    this.out.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void bookout(String request) {
        String delims = ":";
        String[] tokens = request.split(delims);
        try {
            if (tokens.length != 5) {
                this.out.writeObject("bookout:failed");
                this.out.flush();
            } else {
                boolean success = database.bookoutLesson(tokens[1], tokens[2], tokens[3], tokens[4]);
                if (success) {
                    this.out.writeObject("bookout:success");
                    this.out.flush();
                    Thread.sleep(1000);
                    this.out.writeObject(getUser(tokens[3], tokens[4]));
                    this.out.flush();
                } else {
                    this.out.writeObject("bookout:failed");
                    this.out.flush();
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void bookin(String request) {
        String delims = ":";
        String[] tokens = request.split(delims);
        try {
            if (tokens.length != 5) {
                this.out.writeObject("bookin:failed");
                this.out.flush();
            } else {
                boolean success = database.bookinLesson(tokens[1], tokens[2], tokens[3], tokens[4]);
                if (success) {
                    this.out.writeObject("bookin:success");
                    this.out.flush();
                    Thread.sleep(1000);
                    this.out.writeObject(getUser(tokens[3], tokens[4]));
                    this.out.flush();
                } else {
                    this.out.writeObject("bookin:failed");
                    this.out.flush();
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void delete(String request) {
        String delims = ":";
        String[] tokens = request.split(delims);
        try {
            if (tokens.length != 5) {
                this.out.writeObject("delete:failed");
                this.out.flush();
            } else {
                boolean success = database.deleteLesson(tokens[1], tokens[2], tokens[3], tokens[4]);
                if (success) {
                    this.out.writeObject("delete:success");
                    this.out.flush();
                    Thread.sleep(1000);
                    this.out.writeObject(getUser(tokens[3], tokens[4]));
                    this.out.flush();
                } else {
                    this.out.writeObject("delete:failed");
                    this.out.flush();
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    private User getUser(String username, String password) {
        return database.getUser(username, password);
    }


}
