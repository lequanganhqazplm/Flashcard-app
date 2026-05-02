package com.mycompany.flashcardapp.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.flashcardapp.constant.*;
import com.mycompany.flashcardapp.model.Flashcard;

public class FlashcardDAO {
    private final Connection connection;

    public FlashcardDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
        ensureTopicIdColumn();
    }

    private void ensureTopicIdColumn() {
        try {
            String checkSQL = "PRAGMA table_info(flashcards)";
            ResultSet rs = connection.createStatement().executeQuery(checkSQL);

            boolean hasTopicId = false;
            while (rs.next()) {
                if ("topic_id".equals(rs.getString("name"))) {
                    hasTopicId = true;
                    break;
                }
            }

            if (!hasTopicId) {
                String alterSQL = "ALTER TABLE flashcards ADD COLUMN topic_id INTEGER REFERENCES topics(id) ON DELETE SET NULL";
                connection.createStatement().execute(alterSQL);
                System.out.println("Added topic_id column to flashcards table.");
            }
        } catch (SQLException e) {
            System.err.println("Failed to ensure topic_id column!");
            e.printStackTrace();
        }
    }

    public boolean addFlashcard(int userId, String vocabulary, String definition, Integer topicId) {
        String sql = "INSERT INTO flashcards (user_id, vocabulary, definition, topic_id, is_learned) VALUES (?, ?, ?, ?, 0)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, vocabulary);
            pstmt.setString(3, definition);
            if (topicId != null) {
                pstmt.setInt(4, topicId);
            } else {
                pstmt.setNull(4, java.sql.Types.INTEGER);
            }
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println(ErrorMessage.ADD_FLASHCARD_FAILED);
            e.printStackTrace();
            return false;
        }
    }


    public boolean updateFlashcard(int id, String vocabulary, String definition, Integer topicId) {
        String sql = "UPDATE flashcards SET vocabulary = ?, definition = ?, topic_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, vocabulary);
            pstmt.setString(2, definition);
            if (topicId != null) {
                pstmt.setInt(3, topicId);
            } else {
                pstmt.setNull(3, java.sql.Types.INTEGER);
            }
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println(ErrorMessage.UPDATE_FLASHCARD_FAILED);
            e.printStackTrace();
            return false;
        }
    }


    public boolean deleteFlashcard(int id) {
        String sql = "DELETE FROM flashcards WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println(ErrorMessage.DELETE_FLASHCARD_FAILED);
            e.printStackTrace();
            return false;
        }
    }

    public List<Flashcard> getAllFlashcards(int userId) {
        List<Flashcard> flashcards = new ArrayList<>();
        String sql = """
                    SELECT f.id, f.user_id, f.vocabulary, f.definition,
                           f.topic_id, t.name as topic_name, f.is_learned
                    FROM flashcards f
                    LEFT JOIN topics t ON f.topic_id = t.id
                    WHERE f.user_id = ?
                    ORDER BY f.id DESC
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Integer topicId = rs.getInt("topic_id");
                if (rs.wasNull()) {
                    topicId = null;
                }

                String topicName = rs.getString("topic_name");
                if (topicName == null) {
                    topicName = "No Topic";
                }

                Flashcard flashcard = new Flashcard(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("vocabulary"),
                        rs.getString("definition"),
                        topicId,
                        topicName,
                        rs.getInt("is_learned") == 1);
                flashcards.add(flashcard);
            }
        } catch (SQLException e) {
            System.err.println("Failed to get flashcards!");
            e.printStackTrace();
        }
        return flashcards;
    }

    public List<Flashcard> getFlashcardsByTopic(int userId, Integer topicId) {
        List<Flashcard> flashcards = new ArrayList<>();
        String sql;

        if (topicId == null) {
            // Get flashcards with no topic
            sql = """
                        SELECT f.id, f.user_id, f.vocabulary, f.definition,
                               f.topic_id, t.name as topic_name, f.is_learned
                        FROM flashcards f
                        LEFT JOIN topics t ON f.topic_id = t.id
                        WHERE f.user_id = ? AND f.topic_id IS NULL
                        ORDER BY f.id DESC
                    """;
        } else {
            // Get flashcards for specific topic
            sql = """
                        SELECT f.id, f.user_id, f.vocabulary, f.definition,
                               f.topic_id, t.name as topic_name, f.is_learned
                        FROM flashcards f
                        LEFT JOIN topics t ON f.topic_id = t.id
                        WHERE f.user_id = ? AND f.topic_id = ?
                        ORDER BY f.id DESC
                    """;
        }

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            if (topicId != null) {
                pstmt.setInt(2, topicId);
            }
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Integer tid = rs.getInt("topic_id");
                if (rs.wasNull()) {
                    tid = null;
                }

                String topicName = rs.getString("topic_name");
                if (topicName == null) {
                    topicName = "No Topic";
                }

                Flashcard flashcard = new Flashcard(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("vocabulary"),
                        rs.getString("definition"),
                        tid,
                        topicName,
                        rs.getInt("is_learned") == 1);
                flashcards.add(flashcard);
            }
        } catch (SQLException e) {
            System.err.println("Failed to get flashcards by topic!");
            e.printStackTrace();
        }
        return flashcards;
    }

    /**
     * Mark flashcard as learned/unlearned
     */
    public boolean markAsLearned(int id, boolean isLearned) {
        String sql = "UPDATE flashcards SET is_learned = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, isLearned ? 1 : 0);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Failed to mark flashcard as learned!");
            e.printStackTrace();
            return false;
        }
    }

    public List<Flashcard> getUnlearnedFlashcards(int userId) {
        List<Flashcard> flashcards = new ArrayList<>();
        String sql = """
                    SELECT f.id, f.user_id, f.vocabulary, f.definition,
                           f.topic_id, t.name as topic_name, f.is_learned
                    FROM flashcards f
                    LEFT JOIN topics t ON f.topic_id = t.id
                    WHERE f.user_id = ? AND f.is_learned = 0
                    ORDER BY f.id DESC
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Integer topicId = rs.getInt("topic_id");
                if (rs.wasNull()) {
                    topicId = null;
                }

                String topicName = rs.getString("topic_name");
                if (topicName == null) {
                    topicName = "No Topic";
                }

                Flashcard flashcard = new Flashcard(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("vocabulary"),
                        rs.getString("definition"),
                        topicId,
                        topicName,
                        false);
                flashcards.add(flashcard);
            }
        } catch (SQLException e) {
            System.err.println("Failed to get unlearned flashcards!");
            e.printStackTrace();
        }
        return flashcards;
    }
}
