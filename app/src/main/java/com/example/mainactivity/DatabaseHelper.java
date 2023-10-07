package com.example.mainactivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHelper {

    // JDBC URL components
    private static final String HOST = "103.119.109.139";
    private static final String DB = "mobile";
    private static final String USER = "mobileadmin";
    private static final String PASS = "iHgm5npedxNHkpWwFb99SWW28Z5dGgKvYhncehaWGvmbzWVXr3";
    private static final String URL = "jdbc:postgresql://" + HOST + ":5432/" + DB;

    Connection connection;

    public DatabaseHelper() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean databaseConnectionTest() {
        return this.connection != null;
    }


    public String getAllUsers() throws SQLException {

        String sql = "SELECT * FROM mobilecomputing.\"user\";";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        StringBuilder allUsers = new StringBuilder();

        // Process result set
        while (rs.next()) {
            allUsers.append(rs.getString("user_email") + "\n");
        }

        rs.close();
        stmt.close();

        return allUsers.toString();
    }
}