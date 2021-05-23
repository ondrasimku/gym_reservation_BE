package database;

import shared.Lesson;
import shared.User;

import java.sql.*;

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
                        "        where user.username = ? and user.password = ?;";
                preparedStatement = dbConn.prepareStatement(sql);
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                result = preparedStatement.executeQuery();
                while(result.next()) {
                    Long lessonDate = result.getTimestamp("date").getTime();
                    returnUser.addLesson(new Lesson(result.getInt("id_lesson"),
                                                    lessonDate,
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
}
