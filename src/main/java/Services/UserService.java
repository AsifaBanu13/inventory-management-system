package Services;

import dao.UserDAO;
import dao.UserDAOImpl;
import Models.User;
import exception.ValidationException;
import java.sql.SQLException;

public class UserService {
    private UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAOImpl();
    }

    /**
     * Handles user login by verifying username and password.
     *
     * @param username The username entered by the user.
     * @param password The password entered by the user.
     * @return true if login is successful, false otherwise.
     */
    public User login(String username, String password) {
        try {
            User user = userDAO.getUserByUsername(username);

            if (user == null) {
                System.out.println("❌ User not found!");
                return user;
            }
            if (user.getPassword().equals(password)) {
                System.out.println("✅ Login successful! Welcome, " + user.getUsername());
                System.out.println("🔹 Role: " + user.getRole());
                return user;
            } else {
                System.out.println("❌ Invalid password!");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public void register(User user) throws ValidationException {
        try {
            // Optional: validate user fields before saving
            if (user.getUsername() == null || user.getUsername().isEmpty()) {
                throw new ValidationException("Username cannot be empty!");
            }
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                throw new ValidationException("Password cannot be empty!");
            }

            // Save user to database
            userDAO.addUser(user);  // <-- make sure UserDAO has addUser method
        } catch (SQLException e) {
            throw new ValidationException("Error registering user: " + e.getMessage());
        }
    }
}
