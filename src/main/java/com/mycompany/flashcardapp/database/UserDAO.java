package com.mycompany.flashcardapp.database;

import com.mycompany.flashcardapp.model.User;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.mycompany.flashcardapp.constant.*;

public class UserDAO {
    private final Connection connection;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public UserDAO() {

        this.connection = DatabaseConnection.getInstance().getConnection();
        ensureOtpColumns();
    }

    private void ensureOtpColumns() {
        try {
            Statement stmt = connection.createStatement();
            stmt.executeQuery("SELECT otp_code, otp_expiry FROM users LIMIT 1");
        } catch (SQLException e) {
            try {
                Statement stmt = connection.createStatement();
                stmt.execute("ALTER TABLE users ADD COLUMN otp_code TEXT");
                stmt.execute("ALTER TABLE users ADD COLUMN otp_expiry TEXT");
                System.out.println("Đã thêm cột OTP vào bảng users.");
            } catch (SQLException ex) {
                // Có thể cột đã tồn tại hoặc lỗi khác, bỏ qua
            }
        }
    }

    public boolean isEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean saveOTP(String email, String otpCode) throws SQLException {
        String sql = "UPDATE users SET otp_code = ?, otp_expiry = ? WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);
            pstmt.setString(1, otpCode);
            pstmt.setString(2, expiryTime.format(DATE_FORMATTER));
            pstmt.setString(3, email);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean verifyOTP(String email, String inputOtp) {
        String sql = "SELECT otp_code, otp_expiry FROM users WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String dbOtp = rs.getString("otp_code");
                String dbExpiryStr = rs.getString("otp_expiry");

                if (dbOtp == null || dbExpiryStr == null) return false;

                LocalDateTime expiry = LocalDateTime.parse(dbExpiryStr, DATE_FORMATTER);

                if (dbOtp.equals(inputOtp) && LocalDateTime.now().isBefore(expiry)) {
                    return true;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // ĐÃ SỬA: Cập nhật mật khẩu mới theo USERNAME
    public boolean updatePassword(String username, String newPassword) {
        String sql = "UPDATE users SET password = ?, otp_code = NULL, otp_expiry = NULL WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newPassword);
            pstmt.setString(2, username);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isUsernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Failed to check username existence!");
            e.printStackTrace();
        }
        return false;
    }

    public boolean register(String username, String password) {
        if (isUsernameExists(username)) {
            return false;
        }
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println(ErrorMessage.REGISTER_FAILED);
            e.printStackTrace();
            return false;
        }
    }

    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"));
            }
        } catch (SQLException e) {
            System.err.println(ErrorMessage.LOGIN_FAILED);
            e.printStackTrace();
        }
        return null;
    }
}