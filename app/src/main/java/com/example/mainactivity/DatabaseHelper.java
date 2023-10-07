package com.example.mainactivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHelper {

    // JDBC URL components
    private static final String HOST = "103.119.109.139";
    private static final String DB = "mobile";
    private static final String USER = "mobileadmin";
    private static final String PASS = "iHgm5npedxNHkpWwFb99SWW28Z5dGgKvYhncehaWGvmbzWVXr3";
    private static final String URL = "jdbc:postgresql://" + HOST + ":5432/" + DB;

    Connection connector;

    /**
     * Constructor, Create a new database connection instance.
     */
    public DatabaseHelper() {
        try {
            Class.forName("org.postgresql.Driver");
            connector = DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Test the database connection
     *
     * @return boolean, True if database is available, False if database is unavailable.
     */
    public boolean databaseConnectionTest() {
        return this.connector != null;
    }

    /**
     * Getter, get the database connector
     *
     * @return database connection connector
     */
    public Connection getConnector() {
        return this.connector;
    }
}