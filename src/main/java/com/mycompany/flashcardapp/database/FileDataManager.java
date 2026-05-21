package com.mycompany.flashcardapp.database;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
public class FileDataManager {
    @SuppressWarnings("unchecked")
    public static <T> List<T> loadList(String filename) {
        File file = new File(filename);
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<T>) ois.readObject();
        } catch (EOFException e) {
            return new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public static <T> void saveList(String filename, List<T> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
