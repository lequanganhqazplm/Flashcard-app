package com.mycompany.flashcardapp.storage;

import com.mycompany.flashcardapp.model.Topic;
import com.mycompany.flashcardapp.model.Flashcard;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TopicDAO {

    private static final String FILE_NAME = "topics.bin";

    public TopicDAO() {
        FileDataManager.loadList(FILE_NAME);
    }

    private List<Topic> getTopics() {
        return FileDataManager.loadList(FILE_NAME);
    }

    private void saveTopics(List<Topic> topics) {
        FileDataManager.saveList(FILE_NAME, topics);
    }

    private int getNextId(List<Topic> topics) {
        return topics.stream().mapToInt(Topic::getId).max().orElse(0) + 1;
    }

    // Helper: update flashcard count dynamically since we no longer have JOINs
    private int getFlashcardCount(int topicId) {
        List<Flashcard> flashcards = FileDataManager.loadList("flashcards.bin");
        return (int) flashcards.stream().filter(f -> f.getTopicId() != null && f.getTopicId() == topicId).count();
    }

    public boolean addTopic(int userId, String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        if (topicExists(userId, name.trim())) {
            System.err.println("Topic '" + name + "' already exists for this user!");
            return false;
        }

        List<Topic> topics = getTopics();
        int newId = getNextId(topics);
        Topic newTopic = new Topic(newId, userId, name.trim(), 0);
        topics.add(newTopic);
        saveTopics(topics);
        return true;
    }

    public boolean updateTopic(int topicId, String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            return false;
        }

        List<Topic> topics = getTopics();
        for (Topic t : topics) {
            if (t.getId() == topicId) {
                if (t.getUserId() == 0) return false; // Block modifying global topics
                t.setName(newName.trim());
                saveTopics(topics);
                return true;
            }
        }
        return false;
    }

    public boolean deleteTopic(int topicId) {
        // Set all flashcards with this topic_id to null
        List<Flashcard> flashcards = FileDataManager.loadList("flashcards.bin");
        boolean fChanged = false;
        for (Flashcard f : flashcards) {
            if (f.getTopicId() != null && f.getTopicId() == topicId) {
                f.setTopicId(null);
                f.setTopicName("No Topic");
                fChanged = true;
            }
        }
        if (fChanged) {
            FileDataManager.saveList("flashcards.bin", flashcards);
        }

        // Delete topic
        List<Topic> topics = getTopics();
        // Block deleting global topics
        if (topics.stream().anyMatch(t -> t.getId() == topicId && t.getUserId() == 0)) {
            return false;
        }
        boolean removed = topics.removeIf(t -> t.getId() == topicId);
        if (removed) {
            saveTopics(topics);
        }
        return removed;
    }

    public List<Topic> getAllTopics(int userId) {
        List<Topic> topics = getTopics();
        return topics.stream()
                .filter(t -> t.getUserId() == userId || t.getUserId() == 0)
                .map(t -> {
                    t.setFlashcardCount(getFlashcardCount(t.getId()));
                    return t;
                })
                .sorted((t1, t2) -> t1.getName().compareToIgnoreCase(t2.getName()))
                .collect(Collectors.toList());
    }

    public Topic getTopicByName(int userId, String name) {
        List<Topic> topics = getTopics();
        return topics.stream()
                .filter(t -> t.getUserId() == userId && t.getName().equals(name))
                .map(t -> {
                    t.setFlashcardCount(getFlashcardCount(t.getId()));
                    return t;
                })
                .findFirst()
                .orElse(null);
    }

    public boolean topicExists(int userId, String name) {
        List<Topic> topics = getTopics();
        return topics.stream().anyMatch(t -> t.getUserId() == userId && t.getName().equals(name));
    }
}
