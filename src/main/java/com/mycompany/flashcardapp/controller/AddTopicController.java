package com.mycompany.flashcardapp.controller;

import com.mycompany.flashcardapp.database.TopicDAO;
import com.mycompany.flashcardapp.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddTopicController {

    @FXML
    private Button continueButton;

    @FXML
    private TextField topicNameField;

    TopicDAO topicDAO = new TopicDAO();

    private User currentUser;

    @FXML
    void addTopicName(ActionEvent event) {
        String topicName = topicNameField.getText().trim();

        if (topicName.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thông báo", "Vui lòng nhập tên chủ đề!");
            return;
        }

        currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không tìm thấy thông tin người dùng!");
            return;
        }

        topicDAO.addTopic(currentUser.getId(), topicName);
        showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã thêm chủ đề: " + topicName);

        goBackToVocabularyManagement(event);
    }

    private void goBackToVocabularyManagement(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/VocabularyManagement.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setMaximized(false);
            stage.setScene(new Scene(root, 1280, 720));
            stage.setTitle("Flashcard Learning - Quản lý từ vựng");
            stage.setMaximized(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể quay lại màn hình quản lý từ vựng!");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    void handleBack(ActionEvent event) {
        goBackToVocabularyManagement(event);
    }

}
