package com.mycompany.flashcardapp.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DATABASE_URL = "jdbc:sqlite:flashcard.sqlite";
    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            Class.forName("org.sqlite.JDBC");

            connection = DriverManager.getConnection(DATABASE_URL);

        } catch (ClassNotFoundException e) {
            System.err.println("Không tìm thấy SQLite JDBC driver!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Kết nối với database thất bại!");
            e.printStackTrace();
        }
    }

    // Đảm bảo quy tắc singleton

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    //ủa file flashcards.sqlite k có nhể
    public Connection getConnection() {
        try {
            //Kiểm tra nếu đóng keets nối thì kết nối lại =)))
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DATABASE_URL);
            }
        } catch (SQLException e) {
            System.err.println("Failed to get database connection!");
            e.printStackTrace();
        }
        return connection;
    }

    // Đóng kết nối
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Failed to close database connection!");
            e.printStackTrace();
        }
    }
}
