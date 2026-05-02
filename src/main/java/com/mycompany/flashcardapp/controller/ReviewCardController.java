package com.mycompany.flashcardapp.controller;

import com.mycompany.flashcardapp.database.FlashcardDAO;
import com.mycompany.flashcardapp.model.Flashcard;
import com.mycompany.flashcardapp.model.Topic;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ReviewCardController {

    @FXML
    private Label topicNameLabel;

    @FXML
    private Pane frontCardSide;
    @FXML
    private Label vocabLabel;

    @FXML
    private Pane backCardSide;
    @FXML
    private Label definitionLabel;

    @FXML
    private Button flipButton;
    @FXML
    private Button prevButton;
    @FXML
    private Button nextButton;
    @FXML
    private Button learnedButton;

    private final FlashcardDAO flashcardDAO = new FlashcardDAO();
    private List<Flashcard> flashcardList;
    private int currentIndex = 0;
    private boolean isFrontShowing = true;
    private Topic currentTopic;

    public void setTopic(Topic topic) {
        this.currentTopic = topic;
        if (topicNameLabel != null) {
            topicNameLabel.setText(topic.getName());
        }
        loadFlashcards();
    }

    private void loadFlashcards() {
        int userId = SessionManager.getInstance().getCurrentUser().getId();

        List<Flashcard> allCards = flashcardDAO.getAllFlashcards(userId);

        List<Flashcard> topicCards = allCards.stream()
                .filter(f -> f.getTopicId() != null && f.getTopicId() == currentTopic.getId())
                .collect(Collectors.toList());

        if (topicCards.isEmpty()) {
            showAlert("Thông báo", "Chủ đề này chưa có thẻ nào!");
            handleBack(null);
            return;
        }

        List<Flashcard> unlearnedCards = topicCards.stream()
                .filter(f -> !f.isLearned())
                .collect(Collectors.toList());

        if (unlearnedCards.isEmpty()) {
            boolean reset = showConfirmDialog("Hoàn thành",
                    "Bạn đã thuộc hết từ vựng trong chủ đề này.\nBạn có muốn ôn tập lại từ đầu không?");

            if (reset) {
                resetProgressForTopic(topicCards);
                this.flashcardList = topicCards;
            } else {
                handleBack(null);
                return;
            }
        } else {
            this.flashcardList = unlearnedCards;
        }

        currentIndex = 0;
        showCurrentCard();
    }

    private void resetProgressForTopic(List<Flashcard> cards) {
        for (Flashcard f : cards) {
            flashcardDAO.markAsLearned(f.getId(), false); // Cập nhật DB
            f.setLearned(false); // Cập nhật Model
        }
    }

    private void showCurrentCard() {
        if (flashcardList == null || flashcardList.isEmpty())
            return;

        Flashcard card = flashcardList.get(currentIndex);

        if (vocabLabel != null)
            vocabLabel.setText(card.getVocabulary());
        if (definitionLabel != null)
            definitionLabel.setText(card.getDefinition());

        isFrontShowing = true;
        updateCardSideVisibility();

        if (prevButton != null)
            prevButton.setDisable(currentIndex == 0);
        if (nextButton != null)
            nextButton.setDisable(currentIndex == flashcardList.size() - 1);
    }

    @FXML
    private void handleFlip(ActionEvent event) {
        isFrontShowing = !isFrontShowing; // Đảo ngược trạng thái
        updateCardSideVisibility();
    }

    private void updateCardSideVisibility() {
        if (frontCardSide != null && backCardSide != null) {
            frontCardSide.setVisible(isFrontShowing);
            frontCardSide.setManaged(isFrontShowing);

            backCardSide.setVisible(!isFrontShowing);
            backCardSide.setManaged(!isFrontShowing);
        }
    }

    @FXML
    private void handleNext(ActionEvent event) {
        if (currentIndex < flashcardList.size() - 1) {
            currentIndex++;
            showCurrentCard();
        }
    }

    @FXML
    private void handlePrevious(ActionEvent event) {
        if (currentIndex > 0) {
            currentIndex--;
            showCurrentCard();
        }
    }

    @FXML
    private void handleMarkLearned(ActionEvent event) {
        if (flashcardList.isEmpty())
            return;

        Flashcard currentCard = flashcardList.get(currentIndex);

        boolean success = flashcardDAO.markAsLearned(currentCard.getId(), true);

        if (success) {
            flashcardList.remove(currentIndex);

            if (flashcardList.isEmpty()) {
                loadFlashcards();
            } else {
                if (currentIndex >= flashcardList.size()) {
                    currentIndex = 0;
                }
                showCurrentCard();
            }
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ReviewByTopic.fxml"));
            Parent root = loader.load();

            Stage stage = null;
            if (topicNameLabel != null && topicNameLabel.getScene() != null)
                stage = (Stage) topicNameLabel.getScene().getWindow();
            else if (event != null && event.getSource() instanceof Node)
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            if (stage != null) {
                stage.setMaximized(false);
                stage.setScene(new Scene(root, 1280, 720));
                stage.setMaximized(true);
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private boolean showConfirmDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}