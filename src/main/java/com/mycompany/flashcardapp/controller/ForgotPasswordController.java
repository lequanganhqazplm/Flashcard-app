package com.mycompany.flashcardapp.controller;

import com.mycompany.flashcardapp.database.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.regex.Pattern;

public class ForgotPasswordController {

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private TextField usernameField; // Đổi sang usernameField

    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField passwordTextField;

    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField confirmTextField;

    @FXML
    private CheckBox showPasswordCheckbox;

    @FXML
    private Button nextButton;

    @FXML
    private Hyperlink backLink;

    private static final String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[^a-zA-Z0-9]).+$";

    @FXML
    void handleShowPassword(ActionEvent event) {
        if (showPasswordCheckbox.isSelected()) {
            passwordTextField.setText(passwordField.getText());
            confirmTextField.setText(confirmPasswordField.getText());

            passwordField.setVisible(false);
            passwordField.setManaged(false);
            passwordTextField.setVisible(true);
            passwordTextField.setManaged(true);

            confirmPasswordField.setVisible(false);
            confirmPasswordField.setManaged(false);
            confirmTextField.setVisible(true);
            confirmTextField.setManaged(true);
        } else {
            passwordField.setText(passwordTextField.getText());
            confirmPasswordField.setText(confirmTextField.getText());

            passwordField.setVisible(true);
            passwordField.setManaged(true);
            passwordTextField.setVisible(false);
            passwordTextField.setManaged(false);

            confirmPasswordField.setVisible(true);
            confirmPasswordField.setManaged(true);
            confirmTextField.setVisible(false);
            confirmTextField.setManaged(false);
        }
    }

    @FXML
    void handleNext(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = showPasswordCheckbox.isSelected() ? passwordTextField.getText() : passwordField.getText();
        String confirm = showPasswordCheckbox.isSelected() ? confirmTextField.getText() : confirmPasswordField.getText();

        // 1. Kiểm tra đầu vào
        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu mới!");
            return;
        }

        if (!Pattern.matches(PASSWORD_PATTERN, password)) {
            showAlert(Alert.AlertType.WARNING, "Mật khẩu yếu", "Mật khẩu phải bao gồm:\n- Ít nhất 1 chữ in hoa\n- Ít nhất 1 số\n- Ít nhất 1 ký tự đặc biệt (@, #, $, ...)");
            return;
        }

        if (password.length() < 6) {
            showAlert(Alert.AlertType.WARNING, "Mật khẩu quá ngắn", "Mật khẩu phải có ít nhất 6 ký tự!");
            return;
        }

        if (!password.equals(confirm)) {
            showAlert(Alert.AlertType.ERROR, "Lỗi xác nhận", "Mật khẩu xác nhận không trùng khớp!");
            return;
        }

        // 2. Logic kiểm tra username và cập nhật mật khẩu trực tiếp
        if (!userDAO.isUsernameExists(username)) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Tên đăng nhập này không tồn tại trong hệ thống!");
            return;
        }

        if (userDAO.updatePassword(username, password)) {
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đổi mật khẩu thành công! Hệ thống sẽ quay về trang đăng nhập.");
            handleBack(event); // Chuyển về màn hình Login
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi hệ thống", "Không thể cập nhật mật khẩu lúc này. Vui lòng thử lại!");
        }
    }

    @FXML
    void handleBack(ActionEvent event) {
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
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể quay lại màn hình đăng nhập!");
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