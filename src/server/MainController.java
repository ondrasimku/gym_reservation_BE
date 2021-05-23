package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;

public class MainController implements Runnable {

    private boolean running;
    private Scanner input;
    private ServerSocket listener;

    public MainController(ServerSocket listener) {
        this.input = new Scanner(System.in);
        this.running = true;
        this.listener = listener;
    }

    private void startCommandLine() {

        while(running) {

            System.out.print("Command: ");
            String command = input.nextLine();
            parseCommand(command);
        }

    }

    private void parseCommand(String command) {

        String delims = "[ ]+";
        String[] tokens = command.split(delims);

        if (tokens[0].equals("exit"))
        {
            System.out.println("Exiting.");
            this.running = false;
            try {
                listener.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.exit(1);
            return;
        }

        if(tokens.length != 3) {
            System.out.println("Invalid command.");
            return;
        }

        if(tokens[0].equals("add"))
        {
            System.out.println("Adding user \"" + tokens[1] + "\" with password \"" + tokens[2] + "\"");
        }
        else if(tokens[0].equals("delete"))
        {
            System.out.println("Deleting user \"" + tokens[1] + "\" with password \"" + tokens[2] + "\"");
        }
        else
        {
            System.out.println("Invalid command.");
        }

    }

    @Override
    public void run() {
        startCommandLine();
    }
}
