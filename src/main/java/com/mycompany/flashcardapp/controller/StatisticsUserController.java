package com.mycompany.flashcardapp.controller;

import com.mycompany.flashcardapp.database.FlashcardDAO;
import com.mycompany.flashcardapp.database.StreakDAO;
import com.mycompany.flashcardapp.database.TestResultDAO;
import com.mycompany.flashcardapp.database.TopicDAO;
import com.mycompany.flashcardapp.model.Flashcard;
import com.mycompany.flashcardapp.model.Streak;
import com.mycompany.flashcardapp.model.TestResult;
import com.mycompany.flashcardapp.model.Topic;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class StatisticsUserController {

    @FXML
    private Label lblTotalWords;
    @FXML
    private Label lblLearnedWords;
    @FXML
    private Label lblTotalTopics;
    @FXML
    private Label lblAvgScore;

    @FXML
    private Label lblCurrentStreak;
    @FXML
    private Label lblLongestStreak;
    @FXML
    private Label lblFreezeCount;

    @FXML
    private VBox testResultsContainer;
    @FXML
    private Label lblNoResults;

    private final FlashcardDAO flashcardDAO = new FlashcardDAO();
    private final TopicDAO topicDAO = new TopicDAO();
    private final StreakDAO streakDAO = new StreakDAO();
    private final TestResultDAO testResultDAO = new TestResultDAO();

    @FXML
    public void initialize() {
        int userId = SessionManager.getInstance().getCurrentUser().getId();

        Streak existingStreak = streakDAO.getUserStreak(userId);
        if (existingStreak == null) {
            streakDAO.createDefaultStreak(userId);
        }

        loadStatistics(userId);
        loadStreak(userId);
        loadTestHistory(userId);
    }

    private void loadStatistics(int userId) {
        try {
            List<Flashcard> allFlashcards = flashcardDAO.getAllFlashcards(userId);
            int total = allFlashcards.size();
            long learned = allFlashcards.stream().filter(Flashcard::isLearned).count();
            setText(lblTotalWords, String.valueOf(total));
            setText(lblLearnedWords, String.valueOf(learned));
        } catch (Exception e) {
            setText(lblTotalWords, "0");
            setText(lblLearnedWords, "0");
        }

        try {
            List<Topic> topics = topicDAO.getAllTopics(userId);
            setText(lblTotalTopics, String.valueOf(topics.size()));
        } catch (Exception e) {
            setText(lblTotalTopics, "0");
        }

        try {
            double avg = testResultDAO.getAverageScore(userId);
            setText(lblAvgScore, avg < 0 ? "--" : String.format("%.0f%%", avg));
        } catch (Exception e) {
            setText(lblAvgScore, "--");
        }
    }

    private void loadStreak(int userId) {
        try {
            Streak streak = streakDAO.getUserStreak(userId);
            if (streak == null) {
                setText(lblCurrentStreak, "0");
                setText(lblLongestStreak, "0");
                setText(lblFreezeCount, "0");
                return;
            }
            setText(lblCurrentStreak, String.valueOf(streak.getCurrentStreak()));
            setText(lblLongestStreak, String.valueOf(streak.getLongestStreak()));
            setText(lblFreezeCount, String.valueOf(streak.getFreezeCount()));
        } catch (Exception e) {
            setText(lblCurrentStreak, "0");
            setText(lblLongestStreak, "0");
            setText(lblFreezeCount, "0");
        }
    }

    private void loadTestHistory(int userId) {
        try {
            List<TestResult> results = testResultDAO.getResultsByUser(userId);
            if (results.isEmpty()) {
                setVisible(lblNoResults, true);
                return;
            }
            setVisible(lblNoResults, false);
            boolean isEven = false;
            for (TestResult r : results) {
                testResultsContainer.getChildren().add(buildResultRow(r, isEven));
                isEven = !isEven;
            }
        } catch (Exception e) {
            setVisible(lblNoResults, true);
        }
    }

    private HBox buildResultRow(TestResult r, boolean isEven) {
        HBox row = new HBox();
        row.setPadding(new Insets(7, 10, 7, 10));
        row.setStyle("-fx-background-color: " + (isEven ? "#F8F8F8" : "#FFFFFF") + "; -fx-background-radius: 6;");

        Label lblType = new Label(r.getDisplayName());
        lblType.setPrefWidth(155);
        lblType.setStyle("-fx-font-size: 12;");
        lblType.setTextFill(Color.BLACK);

        Label lblScore = new Label(r.getScoreText());
        lblScore.setPrefWidth(85);
        lblScore.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");
        lblScore.setTextFill(Color.BLACK);

        double pct = r.getPercentage();
        Label lblPct = new Label(String.format("%.0f%%", pct));
        lblPct.setPrefWidth(65);
        String color = pct >= 80 ? "#2E7D32" : pct >= 50 ? "#E65100" : "#C62828";
        lblPct.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        Label lblDate = new Label(r.getDateDisplay());
        lblDate.setStyle("-fx-font-size: 11;");
        lblDate.setTextFill(Color.BLACK);

        row.getChildren().addAll(lblType, lblScore, lblPct, lblDate);
        return row;
    }

    private void setText(Label label, String text) {
        if (label != null)
            label.setText(text);
    }

    private void setVisible(Label label, boolean visible) {
        if (label != null)
            label.setVisible(visible);
    }

    @FXML
    void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainMenu.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setMaximized(false);
            stage.setScene(new Scene(root, 1280, 720));
            stage.setTitle("Flashcard Learning - Menu chính");
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
