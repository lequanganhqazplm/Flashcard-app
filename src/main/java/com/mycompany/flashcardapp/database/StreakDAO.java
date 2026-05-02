package com.mycompany.flashcardapp.database;

import com.mycompany.flashcardapp.model.Streak;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class StreakDAO {
    private final Connection connection;

    public StreakDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
        ensureTableExists();
    }

    public void ensureTableExists() {
        String sql = """
                CREATE TABLE IF NOT EXISTS streaks (
                    user_id INTEGER PRIMARY KEY,
                    current_streak INTEGER DEFAULT 0,
                    longest_streak INTEGER DEFAULT 0,
                    last_completed_at TEXT,
                    freeze_count INTEGER DEFAULT 0,
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                )
                """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Lỗi khi tạo bảng streaks: " + e.getMessage());
        }
    }

    public Streak getUserStreak(int userId) {
        String sql = "SELECT * FROM streaks WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Streak(
                        rs.getInt("user_id"),
                        rs.getInt("current_streak"),
                        rs.getInt("longest_streak"),
                        rs.getString("last_completed_at"),
                        rs.getInt("freeze_count"));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy streak: " + e.getMessage());
        }
        return null;
    }

    public boolean createDefaultStreak(int userId) {
        String sql = "INSERT OR IGNORE INTO streaks (user_id, current_streak, longest_streak, last_completed_at, freeze_count) "
                +
                "VALUES (?, 0, 0, NULL, 2)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
            System.out.println("✓ Created default streak for user " + userId);
            return true;
        } catch (SQLException e) {
            System.err.println("Lỗi khi tạo streak mặc định: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStreak(int userId) {
        // Đảm bảo có bản ghi streak cho user
        Streak streak = getUserStreak(userId);
        if (streak == null) {
            createDefaultStreak(userId);
            streak = getUserStreak(userId);
            if (streak == null) {
                System.err.println("Không thể tạo streak cho user " + userId);
                return false;
            }
        }

        LocalDate today = LocalDate.now();
        LocalDate lastCompleted = null;

        if (streak.getLastCompletedAt() != null && !streak.getLastCompletedAt().isEmpty()) {
            try {
                lastCompleted = LocalDate.parse(streak.getLastCompletedAt());
            } catch (Exception e) {
                System.err.println("Lỗi parse last_completed_at: " + streak.getLastCompletedAt());
            }
        }

        boolean shouldUpdate = false;

        if (lastCompleted == null) {
            // Lần đầu tiên học
            streak.setCurrentStreak(1);
            shouldUpdate = true;
            System.out.println("✓ Lần đầu học! Streak = 1");

        } else if (lastCompleted.equals(today)) {
            // Đã học hôm nay rồi, không tăng
            System.out.println("ℹ Đã học hôm nay. Streak giữ nguyên: " + streak.getCurrentStreak());
            return true;

        } else {
            long daysBetween = ChronoUnit.DAYS.between(lastCompleted, today);

            if (daysBetween == 1) {
                // Học liên tục
                streak.setCurrentStreak(streak.getCurrentStreak() + 1);
                System.out.println("✓ Ngày liên tiếp! Streak tăng lên: " + streak.getCurrentStreak());
            } else if (daysBetween == 2 && streak.getFreezeCount() > 0) {
                // Bỏ lỡ đúng 1 ngày nhưng còn freeze → tiêu 1 freeze, giữ streak
                streak.setFreezeCount(streak.getFreezeCount() - 1);
                streak.setCurrentStreak(streak.getCurrentStreak() + 1);
                System.out.println("🧊 Dùng 1 freeze! Streak giữ nguyên: " + streak.getCurrentStreak()
                        + " | Freeze còn lại: " + streak.getFreezeCount());
            } else {
                // Bỏ lỡ quá nhiều ngày hoặc hết freeze → reset
                System.out.println("⚠ Bỏ lỡ " + (daysBetween - 1) + " ngày. Streak reset về 1");
                streak.setCurrentStreak(1);
            }
            shouldUpdate = true;
        }

        if (shouldUpdate && streak.getCurrentStreak() > streak.getLongestStreak()) {
            streak.setLongestStreak(streak.getCurrentStreak());
            System.out.println("🏆 Kỷ lục mới! Streak dài nhất: " + streak.getLongestStreak());
        }

        if (shouldUpdate) {
            streak.setLastCompletedAt(today.toString());
        }

        return saveStreak(streak);
    }

    private boolean saveStreak(Streak streak) {
        String sql = "UPDATE streaks SET current_streak = ?, longest_streak = ?, last_completed_at = ?, freeze_count = ? "
                +
                "WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, streak.getCurrentStreak());
            pstmt.setInt(2, streak.getLongestStreak());
            pstmt.setString(3, streak.getLastCompletedAt());
            pstmt.setInt(4, streak.getFreezeCount());
            pstmt.setInt(5, streak.getUserId());
            pstmt.executeUpdate();
            System.out.println("✓ Streak đã được lưu");
            return true;
        } catch (SQLException e) {
            System.err.println("Lỗi khi lưu streak: " + e.getMessage());
            return false;
        }
    }
}
