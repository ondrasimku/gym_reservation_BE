package database;

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
}
