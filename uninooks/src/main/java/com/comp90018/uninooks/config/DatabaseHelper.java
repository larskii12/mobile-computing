package com.comp90018.uninooks.config;

import com.comp90018.uninooks.R;
import com.comp90018.uninooks.activities.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Constructor, define the database information and connector
 */
public class DatabaseHelper {

    InputStream inputStream;
    Properties properties;
    Connection connector;

    /**
     * Constructor, Create a new database connection instance.
     */
    public DatabaseHelper() {
        try {
            inputStream = MainActivity.getAppContext().getResources().openRawResource(R.raw.config);
            properties = new Properties();
            properties.load(inputStream);

            // JDBC URL components
            String HOST = properties.getProperty("DATABASE_HOST");
            String DB = properties.getProperty("DATABASE_NAME");
            String USER = properties.getProperty("DATABASE_USER");
            String PASS = properties.getProperty("DATABASE_PASS");

            String URL = "jdbc:postgresql://" + HOST + ":5432/" + DB;

            Class.forName("org.postgresql.Driver");
            connector = DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException | ClassNotFoundException | IOException e) {
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