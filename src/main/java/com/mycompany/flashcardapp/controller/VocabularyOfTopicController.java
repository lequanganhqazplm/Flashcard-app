package com.mycompany.flashcardapp.controller;

import com.mycompany.flashcardapp.database.FlashcardDAO;
import com.mycompany.flashcardapp.database.TopicDAO;
import com.mycompany.flashcardapp.model.Flashcard;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class VocabularyOfTopicController {

    @FXML
    private Text textLabel;
    @FXML
    private TextField newVocabField;
    @FXML
    private TextField newDefinitionField;
    @FXML
    private TextField searchVocabField;

    @FXML
    private TableView<Flashcard> vocabTable;
    @FXML
    private TableColumn<Flashcard, String> vocabCol;
    @FXML
    private TableColumn<Flashcard, String> defCol;

    @FXML
    private Button addVocabButton;
    @FXML
    private Button updateVocabButton;
    @FXML
    private Button deleteVocabButton;
    @FXML
    private Button deleteTopicButton;
    @FXML
    private Button backButton;

    private User currentUser;
    private Topic currentTopic;

    private final FlashcardDAO flashcardDAO = new FlashcardDAO();
    private final TopicDAO topicDAO = new TopicDAO();

    private ObservableList<Flashcard> masterData = FXCollections.observableArrayList();
    private FilteredList<Flashcard> filteredData;

    @FXML
    public void initialize() {
        vocabCol.setCellValueFactory(new PropertyValueFactory<>("vocabulary"));
        defCol.setCellValueFactory(new PropertyValueFactory<>("definition"));

        filteredData = new FilteredList<>(masterData, p -> true);

        searchVocabField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(flashcard -> {
                if (newValue == null || newValue.isEmpty())
                    return true;
                String lowerCaseFilter = newValue.toLowerCase();
                return flashcard.getVocabulary().toLowerCase().contains(lowerCaseFilter)
                        || flashcard.getDefinition().toLowerCase().contains(lowerCaseFilter);
            });
        });

        vocabTable.setItems(filteredData);
    }

    public void setData(User user, Topic topic) {
        this.currentUser = user;
        this.currentTopic = topic;

        if (currentTopic != null) {
            textLabel.setText("Chủ đề: " + currentTopic.getName());
            loadData();
        }
    }

    private void loadData() {
        if (currentUser != null && currentTopic != null) {
            var listFromDB = flashcardDAO.getFlashcardsByTopic(currentUser.getId(), currentTopic.getId());
            masterData.clear();
            masterData.addAll(listFromDB);
        }
    }

    @FXML
    void handleAddVocab(ActionEvent event) {
        String vocab = newVocabField.getText().trim();
        String def = newDefinitionField.getText().trim();

        if (vocab.isEmpty() || def.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập đủ từ vựng và nghĩa.");
            return;
        }

        if (currentUser == null || currentTopic == null) {
            showAlert(Alert.AlertType.ERROR, "Lỗi",
                    "Không tìm thấy thông tin người dùng hoặc chủ đề. Vui lòng thử lại.");
            return;
        }

        boolean success = flashcardDAO.addFlashcard(currentUser.getId(), vocab, def, currentTopic.getId());

        if (success) {
            newVocabField.clear();
            newDefinitionField.clear();
            loadData();
            newVocabField.requestFocus();
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể thêm từ này.");
        }
    }

    @FXML
    void handleDeleteVocab(ActionEvent event) {
        Flashcard selected = vocabTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Chưa chọn dòng", "Vui lòng chọn từ cần xóa.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Xóa từ: " + selected.getVocabulary() + "?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            flashcardDAO.deleteFlashcard(selected.getId());
            loadData();
        }
    }

    @FXML
    void handleUpdateVocab(ActionEvent event) {
        Flashcard selected = vocabTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Chưa chọn dòng", "Vui lòng chọn từ cần sửa.");
            return;
        }

        newVocabField.setText(selected.getVocabulary());
        newDefinitionField.setText(selected.getDefinition());

        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setHeaderText("Chế độ sửa");
        info.setContentText(
                "Dữ liệu đã được đưa lên ô nhập. Hãy sửa và nhấn nút '+ Thêm từ'.\n(Từ cũ sẽ bị xóa ngay bây giờ).");

        Optional<ButtonType> res = info.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            flashcardDAO.deleteFlashcard(selected.getId());
            loadData();
            newVocabField.requestFocus();
        }
    }

    @FXML
    void handleDeleteTopic(ActionEvent event) {
        if (currentTopic == null) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không tìm thấy thông tin chủ đề.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("CẢNH BÁO");
        alert.setHeaderText("Xóa chủ đề: " + currentTopic.getName());
        alert.setContentText("Hành động này sẽ xóa vĩnh viễn chủ đề và TẤT CẢ từ vựng bên trong.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            topicDAO.deleteTopic(currentTopic.getId());
            handleBack(event);
        }
    }

    @FXML
    void handleBack(ActionEvent event) {
        if (this.currentUser == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setMaximized(false);
                stage.setScene(new Scene(root, 1280, 720));
                stage.setTitle("Flashcard Learning - Đăng nhập");
                stage.setMaximized(true);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể quay về trang đăng nhập.");
            }
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/VocabularyManagement.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setMaximized(false);
            stage.setScene(new Scene(root, 1280, 720));
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể quay lại.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}