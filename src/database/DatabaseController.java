package database;

import shared.Lesson;
import shared.User;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DatabaseController {

    private String dbUrl;
    private String dbUsername;
    private String dbPassword;
    private Connection dbConn;
    private Statement stmt;

    public DatabaseController(String url, String username, String password) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        this.dbConn = DriverManager.getConnection(
                url, username, password);
        this.stmt = dbConn.createStatement();
    }

    public void closeConnection() {
        try {
            dbConn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public boolean loginUser(String username, String password)  {
        String sql = "SELECT id_user FROM user WHERE username = ? AND password = ? ";
        try {
            PreparedStatement preparedStatement = dbConn.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet result = preparedStatement.executeQuery();
            if(!result.next()) {
                return false;
            } else {
                return true;
            }
        } catch (SQLException throwables) {
            System.err.println("Exception in login prepared statement " + throwables.getMessage());
            return false;
        }
    }

    public User getUser(String username, String password) {
        String sql = "SELECT * FROM user WHERE username = ? AND password = ? ";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = dbConn.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet result = preparedStatement.executeQuery();

            if(result.next()) {
                User returnUser = new User(result.getString("username"),
                                            result.getString("password"),
                                            result.getInt("id_user"),
                                            result.getShort("is_instructor"));

                sql = "select l.* from user join user_lesson ul on user.id_user = ul.id_user\n" +
                        "    join lesson l on l.id_lesson = ul.id_lesson\n" +
                        "        where user.username = ? and user.password = ? " +
                        "           and (l.date >= CURDATE() and l.date <= CURDATE() + INTERVAL 14 DAY)";
                preparedStatement = dbConn.prepareStatement(sql);
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                result = preparedStatement.executeQuery();
                while(result.next()) {
                    Long lessonDate = result.getTimestamp("date").getTime();
                    DateFormat dateFormat = new SimpleDateFormat("dd.MM. yyyy hh:mm");
                    String strDate = dateFormat.format(lessonDate);
                    returnUser.addLesson(new Lesson(result.getInt("id_lesson"),
                                                    strDate,
                                                    result.getString("name"),
                                                    result.getString("text") ));
                }

                sql = "(select * from lesson)\n" +
                        "except\n" +
                        "(select l.* from user join user_lesson ul on user.id_user = ul.id_user\n" +
                        "   join lesson l on l.id_lesson = ul.id_lesson\n" +
                        "       where user.username = ? and user.password = ?\n" +
                        "         and (l.date >= CURDATE() and l.date <= CURDATE() + INTERVAL 14 DAY));";
                preparedStatement = dbConn.prepareStatement(sql);
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                result = preparedStatement.executeQuery();
                while(result.next()) {
                    Long lessonDate = result.getTimestamp("date").getTime();
                    DateFormat dateFormat = new SimpleDateFormat("dd.MM. yyyy hh:mm");
                    String strDate = dateFormat.format(lessonDate);
                    returnUser.addUpcomingLesson(new Lesson(result.getInt("id_lesson"),
                            strDate,
                            result.getString("name"),
                            result.getString("text") ));
                }

                return returnUser;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }

    public boolean registerUser(String username, String password) {
        String sql = "INSERT INTO user (username, password, is_instructor)\n" +
                "    VALUES (?, ?, 0);";
        try {
            PreparedStatement preparedStatement = dbConn.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            int result = preparedStatement.executeUpdate();
            if(result == 0) {
                return false;
            } else {
                return true;
            }
        } catch (SQLException throwables) {
            System.err.println("Exception in register prepared statement " + throwables.getMessage());
            return false;
        }
    }

    public boolean bookoutLesson(String lesson, String user, String username, String password) {
        int lesson_id = Integer.parseInt(lesson);
        int user_id = Integer.parseInt(user);

        String sql = "delete user_lesson from user_lesson inner join user u on user_lesson.id_user = u.id_user\n" +
                "    WHERE id_lesson = ? AND u.id_user = ? and u.username= ? and u.password = ?";
        try {
            PreparedStatement preparedStatement = dbConn.prepareStatement(sql);
            preparedStatement.setInt(1, lesson_id);
            preparedStatement.setInt(2, user_id);
            preparedStatement.setString(3, username);
            preparedStatement.setString(4, password);
            int result = preparedStatement.executeUpdate();
            if(result == 0) {
                return false;
            } else {
                return true;
            }
        } catch (SQLException throwables) {
            System.err.println("Exception in bookout prepared statement " + throwables.getMessage());
            return false;
        }

    }

    public boolean bookinLesson(String lesson, String user, String username, String password) {
        int lesson_id = Integer.parseInt(lesson);

        String sql = "insert into user_lesson (id_lesson, id_user) values (?,\n" +
                "        (select id_user from user where username=? and password = ?) )";
        try {
            PreparedStatement preparedStatement = dbConn.prepareStatement(sql);
            preparedStatement.setInt(1, lesson_id);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, password);
            int result = preparedStatement.executeUpdate();
            if(result == 0) {
                return false;
            } else {
                return true;
            }
        } catch (SQLException throwables) {
            System.err.println("Exception in bookin prepared statement " + throwables.getMessage());
            return false;
        }
    }

}
