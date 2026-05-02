package com.mycompany.flashcardapp.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestResult {
    private int id;
    private int userId;
    private String testType; // random or topic test
    private String topicName; // random thì tên topic null
    private int correctAnswers;
    private int totalQuestions;
    private double percentage;
    private String createdAt;

    public TestResult() {
    }

    public TestResult(int userId, String testType, String topicName,
                      int correctAnswers, int totalQuestions) {
        this.userId = userId;
        this.testType = testType;
        this.topicName = topicName;
        this.correctAnswers = correctAnswers;
        this.totalQuestions = totalQuestions;
        this.percentage = totalQuestions > 0
                ? (double) correctAnswers / totalQuestions * 100
                : 0;
        this.createdAt = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDisplayName() {
        if ("TOPIC".equals(testType) && topicName != null && !topicName.isEmpty()) {
            return "Chủ đề: " + topicName;
        }
        return "Ngẫu nhiên";
    }

    public String getScoreText() {
        return correctAnswers + "/" + totalQuestions;
    }

    public String getDateDisplay() {
        if (createdAt == null || createdAt.length() < 16)
            return createdAt;
        try {
            String[] parts = createdAt.split(" ");
            String[] dateParts = parts[0].split("-");
            String timePart = parts[1].substring(0, 5);
            return dateParts[2] + "/" + dateParts[1] + " " + timePart;
        } catch (Exception e) {
            return createdAt;
        }
    }
}
