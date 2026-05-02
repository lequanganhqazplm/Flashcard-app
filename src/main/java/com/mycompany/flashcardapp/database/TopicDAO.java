package com.mycompany.flashcardapp.database;

import com.mycompany.flashcardapp.model.Topic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TopicDAO {

    private final Connection connection;

    public TopicDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public boolean addTopic(int userId, String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        String sql = "INSERT INTO topics (user_id, name) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, name.trim());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE")) {
                System.err.println("Topic '" + name + "' already exists for this user!");
            } else {
                System.err.println("Failed to add topic!");
                e.printStackTrace();
            }
            return false;
        }
    }

    public boolean updateTopic(int topicId, String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            return false;
        }

        String sql = "UPDATE topics SET name = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newName.trim());
            pstmt.setInt(2, topicId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Failed to update topic!");
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteTopic(int topicId) {
        // First, set all flashcards with this topic_id to NULL
        String updateFlashcardsSQL = "UPDATE flashcards SET topic_id = NULL WHERE topic_id = ?";
        String deleteSQL = "DELETE FROM topics WHERE id = ?";

        try {
            // Update flashcards first
            try (PreparedStatement pstmt = connection.prepareStatement(updateFlashcardsSQL)) {
                pstmt.setInt(1, topicId);
                pstmt.executeUpdate();
            }

            // Then delete the topic
            try (PreparedStatement pstmt = connection.prepareStatement(deleteSQL)) {
                pstmt.setInt(1, topicId);
                pstmt.executeUpdate();
            }

            return true;
        } catch (SQLException e) {
            System.err.println("Failed to delete topic!");
            e.printStackTrace();
            return false;
        }
    }

    public List<Topic> getAllTopics(int userId) {
        List<Topic> topics = new ArrayList<>();
        String sql = """
                    SELECT t.id, t.user_id, t.name,
                           COUNT(f.id) as flashcard_count
                    FROM topics t
                    LEFT JOIN flashcards f ON t.id = f.topic_id
                    WHERE t.user_id = ?
                    GROUP BY t.id, t.user_id, t.name
                    ORDER BY t.name
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Topic topic = new Topic(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getInt("flashcard_count"));
                topics.add(topic);
            }
        } catch (SQLException e) {
            System.err.println("Failed to get topics!");
            e.printStackTrace();
        }

        return topics;
    }

    public Topic getTopicByName(int userId, String name) {
        String sql = """
                    SELECT t.id, t.user_id, t.name,
                           COUNT(f.id) as flashcard_count
                    FROM topics t
                    LEFT JOIN flashcards f ON t.id = f.topic_id
                    WHERE t.user_id = ? AND t.name = ?
                    GROUP BY t.id, t.user_id, t.name
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Topic(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getInt("flashcard_count"));
            }
        } catch (SQLException e) {
            System.err.println("Failed to get topic by name!");
            e.printStackTrace();
        }

        return null;
    }

    public boolean topicExists(int userId, String name) {
        String sql = "SELECT COUNT(*) FROM topics WHERE user_id = ? AND name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, name);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("Failed to check topic existence!");
            e.printStackTrace();
            return false;
        }
    }
}
