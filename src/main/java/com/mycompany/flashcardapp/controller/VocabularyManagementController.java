package com.mycompany.flashcardapp.controller;

import com.mycompany.flashcardapp.database.TopicDAO;
import com.mycompany.flashcardapp.model.Topic;
import com.mycompany.flashcardapp.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class VocabularyManagementController {

    @FXML
    private Button addTopicButton;
    @FXML
    private Button backButton;
    @FXML
    private Button searchButton;
    @FXML
    private TextField searchTopicTextField;
    @FXML
    private ListView<Topic> topicListView;

    private ObservableList<Topic> topicsList = FXCollections.observableArrayList();
    private FilteredList<Topic> filteredTopics;

    private final TopicDAO topicDAO = new TopicDAO();

    @FXML
    public void initialize() {
        filteredTopics = new FilteredList<>(topicsList, p -> true);
        topicListView.setItems(filteredTopics);

        topicListView.setCellFactory(lv -> new javafx.scene.control.ListCell<Topic>() {
            @Override
            protected void updateItem(Topic topic, boolean empty) {
                super.updateItem(topic, empty);
                if (empty || topic == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(topic.getName() + " (" + topic.getFlashcardCount() + " từ)");
                    setStyle("-fx-padding: 10; -fx-font-size: 14px; -fx-cursor: hand;");
                }
            }
        });

        topicListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Topic selected = topicListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    openVocabularyOfTopic(selected);
                }
            }
        });

        loadTopics();
    }

    private void loadTopics() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null)
            return;
        try {
            List<Topic> topics = topicDAO.getAllTopics(currentUser.getId());
            topicsList.setAll(topics);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi khi tải danh sách chủ đề: " + e.getMessage());
        }
    }

    @FXML
    void handleSearchTopic(ActionEvent event) {
        String searchText = searchTopicTextField.getText().trim().toLowerCase();
        System.out.println("DEBUG: Searching for: '" + searchText + "'");

        filteredTopics.setPredicate(topic -> {
            if (searchText.isEmpty())
                return true;
            return topic.getName().toLowerCase().contains(searchText);
        });

    }

    @FXML
    void handleAddTopic(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/topic.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setMaximized(false);
            stage.setScene(new Scene(root, 1280, 720));
            stage.setTitle("Thêm chủ đề");
            stage.setMaximized(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Không thể chuyển qua màn hình Thêm chủ đề: " + e.getMessage());
        }
    }

    @FXML
    void handleBack(ActionEvent event) {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setMaximized(false);
                stage.setScene(new Scene(root, 1280, 720));
                stage.setTitle("Flashcard Learning - Đăng nhập");
                stage.setMaximized(true);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Không thể quay về trang đăng nhập: " + e.getMessage());
            }
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainMenu.fxml"));
            Parent root = loader.load();

            System.out.println("DEBUG: User already in SessionManager: " + currentUser.getUsername());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setMaximized(false);
            stage.setScene(new Scene(root, 1280, 720));
            stage.setTitle("Flashcard Learning - Menu Chính");
            stage.setMaximized(true);
            stage.show();
        } catch (Exception e) {
            System.err.println("ERROR: Failed to navigate back to MainMenu");
            e.printStackTrace();
            showAlert("Không thể quay về Menu chính: " + e.getMessage());
        }
    }

    private void openVocabularyOfTopic(Topic topic) {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            System.err.println("ERROR: Cannot open topic - currentUser is NULL");
            showAlert("Lỗi: Không tìm thấy thông tin người dùng.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/VocabularyOfTopic.fxml"));
            Parent root = loader.load();

            VocabularyOfTopicController controller = loader.getController();
            controller.setData(currentUser, topic);

            Stage stage = (Stage) topicListView.getScene().getWindow();
            stage.setMaximized(false);
            stage.setScene(new Scene(root, 1280, 720));
            stage.setTitle("Chi tiết chủ đề: " + topic.getName());
            stage.setMaximized(true);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Không thể mở chi tiết chủ đề: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}