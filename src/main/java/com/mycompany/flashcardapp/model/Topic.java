package com.mycompany.flashcardapp.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Topic {
    private final IntegerProperty id;
    private final IntegerProperty userId;
    private final StringProperty name;
    private final IntegerProperty flashcardCount; // Number of flashcards in this topic

    public Topic() {
        this(0, 0, "", 0);
    }

    public Topic(int id, int userId, String name, int flashcardCount) {
        this.id = new SimpleIntegerProperty(id);
        this.userId = new SimpleIntegerProperty(userId);
        this.name = new SimpleStringProperty(name);
        this.flashcardCount = new SimpleIntegerProperty(flashcardCount);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public int getUserId() {
        return userId.get();
    }

    public IntegerProperty userIdProperty() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId.set(userId);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    // Flashcard Count
    public int getFlashcardCount() {
        return flashcardCount.get();
    }

    public IntegerProperty flashcardCountProperty() {
        return flashcardCount;
    }

    public void setFlashcardCount(int count) {
        this.flashcardCount.set(count);
    }

    @Override
    public String toString() {
        return name.get();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Topic topic = (Topic) obj;
        return id.get() == topic.id.get();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id.get());
    }
}
