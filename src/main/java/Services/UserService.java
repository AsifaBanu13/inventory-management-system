package Services;

import dao.UserDAO;
import dao.UserDAOImpl;
import Models.User;
import exception.ValidationException;

import java.sql.SQLException;

public class UserService {

    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAOImpl(); // Always persistent connection
    }

    /**
     * Login returns the User object if successful (password matches and verified).
     */
    public User login(String username, String password) throws ValidationException {
        try {
            User user = userDAO.getUserByUsername(username);
            if (user == null) {
                throw new ValidationException("❌ User not found!");
            }
            if (!user.getPassword().equals(password)) {
                throw new ValidationException("❌ Invalid password!");
            }
            if (!user.isVerified()) {
                throw new ValidationException("⚠️ Email not verified. Please verify before logging in.");
            }
            return user;
        } catch (SQLException e) {
            throw new ValidationException("Database error during login: " + e.getMessage());
        }
    }

    /**
     * Register stores user in DB. Throws ValidationException on bad input or SQL errors.
     */
    public void register(User user) throws ValidationException {
        if (user.getUsername() == null || user.getUsername().isEmpty())
            throw new ValidationException("Username cannot be empty!");
        if (user.getPassword() == null || user.getPassword().isEmpty())
            throw new ValidationException("Password cannot be empty!");
        if (user.getEmail() == null || user.getEmail().isEmpty())
            throw new ValidationException("Email cannot be empty!");

        try {
            userDAO.addUser(user);
        } catch (SQLException e) {
            throw new ValidationException("Failed to register user: " + e.getMessage());
        }
    }

    public User getUserByEmail(String email) throws ValidationException {
        try {
            User user = userDAO.getUserByEmail(email);
            if (user == null) {
                throw new ValidationException("❌ No user found with this email!");
            }
            return user;
        } catch (SQLException e) {
            throw new ValidationException("Database error: " + e.getMessage());
        }
    }

    /**
     * Set verified status after OTP validation.
     */
    public void setVerified(String email) throws ValidationException {
        try {
            userDAO.setVerified(email, true);
        } catch (SQLException e) {
            throw new ValidationException("Failed to set verification: " + e.getMessage());
        }
    }

    /**
     * Update OTP in DB for email verification.
     */
    public void updateOTP(String email, String otp) throws ValidationException {
        try {
            userDAO.updateOTP(email, otp);
        } catch (SQLException e) {
            throw new ValidationException("Failed to update OTP: " + e.getMessage());
        }
    }
}
