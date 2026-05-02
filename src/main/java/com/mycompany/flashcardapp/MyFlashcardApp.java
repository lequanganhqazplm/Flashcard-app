/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.flashcardapp;

import com.mycompany.flashcardapp.database.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Admin
 */
public class MyFlashcardApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        try {
            DatabaseConnection.getInstance();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Welcome.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            // Configure the stage
            stage.setTitle("Flashcard Learning - Welcome");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.centerOnScreen();
            stage.show();

            System.out.println("Mở application thành công");
        } catch (Exception e) {
            System.err.println("Mở application thâ bại");
            e.printStackTrace();
        }
    }
}
