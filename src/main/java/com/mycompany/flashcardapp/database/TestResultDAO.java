package com.mycompany.flashcardapp.database;

import com.mycompany.flashcardapp.model.TestResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TestResultDAO {
    private Connection getConnection() {
        return DatabaseConnection.getInstance().getConnection();
    }

    public void ensureTableExists() {
        String sql = """
                CREATE TABLE IF NOT EXISTS test_results (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    test_type TEXT NOT NULL,
                    topic_name TEXT,
                    correct_answers INTEGER NOT NULL,
                    total_questions INTEGER NOT NULL,
                    percentage REAL NOT NULL,
                    created_at TEXT NOT NULL,
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                )
                """;
        try (Statement stmt = getConnection().createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Lỗi khi tạo bảng test_results: " + e.getMessage());
        }
    }

    public boolean saveResult(TestResult result) {
        ensureTableExists();
        String sql = """
                INSERT INTO test_results (user_id, test_type, topic_name,
                    correct_answers, total_questions, percentage, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, result.getUserId());
            ps.setString(2, result.getTestType());
            ps.setString(3, result.getTopicName());
            ps.setInt(4, result.getCorrectAnswers());
            ps.setInt(5, result.getTotalQuestions());
            ps.setDouble(6, result.getPercentage());
            ps.setString(7, result.getCreatedAt());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Lỗi khi lưu kết quả kiểm tra: " + e.getMessage());
            return false;
        }
    }

    public List<TestResult> getResultsByUser(int userId) {
        ensureTableExists();
        List<TestResult> results = new ArrayList<>();
        String sql = """
                SELECT id, user_id, test_type, topic_name,
                       correct_answers, total_questions, percentage, created_at
                FROM test_results
                WHERE user_id = ?
                ORDER BY created_at DESC
                """;
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                TestResult r = new TestResult();
                r.setId(rs.getInt("id"));
                r.setUserId(rs.getInt("user_id"));
                r.setTestType(rs.getString("test_type"));
                r.setTopicName(rs.getString("topic_name"));
                r.setCorrectAnswers(rs.getInt("correct_answers"));
                r.setTotalQuestions(rs.getInt("total_questions"));
                r.setPercentage(rs.getDouble("percentage"));
                r.setCreatedAt(rs.getString("created_at"));
                results.add(r);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy kết quả kiểm tra: " + e.getMessage());
        }
        return results;
    }

    public double getAverageScore(int userId) {
        ensureTableExists();
        String sql = "SELECT AVG(percentage) FROM test_results WHERE user_id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double avg = rs.getDouble(1);
                return rs.wasNull() ? -1 : avg;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tính điểm trung bình: " + e.getMessage());
        }
        return -1;
    }

}
