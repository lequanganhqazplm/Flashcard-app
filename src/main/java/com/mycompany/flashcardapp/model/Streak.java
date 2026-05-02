package com.mycompany.flashcardapp.model;

public class Streak {
    private int userId;
    private int currentStreak;
    private int longestStreak;
    private String lastCompletedAt;
    private int freezeCount;

    public Streak() {
    }

    public Streak(int userId, int currentStreak, int longestStreak, String lastCompletedAt, int freezeCount) {
        this.userId = userId;
        this.currentStreak = currentStreak;
        this.longestStreak = longestStreak;
        this.lastCompletedAt = lastCompletedAt;
        this.freezeCount = freezeCount;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public int getLongestStreak() {
        return longestStreak;
    }

    public void setLongestStreak(int longestStreak) {
        this.longestStreak = longestStreak;
    }

    public String getLastCompletedAt() {
        return lastCompletedAt;
    }

    public void setLastCompletedAt(String lastCompletedAt) {
        this.lastCompletedAt = lastCompletedAt;
    }

    public int getFreezeCount() {
        return freezeCount;
    }

    public void setFreezeCount(int freezeCount) {
        this.freezeCount = freezeCount;
    }

    @Override
    public String toString() {
        return "Streak{" +
                "userId=" + userId +
                ", currentStreak=" + currentStreak +
                ", longestStreak=" + longestStreak +
                ", lastCompletedAt='" + lastCompletedAt + '\'' +
                ", freezeCount=" + freezeCount +
                '}';
    }
}
