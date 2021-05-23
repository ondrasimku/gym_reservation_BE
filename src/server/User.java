package server;

import java.util.ArrayList;
import server.Lesson;

public class User {
    private String username;
    private String password;
    private int id_user;
    private short is_instructor;
    private ArrayList<Lesson> lessons;

    public User(String username, String password, int id_user, short is_instructor) {
        this.username = username;
        this.password = password;
        this.id_user = id_user;
        this.is_instructor = is_instructor;
    }

}
