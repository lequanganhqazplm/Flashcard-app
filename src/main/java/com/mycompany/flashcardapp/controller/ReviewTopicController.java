package com.mycompany.flashcardapp.controller;

import com.mycompany.flashcardapp.database.TopicDAO;
import com.mycompany.flashcardapp.model.Topic;
import com.mycompany.flashcardapp.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ReviewTopicController {

    @FXML
    private TextField searchField; //

    @FXML
    private ListView<Topic> topicListView; //

    @FXML
    private Button backButton; //

    private final TopicDAO topicDAO = new TopicDAO();
    private final ObservableList<Topic> masterData = FXCollections.observableArrayList();
    private FilteredList<Topic> filteredData;

    @FXML
    public void initialize() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null)
            return;

        List<Topic> topics = topicDAO.getAllTopics(currentUser.getId());
        masterData.setAll(topics);

        filteredData = new FilteredList<>(masterData, p -> true);
        topicListView.setItems(filteredData);

        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                filteredData.setPredicate(topic -> {
                    if (newVal == null || newVal.isEmpty())
                        return true;
                    return topic.getName().toLowerCase().contains(newVal.toLowerCase());
                });
            });
        }

        topicListView.setCellFactory(param -> new ListCell<Topic>() {
            @Override
            protected void updateItem(Topic item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (" + item.getFlashcardCount() + " từ)");
                    setStyle("-fx-font-size: 16px; -fx-padding: 10;");
                }
            }
        });

        topicListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Topic selectedTopic = topicListView.getSelectionModel().getSelectedItem();
                if (selectedTopic != null) {
                    openReviewCard(selectedTopic);
                }
            }
        });

        // Gán nút Back
        if (backButton != null)
            backButton.setOnAction(e -> handleBack());
    }

    private void openReviewCard(Topic topic) {
        if (topic.getFlashcardCount() == 0) {
            showAlert("Chủ đề này chưa có từ vựng nào!");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ReviewCard.fxml"));
            Parent root = loader.load();

            ReviewCardController controller = loader.getController();
            if (controller != null) {
                controller.setTopic(topic);
            }

            Stage stage = (Stage) topicListView.getScene().getWindow();
            stage.setMaximized(false);
            stage.setScene(new Scene(root, 1280, 720));
            stage.setMaximized(true);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Lỗi không thể mở màn hình ôn tập: " + e.getMessage());
        }
    }

    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainMenu.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) topicListView.getScene().getWindow();
            stage.setMaximized(false);
            stage.setScene(new Scene(root, 1280, 720));
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(content);
        alert.show();
    }
}