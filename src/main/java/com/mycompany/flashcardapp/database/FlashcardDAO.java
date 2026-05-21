package com.mycompany.flashcardapp.database;

import com.mycompany.flashcardapp.constant.ErrorMessage;
import com.mycompany.flashcardapp.model.Flashcard;
import com.mycompany.flashcardapp.model.Topic;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FlashcardDAO {
    private static final String FILE_NAME = "flashcards.dat";

    public FlashcardDAO() {
        FileDataManager.loadList(FILE_NAME);
    }

    private List<Flashcard> getFlashcards() {
        return FileDataManager.loadList(FILE_NAME);
    }

    private void saveFlashcards(List<Flashcard> flashcards) {
        FileDataManager.saveList(FILE_NAME, flashcards);
    }

    private int getNextId(List<Flashcard> flashcards) {
        return flashcards.stream().mapToInt(Flashcard::getId).max().orElse(0) + 1;
    }

    private String getTopicName(Integer topicId) {
        if (topicId == null) return "No Topic";
        List<Topic> topics = FileDataManager.loadList("topics.dat");
        return topics.stream()
                .filter(t -> t.getId() == topicId)
                .map(Topic::getName)
                .findFirst()
                .orElse("No Topic");
    }

    public boolean addFlashcard(int userId, String vocabulary, String definition, Integer topicId) {
        List<Flashcard> flashcards = getFlashcards();
        int newId = getNextId(flashcards);
        String tName = getTopicName(topicId);
        Flashcard newCard = new Flashcard(newId, userId, vocabulary, definition, topicId, tName, false);
        flashcards.add(newCard);
        saveFlashcards(flashcards);
        return true;
    }

    public boolean updateFlashcard(int id, String vocabulary, String definition, Integer topicId) {
        List<Flashcard> flashcards = getFlashcards();
        for (Flashcard f : flashcards) {
            if (f.getId() == id) {
                // Flashcard is immutable regarding the property wrapper in the custom code I wrote?
                // Wait, I didn't add setters for vocabulary and definition in Flashcard model!
                // Ah, the properties handle it by returning the property itself to call `.set()`
                // Let's modify the values inside the property directly.
                // Wait, if we do `f.vocabularyProperty().set(vocabulary);`, it will update the model.
                // But wait, the `Flashcard` model had no setters for them? It only had `property()` methods. Let's assume there are getters, we can use `f.vocabularyProperty().set()`.
                f.vocabularyProperty().set(vocabulary);
                f.definitionProperty().set(definition);
                f.setTopicId(topicId);
                f.setTopicName(getTopicName(topicId));
                saveFlashcards(flashcards);
                return true;
            }
        }
        return false;
    }

    public boolean deleteFlashcard(int id) {
        List<Flashcard> flashcards = getFlashcards();
        boolean removed = flashcards.removeIf(f -> f.getId() == id);
        if (removed) {
            saveFlashcards(flashcards);
        }
        return removed;
    }

    public List<Flashcard> getAllFlashcards(int userId) {
        return getFlashcards().stream()
                .filter(f -> f.getUserId() == userId)
                .map(f -> {
                    f.setTopicName(getTopicName(f.getTopicId()));
                    return f;
                })
                .sorted((f1, f2) -> Integer.compare(f2.getId(), f1.getId())) // DESC by id
                .collect(Collectors.toList());
    }

    public List<Flashcard> getFlashcardsByTopic(int userId, Integer topicId) {
        return getFlashcards().stream()
                .filter(f -> f.getUserId() == userId)
                .filter(f -> {
                    if (topicId == null) return f.getTopicId() == null;
                    return f.getTopicId() != null && f.getTopicId().equals(topicId);
                })
                .map(f -> {
                    f.setTopicName(getTopicName(f.getTopicId()));
                    return f;
                })
                .sorted((f1, f2) -> Integer.compare(f2.getId(), f1.getId()))
                .collect(Collectors.toList());
    }

    public boolean markAsLearned(int id, boolean isLearned) {
        List<Flashcard> flashcards = getFlashcards();
        for (Flashcard f : flashcards) {
            if (f.getId() == id) {
                f.setLearned(isLearned);
                saveFlashcards(flashcards);
                return true;
            }
        }
        return false;
    }

    public List<Flashcard> getUnlearnedFlashcards(int userId) {
        return getFlashcards().stream()
                .filter(f -> f.getUserId() == userId && !f.isLearned())
                .map(f -> {
                    f.setTopicName(getTopicName(f.getTopicId()));
                    return f;
                })
                .sorted((f1, f2) -> Integer.compare(f2.getId(), f1.getId()))
                .collect(Collectors.toList());
    }
}
