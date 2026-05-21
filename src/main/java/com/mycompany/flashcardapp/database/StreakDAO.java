package com.mycompany.flashcardapp.database;

import com.mycompany.flashcardapp.model.Streak;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

public class StreakDAO {
    private static final String FILE_NAME = "streaks.dat";

    public StreakDAO() {
        FileDataManager.loadList(FILE_NAME);
    }

    private List<Streak> getStreaks() {
        return FileDataManager.loadList(FILE_NAME);
    }

    private void saveStreaks(List<Streak> streaks) {
        FileDataManager.saveList(FILE_NAME, streaks);
    }

    public Streak getUserStreak(int userId) {
        List<Streak> streaks = getStreaks();
        return streaks.stream().filter(s -> s.getUserId() == userId).findFirst().orElse(null);
    }

    public boolean createDefaultStreak(int userId) {
        List<Streak> streaks = getStreaks();
        if (streaks.stream().noneMatch(s -> s.getUserId() == userId)) {
            Streak newStreak = new Streak(userId, 0, 0, null, 2);
            streaks.add(newStreak);
            saveStreaks(streaks);
            System.out.println("✓ Created default streak for user " + userId);
            return true;
        }
        return false;
    }

    public boolean updateStreak(int userId) {
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
        List<Streak> streaks = getStreaks();
        for (int i = 0; i < streaks.size(); i++) {
            if (streaks.get(i).getUserId() == streak.getUserId()) {
                streaks.set(i, streak);
                saveStreaks(streaks);
                System.out.println("✓ Streak đã được lưu");
                return true;
            }
        }
        // If not found (shouldn't happen here normally but just in case)
        streaks.add(streak);
        saveStreaks(streaks);
        return true;
    }
}
