package com.mycompany.flashcardapp.controller;

import com.mycompany.flashcardapp.database.FlashcardDAO;
import com.mycompany.flashcardapp.database.StreakDAO;
import com.mycompany.flashcardapp.database.TestResultDAO;
import com.mycompany.flashcardapp.database.TopicDAO;
import com.mycompany.flashcardapp.model.Flashcard;
import com.mycompany.flashcardapp.model.TestResult;
import com.mycompany.flashcardapp.model.Topic;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.*;

public class TopicTestController {
    @FXML
    private Label scoreLabel;
    @FXML
    private VBox topicSelectionPane;
    @FXML
    private ComboBox<Topic> topicComboBox;
    @FXML
    private VBox testContentPane;
    @FXML
    private Label questionLabel;
    @FXML
    private Label questionTypeLabel;
    @FXML
    private Label feedbackLabel;
    @FXML
    private Label progressLabel;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Button submitButton;
    @FXML
    private Button nextButton;

    @FXML
    private TextField answerField;

    @FXML
    private VBox multipleChoicePane;
    @FXML
    private Button optionButton1;
    @FXML
    private Button optionButton2;
    @FXML
    private Button optionButton3;

    @FXML
    private VBox wordScramblePane;
    @FXML
    private Label scrambledLabel;
    @FXML
    private TextField unscrambleField;

    private FlashcardDAO flashcardDAO;
    private TopicDAO topicDAO;
    private TestResultDAO testResultDAO;
    private StreakDAO streakDAO;
    private List<QuestionData> testQuestions;
    private int currentQuestionIndex = 0;
    private int correctAnswers = 0;
    private int totalQuestions = 0;
    private QuestionData currentQuestion;
    private Topic selectedTopic;

    private enum QuestionType {
        DIEN_TU,
        TRAC_NGHIEM,
        XAO_TU
    }

    public class QuestionData {
        Flashcard flashcard;
        QuestionType type;
        List<String> options; // TRAC_NGHIEM
        int correctOptionIndex;
        String scrambleWord; // XAO_TU

        QuestionData(Flashcard flashcard, QuestionType type) {
            this.flashcard = flashcard;
            this.type = type;
        }
    }

    @FXML
    private void initialize() {
        flashcardDAO = new FlashcardDAO();
        topicDAO = new TopicDAO();
        testResultDAO = new TestResultDAO();
        streakDAO = new StreakDAO();
        loadTopics();
    }

    private void loadTopics() {
        int userId = SessionManager.getInstance().getCurrentUser().getId();
        List<Topic> topics = topicDAO.getAllTopics(userId);

        if (topics.isEmpty()) {
            showAlert("Không có chủ đề!", "Bạn chưa tạo chủ đề nào để kiểm tra!", AlertType.WARNING);
            backToTestMenu();
            return;
        }

        List<Topic> topicsWithFlashcards = new ArrayList<>();
        for (Topic topic : topics) {
            if (topic.getFlashcardCount() > 0) {
                topicsWithFlashcards.add(topic);
            }
        }

        if (topicsWithFlashcards.isEmpty()) {
            showAlert("Không có từ vựng!", "Không có chủ đề nào chứa từ vựng để kiểm tra!", AlertType.WARNING);
            backToTestMenu();
            return;
        }

        topicComboBox.getItems().addAll(topicsWithFlashcards);
        /*
         * Đoạn này AI vibe code, chưa hiểu lắm :)))
         */

        topicComboBox.setCellFactory(param -> new javafx.scene.control.ListCell<Topic>() {
            @Override
            protected void updateItem(Topic item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (" + item.getFlashcardCount() + " từ)");
                }
            }
        });

