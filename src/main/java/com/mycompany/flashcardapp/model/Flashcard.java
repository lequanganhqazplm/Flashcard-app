package com.mycompany.flashcardapp.model;

import javafx.beans.property.*;

public class Flashcard {
    private final IntegerProperty id;
    private final IntegerProperty userId;
    private final StringProperty vocabulary;
    private final StringProperty definition;
    private final IntegerProperty topicId; // Foreign key to topics table
    private final StringProperty topicName; // For display purposes (not stored in DB)
    private final BooleanProperty isLearned;

    public Flashcard() {
        this(0, 0, "", "", null, "", false);
    }

    public Flashcard(int id, int userId, String vocabulary, String definition, Integer topicId, String topicName,
                     boolean isLearned) {
        this.id = new SimpleIntegerProperty(id);
        this.userId = new SimpleIntegerProperty(userId);
        this.vocabulary = new SimpleStringProperty(vocabulary);
        this.definition = new SimpleStringProperty(definition);
        this.topicId = new SimpleIntegerProperty(topicId != null ? topicId : 0);
        this.topicName = new SimpleStringProperty(topicName != null ? topicName : "No Topic");
        this.isLearned = new SimpleBooleanProperty(isLearned);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public int getUserId() {
        return userId.get();
    }

    public IntegerProperty userIdProperty() {
        return userId;
    }

    public String getVocabulary() {
        return vocabulary.get();
    }

    public StringProperty vocabularyProperty() {
        return vocabulary;
    }

    public String getDefinition() {
        return definition.get();
    }

    public StringProperty definitionProperty() {
        return definition;
    }

    public Integer getTopicId() {
        int val = topicId.get();
        return val == 0 ? null : val;
    }

    public IntegerProperty topicIdProperty() {
        return topicId;
    }

    public void setTopicId(Integer topicId) {
        this.topicId.set(topicId != null ? topicId : 0);
    }

    public String getTopicName() {
        return topicName.get();
    }

    public StringProperty topicNameProperty() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName.set(topicName != null ? topicName : "No Topic");
    }

    public boolean isLearned() {
        return isLearned.get();
    }

    public BooleanProperty isLearnedProperty() {
        return isLearned;
    }

    public void setLearned(boolean isLearned) {
        this.isLearned.set(isLearned);
    }

    @Override
    public String toString() {
        return "Flashcard{" +
                "id=" + getId() +
                ", userId=" + getUserId() +
                ", vocabulary='" + getVocabulary() + '\'' +
                ", definition='" + getDefinition() + '\'' +
                ", topicId=" + getTopicId() +
                ", topicName='" + getTopicName() + '\'' +
                ", isLearned=" + isLearned() +
                '}';
    }
}
