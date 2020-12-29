package ru.itis.application.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionUtil {

    private static Connection connection;
    private static final String URL = "jdbc:postgresql://localhost:5432/rabbit_docs";
    private static final String NAME = "postgres";
    private static final String PASSWORD = "123990";

    private ConnectionUtil() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(URL, NAME, PASSWORD);
        } catch (SQLException | ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Connection getConnection() {
        if (connection == null) {
            new ConnectionUtil();
        }
        return connection;
    }
}