        topicComboBox.setButtonCell(new javafx.scene.control.ListCell<Topic>() {
            @Override
            protected void updateItem(Topic item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (" + item.getFlashcardCount() + " từ)");
                }
            }
        });
    }

    @FXML
    private void startTest() {
        selectedTopic = topicComboBox.getValue();

        if (selectedTopic == null) {
            showAlert("Chưa chọn chủ đề!", "Vui lòng chọn một chủ đề để bắt đầu kiểm tra!", AlertType.WARNING);
            return;
        }
        loadTestFlashcards();
        topicSelectionPane.setVisible(false);
        testContentPane.setVisible(true);
    }

    private void loadTestFlashcards() {
        int userId = SessionManager.getInstance().getCurrentUser().getId();
        List<Flashcard> allFlashcards = flashcardDAO.getFlashcardsByTopic(userId, selectedTopic.getId());
        if (allFlashcards.isEmpty()) {
            showAlert("Không có từ vựng!", "Chủ đề này không có từ vựng nào để kiểm tra!", AlertType.WARNING);
            backToTestMenu();
            return;
        }

        Collections.shuffle(allFlashcards);
        int limit = Math.min(10, allFlashcards.size());
        testQuestions = new ArrayList<>();

        for (int i = 0; i < limit; i++) {
            Flashcard flashcard = allFlashcards.get(i);
            QuestionType type;

            if (i < 4) {
                type = QuestionType.TRAC_NGHIEM;
            } else if (i < 7) {
                type = QuestionType.DIEN_TU;
            } else {
                type = QuestionType.XAO_TU;
            }

            QuestionData questionData = new QuestionData(flashcard, type);
            if (type == QuestionType.TRAC_NGHIEM) {
                prepareMultipleChoiceData(questionData, allFlashcards);
            } else if (type == QuestionType.XAO_TU) {
                prepareWordScrambleData(questionData);
            }

            testQuestions.add(questionData);
        }

        totalQuestions = testQuestions.size();
        currentQuestionIndex = 0;
        correctAnswers = 0;

        displayCurrentQuestion();
        updateProgress();
    }

    private void prepareMultipleChoiceData(QuestionData question, List<Flashcard> allFlashcards) {
        question.options = new ArrayList<>();
        question.options.add(question.flashcard.getDefinition());
        List<Flashcard> otherCards = new ArrayList<>(allFlashcards);
        otherCards.remove(question.flashcard);
        Collections.shuffle(otherCards);

        int wrongCount = 0;

        for (Flashcard card : otherCards) {
            if (wrongCount >= 2)
                break;
            if (!card.getDefinition().equals(question.flashcard.getDefinition())) {
                question.options.add(card.getDefinition());
                wrongCount++;
            }
        }
        while (question.options.size() < 3) {
            question.options.add("Đáp án ngẫu nhiên " + question.options.size());
        }

        String correctAnswer = question.flashcard.getDefinition();
        Collections.shuffle(question.options);
        question.correctOptionIndex = question.options.indexOf(correctAnswer);
    }

    private void prepareWordScrambleData(QuestionData question) {
        String word = question.flashcard.getVocabulary();
        List<Character> chars = new ArrayList<>();
        for (char c : word.toCharArray()) {
            chars.add(c);
        }
        Collections.shuffle(chars);

        StringBuilder scrambled = new StringBuilder();
        for (char c : chars) {
            scrambled.append(c);
        }
        question.scrambleWord = scrambled.toString();
    }

    private void displayCurrentQuestion() {
        if (currentQuestionIndex < testQuestions.size()) {
            currentQuestion = testQuestions.get(currentQuestionIndex);

            if (currentQuestion.type == QuestionType.XAO_TU) {
                questionLabel.setText(currentQuestion.flashcard.getDefinition());
            } else {
                questionLabel.setText(currentQuestion.flashcard.getVocabulary());
            }

            answerField.setVisible(false);
            multipleChoicePane.setVisible(false);
            wordScramblePane.setVisible(false);

            switch (currentQuestion.type) {
                case DIEN_TU:
                    questionTypeLabel.setText("ĐIỀN NGHĨA CỦA TỪ SAU:");
                    answerField.setVisible(true);
                    answerField.clear();
                    answerField.setDisable(false);
                    Platform.runLater(() -> answerField.requestFocus());
                    break;

                case TRAC_NGHIEM:
                    questionTypeLabel.setText("CHỌN NGHĨA ĐÚNG:");
                    multipleChoicePane.setVisible(true);
                    optionButton1.setText("A. " + currentQuestion.options.get(0));
                    optionButton2.setText("B. " + currentQuestion.options.get(1));
                    optionButton3.setText("C. " + currentQuestion.options.get(2));
                    resetButtonStyles();
                    enableAllButtons(true);
                    break;

                case XAO_TU:
                    questionTypeLabel.setText("SẮP XẾP CÁC CHỮ CÁI:");
                    wordScramblePane.setVisible(true);
                    scrambledLabel.setText(currentQuestion.scrambleWord);
                    unscrambleField.clear();
                    unscrambleField.setDisable(false);
                    Platform.runLater(() -> unscrambleField.requestFocus());
                    break;
            }

            feedbackLabel.setVisible(false);
            submitButton.setVisible(true);
            nextButton.setVisible(false);
        } else {
            showResults();
        }
    }

    @FXML
    private void submitAnswer() {
        boolean isCorrect = false;
        String userAnswer = "";
        String correctAnswer;

        if (currentQuestion.type == QuestionType.XAO_TU) {
            correctAnswer = currentQuestion.flashcard.getVocabulary();
        } else {
            correctAnswer = currentQuestion.flashcard.getDefinition();
        }

        switch (currentQuestion.type) {
            case DIEN_TU:
                userAnswer = answerField.getText().trim();
                if (userAnswer.isEmpty()) {
                    showFeedback("Vui lòng nhập câu trả lời!", "#e74c3c");
                    return;
                }
                isCorrect = userAnswer.equalsIgnoreCase(correctAnswer);
                answerField.setDisable(true);
                break;

            case XAO_TU:
                userAnswer = unscrambleField.getText().trim();
                if (userAnswer.isEmpty()) {
                    showFeedback("Vui lòng nhập từ đã sắp xếp!", "#e74c3c");
                    return;
                }
                isCorrect = userAnswer.equalsIgnoreCase(correctAnswer);
                unscrambleField.setDisable(true);
                break;

            case TRAC_NGHIEM:
                return;
        }

        processAnswer(isCorrect, correctAnswer);
    }

    @FXML
    private void selectOption1() {
        checkMultipleChoice(0);
    }

    @FXML
    private void selectOption2() {
        checkMultipleChoice(1);
    }

    @FXML
    private void selectOption3() {
        checkMultipleChoice(2);
    }

    private void checkMultipleChoice(int selectedIndex) {
        boolean isCorrect = selectedIndex == currentQuestion.correctOptionIndex;
        String correctAnswer = currentQuestion.flashcard.getDefinition();

        Button selectedButton = selectedIndex == 0 ? optionButton1 : selectedIndex == 1 ? optionButton2 : optionButton3;
        Button correctButton = currentQuestion.correctOptionIndex == 0 ? optionButton1
                : currentQuestion.correctOptionIndex == 1 ? optionButton2 : optionButton3;

        if (isCorrect) {
            selectedButton.setStyle(selectedButton.getStyle() + "; -fx-background-color: #27ae60;");
        } else {
            selectedButton.setStyle(selectedButton.getStyle() + "; -fx-background-color: #e74c3c;");
            correctButton.setStyle(correctButton.getStyle() + "; -fx-background-color: #27ae60;");
        }

        enableAllButtons(false);
        processAnswer(isCorrect, correctAnswer);
    }

    private void processAnswer(boolean isCorrect, String correctAnswer) {
        if (isCorrect) {
            correctAnswers++;
            showFeedback("✓ Chính xác! " + correctAnswer, "#27ae60");
        } else {
            showFeedback("✗ Sai rồi! Đáp án đúng: " + correctAnswer, "#e74c3c");
        }

        submitButton.setVisible(false);
        nextButton.setVisible(true);
        updateProgress();
    }

    private void resetButtonStyles() {
        String baseStyle = "-fx-background-color: #ecf0f1; -fx-background-radius: 12; -fx-cursor: hand; -fx-font-size: 20; -fx-text-fill: #2c3e50; -fx-alignment: CENTER-LEFT; -fx-padding: 0 30;";
        optionButton1.setStyle(baseStyle);
        optionButton2.setStyle(baseStyle);
        optionButton3.setStyle(baseStyle);
    }

    private void enableAllButtons(boolean enable) {
        optionButton1.setDisable(!enable);
        optionButton2.setDisable(!enable);
        optionButton3.setDisable(!enable);
    }

    private void updateProgress() {
        progressLabel.setText(String.format("Câu %d/%d", currentQuestionIndex + 1, totalQuestions));
        progressBar.setProgress((double) (currentQuestionIndex + 1) / totalQuestions);
        scoreLabel.setText(String.format("Điểm: %d/%d", correctAnswers, totalQuestions));
    }

    private void showFeedback(String message, String color) {
        feedbackLabel.setText(message);
        feedbackLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        feedbackLabel.setVisible(true);
    }

    @FXML
    private void nextQuestion() {
        currentQuestionIndex++;
        displayCurrentQuestion();
        updateProgress();
    }

    private void showResults() {
        double percentage = (double) correctAnswers / totalQuestions * 100;
        String grade;
        String message;

        if (percentage >= 90) {
            grade = "Xuất sắc!";
            message = "Bạn đã làm rất tốt!";
        } else if (percentage >= 70) {
            grade = "Tốt!";
            message = "Kết quả khá ổn!";
        } else if (percentage >= 50) {
            grade = "Khá";
            message = "Cần cố gắng thêm nhé!";
        } else {
            grade = "Cần học thêm";
            message = "Đừng nản chí, hãy tiếp tục luyện tập!";
        }

        // Lưu kết quả kiểm tra vào database
        int userId = SessionManager.getInstance().getCurrentUser().getId();
        String topicName = selectedTopic != null ? selectedTopic.getName() : null;
        TestResult result = new TestResult(userId, "TOPIC", topicName, correctAnswers, totalQuestions);
        boolean saved = testResultDAO.saveResult(result);
        if (saved) {
            System.out.println("Đã lưu kết quả bài kiểm tra chủ đề: " + topicName);
        } else {
            System.err.println("Không thể lưu kết quả kiểm tra");
        }

        streakDAO.updateStreak(userId);

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Kết quả kiểm tra");
        alert.setHeaderText(grade);
        alert.setContentText(String.format(
                "%s\n\nChủ đề: %s\nĐiểm số: %d/%d (%.0f%%)\n\nBạn trả lời đúng %d câu trong tổng số %d câu.",
                message, selectedTopic.getName(), correctAnswers, totalQuestions, percentage, correctAnswers,
                totalQuestions));
        alert.showAndWait();

        backToTestMenu();
    }

    private void showAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void backToTestMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TestModeMenu.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) scoreLabel.getScene().getWindow();
            Scene scene = new Scene(root, 1280, 720);
            stage.setMaximized(false);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.setTitle("Chế độ kiểm tra");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể quay lại menu!", AlertType.ERROR);
        }
    }
}
