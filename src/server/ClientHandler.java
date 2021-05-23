package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;

    public ClientHandler(Socket clientSocket) throws IOException {
        this.client = clientSocket;
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream());
    }


    @Override
    public void run() {
        try {
            while(true) {
                String request;
                while((request = in.readLine()) != null ) {
                    System.out.println("Client requested " + request);
                    if(request.equals("login:admin:admin")) {
                        this.out.println("I'm logging you in.");
                    } else {
                        this.out.println("Unknown command.");
                    }
                    this.out.flush();
                }
            }
        } catch (IOException e) {
            System.err.println("ClientHandler IOexception thrown");
        } finally {
            out.close();
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
