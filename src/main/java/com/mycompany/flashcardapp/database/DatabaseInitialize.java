package com.mycompany.flashcardapp.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitialize {
    public static void main(String[] args) {
        Connection connection = DatabaseConnection.getInstance().getConnection();
        try (Statement stmt = connection.createStatement()) {

            System.out.println("Đang tạo bảng 'users'...");
            String createUsersTable = """
                        CREATE TABLE IF NOT EXISTS users (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            username TEXT UNIQUE NOT NULL,
                            password TEXT NOT NULL,
                            email TEXT
                        )
                    """;
            stmt.execute(createUsersTable);
            System.out.println("Bảng 'users' đã sẵn sàng");

            System.out.println("Đang tạo bảng 'topics'...");
            String createTopicsTable = """
                        CREATE TABLE IF NOT EXISTS topics (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            name TEXT NOT NULL,
                            user_id INTEGER NOT NULL,
                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                            UNIQUE(user_id, name)
                        )
                    """;
            stmt.execute(createTopicsTable);
            System.out.println("Bảng 'topics' đã sẵn sàng");

            System.out.println("Đang tạo bảng 'flashcards'...");
            String createFlashcardsTable = """
                        CREATE TABLE IF NOT EXISTS flashcards (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            vocabulary TEXT NOT NULL,
                            definition TEXT NOT NULL,
                            is_learned INTEGER DEFAULT 0,
                            user_id INTEGER NOT NULL,
                            topic_id INTEGER,
                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                            FOREIGN KEY (topic_id) REFERENCES topics(id) ON DELETE SET NULL
                        )
                    """;
            stmt.execute(createFlashcardsTable);
            System.out.println("Bảng 'flashcards' đã sẵn sàng");

            System.out.println("Đang tạo bảng 'streaks'...");
            String createUserStreaksTable = """
                        CREATE TABLE IF NOT EXISTS streaks (
                            user_id INTEGER PRIMARY KEY,
                            current_streak INTEGER DEFAULT 0,
                            longest_streak INTEGER DEFAULT 0,
                            last_completed_at TEXT,
                            freeze_count INTEGER DEFAULT 0,
                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                        )
                    """;
            stmt.execute(createUserStreaksTable);
            System.out.println("Bảng 'streaks' đã sẵn sàng");

        } catch (SQLException e) {
            System.err.println("Lỗi khi khởi tạo database!");
            e.printStackTrace();
        }

    }

}
