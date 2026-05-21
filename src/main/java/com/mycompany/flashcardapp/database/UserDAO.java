package com.mycompany.flashcardapp.database;

import com.mycompany.flashcardapp.model.User;
import com.mycompany.flashcardapp.constant.ErrorMessage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UserDAO {
    private static final String FILE_NAME = "users.dat";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Temporary in-memory storage for OTP since it expires quickly
    private static class OtpData {
        String code;
        LocalDateTime expiry;
        OtpData(String code, LocalDateTime expiry) {
            this.code = code;
            this.expiry = expiry;
        }
    }
    private static final Map<String, OtpData> otpMap = new HashMap<>();

    public UserDAO() {
        // Initialize file if not exists
        FileDataManager.loadList(FILE_NAME);
    }

    private List<User> getUsers() {
        return FileDataManager.loadList(FILE_NAME);
    }

    private void saveUsers(List<User> users) {
        FileDataManager.saveList(FILE_NAME, users);
    }

    private int getNextId(List<User> users) {
        return users.stream().mapToInt(User::getId).max().orElse(0) + 1;
    }

    public boolean isEmailExists(String email) {
        // Since User model doesn't even have email in the Java class, we simulate it
        // Or if it did, we'd check it. The original SQL table had email but User object doesn't.
        // We will just return false or true based on username if email isn't in User class.
        // To keep it simple, we just assume the email is the username for this check.
        List<User> users = getUsers();
        return users.stream().anyMatch(u -> u.getUsername().equals(email));
    }

    public boolean saveOTP(String email, String otpCode) {
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);
        otpMap.put(email, new OtpData(otpCode, expiryTime));
        return true;
    }

    public boolean verifyOTP(String email, String inputOtp) {
        OtpData data = otpMap.get(email);
        if (data == null) return false;

        if (data.code.equals(inputOtp) && LocalDateTime.now().isBefore(data.expiry)) {
            return true;
        }
        return false;
    }

    public boolean updatePassword(String username, String newPassword) {
        List<User> users = getUsers();
        Optional<User> userOpt = users.stream().filter(u -> u.getUsername().equals(username)).findFirst();
        if (userOpt.isPresent()) {
            User u = userOpt.get();
            u.setPassword(newPassword);
            saveUsers(users);
            otpMap.remove(username); // Clear OTP on success
            return true;
        }
        return false;
    }

    public boolean isUsernameExists(String username) {
        List<User> users = getUsers();
        return users.stream().anyMatch(u -> u.getUsername().equals(username));
    }

    public boolean register(String username, String password) {
        if (isUsernameExists(username)) {
            return false;
        }
        List<User> users = getUsers();
        int newId = getNextId(users);
        User newUser = new User(newId, username, password);
        users.add(newUser);
        saveUsers(users);
        return true;
    }

    public User login(String username, String password) {
        List<User> users = getUsers();
        return users.stream()
                .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }
}