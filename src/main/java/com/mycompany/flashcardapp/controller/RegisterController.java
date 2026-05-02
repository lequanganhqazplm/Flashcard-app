package com.mycompany.flashcardapp.controller;

import com.mycompany.flashcardapp.database.UserDAO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.regex.Pattern;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField passwordTextField; // Ô hiển thị chữ cho Mật khẩu

    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField confirmTextField; // Ô hiển thị chữ cho Xác nhận mật khẩu

    @FXML
    private CheckBox showPasswordCheckbox;

    @FXML
    private Button registerButton;

    private final UserDAO userDAO = new UserDAO();

    private static final String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[^a-zA-Z0-9]).+$";


    /**
     * Xử lý khi tick vào "Hiện mật khẩu"
     */
    @FXML
    void handleShowPassword(ActionEvent event) {
        if (showPasswordCheckbox.isSelected()) {
            // Chế độ HIỆN: Lấy dữ liệu từ ô ẩn -> gán sang ô hiện
            passwordTextField.setText(passwordField.getText());
            confirmTextField.setText(confirmPasswordField.getText());

            // Đảo trạng thái hiển thị
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            passwordTextField.setVisible(true);
            passwordTextField.setManaged(true);

            confirmPasswordField.setVisible(false);
            confirmPasswordField.setManaged(false);
            confirmTextField.setVisible(true);
            confirmTextField.setManaged(true);

        } else {
            // Chế độ ẨN: Lấy dữ liệu từ ô hiện -> gán sang ô ẩn
            passwordField.setText(passwordTextField.getText());
            confirmPasswordField.setText(confirmTextField.getText());

            // Đảo trạng thái hiển thị
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

    /**
     * Xử lý sự kiện khi nhấn nút "Đăng ký"
     */
    @FXML
    private void handleRegister(ActionEvent event) {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();

        // Lấy mật khẩu tùy theo việc đang bật hay tắt chế độ "Hiện mật khẩu"
        String password = showPasswordCheckbox.isSelected() ? passwordTextField.getText() : passwordField.getText();
        String confirmPassword = showPasswordCheckbox.isSelected() ? confirmTextField.getText() : confirmPasswordField.getText();

        // 1. Kiểm tra dữ liệu đầu vào
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập đầy đủ tất cả các trường!");
            return;
        }

        if (username.length() < 3) {
            showAlert(Alert.AlertType.WARNING, "Tên đăng nhập không hợp lệ", "Tên đăng nhập phải có ít nhất 3 ký tự!");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            showAlert(Alert.AlertType.WARNING, "Email không hợp lệ",
                    "Vui lòng nhập đúng định dạng email (vd: user@example.com)!");
            return;
        }

        if (password.length() < 4) {
            showAlert(Alert.AlertType.WARNING, "Mật khẩu quá ngắn", "Mật khẩu phải có ít nhất 4 ký tự!");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Mật khẩu không khớp", "Mật khẩu xác nhận không trùng khớp!");
            return;
        }

        // 2. Kiểm tra logic nghiệp vụ (Database)
        if (userDAO.isUsernameExists(username)) {
            showAlert(Alert.AlertType.ERROR, "Lỗi đăng ký", "Tên đăng nhập đã tồn tại! Vui lòng chọn tên khác.");
            return;
        }

        if (!Pattern.matches(PASSWORD_PATTERN, password)) {
            showAlert(Alert.AlertType.WARNING, "Mật khẩu yếu", "Mật khẩu phải bao gồm:\n- Ít nhất 1 chữ in hoa\n- Ít nhất 1 số\n- Ít nhất 1 ký tự đặc biệt (@, #, $, ...)");
            return;
        }

        // 3. Thực hiện đăng ký
        boolean success = userDAO.register(username, password);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đăng ký tài khoản thành công!");
            navigateToLogin(event);
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi hệ thống", "Đăng ký thất bại! Vui lòng thử lại sau.");
        }
    }

    @FXML
    void goToLogin(ActionEvent event) {
        navigateToLogin(event);
    }

    private void navigateToLogin(ActionEvent event) {
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
            showAlert(Alert.AlertType.ERROR, "Lỗi ứng dụng", "Không thể mở màn hình đăng nhập!");
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