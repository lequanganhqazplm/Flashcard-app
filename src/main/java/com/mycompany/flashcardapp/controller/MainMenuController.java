package com.mycompany.flashcardapp.controller;

import com.mycompany.flashcardapp.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class MainMenuController {

    @FXML
    private VBox TestBox;

    @FXML
    private Button logoutButton;

    @FXML
    private VBox reviewBox;

    @FXML
    private VBox statisticsBox;

    @FXML
    private VBox topicManagementBox;

    @FXML
    private Label welcomeLabel;

    @FXML
    public void initialize() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null && welcomeLabel != null) {
            welcomeLabel.setText("Xin chào, " + currentUser.getUsername() + "!");
        }
    }

    @FXML
    void handleLogout(ActionEvent event) {
        try {
            SessionManager.getInstance().logout(); // Xóa session khi đăng xuất
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Welcome.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setMaximized(false);
            stage.setScene(new Scene(root, 1280, 720));
            stage.setTitle("Flashcard Learning - Welcome");
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Không thể quay về màn hình Welcome");
        }
    }

    @FXML
    void openVocabularyManagement(MouseEvent event) {
        navigate(event, "/view/VocabularyManagement.fxml", "Flashcard Learning - Quản lý từ vựng");
    }

    // --- CHỈNH SỬA Ở ĐÂY ---
    @FXML
    void openStudyMode(MouseEvent event) {
        // Chuyển hướng sang màn hình Chọn Topic để ôn tập (ReviewByTopic)
        navigate(event, "/view/ReviewByTopic.fxml", "Flashcard Learning - Chọn chủ đề ôn tập");
    }
    // -----------------------

    @FXML
    void openTestMode(MouseEvent event) {
        navigate(event, "/view/TestModeMenu.fxml", "Flashcard Learning - Kiểm tra");
    }

    @FXML
    void openStatistics(MouseEvent event) {
        navigate(event, "/view/StatisticsUser.fxml", "Flashcard Learning - Thống kê");
    }

    // Hàm hỗ trợ chuyển cảnh để code gọn hơn
    private void navigate(MouseEvent event, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setMaximized(false);
            stage.setScene(new Scene(root, 1280, 720));
            stage.setTitle(title);
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Không thể mở màn hình: " + fxmlPath);
        }
    }
}