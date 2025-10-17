package Services;

import dao.UserDAO;
import dao.UserDAOImpl;
import Models.User;
import exception.ValidationException;

public class UserService {
    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAOImpl();
    }

    /**
     * Login returns the User object if successful (password matches and verified).
     */
    public User login(String username, String password) throws Exception {
        try {
            User user = userDAO.getUserByUsername(username);
            if (user == null) {
                System.out.println("❌ User not found!");
                return null;
            }
            if (!user.getPassword().equals(password)) {
                System.out.println("❌ Invalid password!");
                return null;
            }
            if (!user.isVerified()) {
                System.out.println("⚠️ Email not verified. Please verify before logging in.");
                return null;
            }
            // success
            return user;
        } catch (Exception e) {
            throw new Exception("Error during login: " + e.getMessage(), e);
        }
    }

    /**
     * Register stores user in DB. Throws ValidationException on bad input or SQL errors.
     */
    public void register(User user) throws ValidationException {
        try {
            if (user.getUsername() == null || user.getUsername().isEmpty())
                throw new ValidationException("Username cannot be empty!");
            if (user.getPassword() == null || user.getPassword().isEmpty())
                throw new ValidationException("Password cannot be empty!");
            if (user.getEmail() == null || user.getEmail().isEmpty())
                throw new ValidationException("Email cannot be empty!");

            userDAO.addUser(user);
            // caller will notify user to verify email
        } catch (Exception e) {
            // bubble up as ValidationException for user-friendly message
            throw new ValidationException("Failed to register user: " + e.getMessage());
        }
    }
    public User getUserByEmail(String email) throws Exception {
        return userDAO.getUserByEmail(email);
    }

    // ✅ Add this to set verified status after OTP validation
    public void setVerified(String email) throws Exception {
        userDAO.setVerified(email, true);
    }
}
