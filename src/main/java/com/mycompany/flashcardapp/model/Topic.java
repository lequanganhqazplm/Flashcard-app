package com.mycompany.flashcardapp.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.IOException;
import java.io.Serializable;

public class Topic implements Serializable {
    private static final long serialVersionUID = 1L;

    private transient IntegerProperty id;
    private transient IntegerProperty userId;
    private transient StringProperty name;
    private transient IntegerProperty flashcardCount; // Number of flashcards in this topic

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

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(getId());
        out.writeInt(getUserId());
        out.writeUTF(getName());
        out.writeInt(getFlashcardCount());
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        int idVal = in.readInt();
        int userIdVal = in.readInt();
        String nameVal = in.readUTF();
        int countVal = in.readInt();

        this.id = new SimpleIntegerProperty(idVal);
        this.userId = new SimpleIntegerProperty(userIdVal);
        this.name = new SimpleStringProperty(nameVal);
        this.flashcardCount = new SimpleIntegerProperty(countVal);
    }
}
