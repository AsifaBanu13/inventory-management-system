package dao;

import Models.User;
import java.sql.SQLException;

/**
 * Interface defining database operations for User entity.
 */
public interface UserDAO {

    /**
     * Adds a new user to the database.
     *
     * @param user the User object to add
     * @throws SQLException if a database error occurs (e.g., duplicate username/email)
     */
    void addUser(User user) throws SQLException;

    /**
     * Retrieves a user by their username.
     *
     * @param username the username to search
     * @return the User object if found, otherwise null
     * @throws SQLException if a database error occurs
     */
    User getUserByUsername(String username) throws SQLException;

    /**
     * Retrieves a user by their email.
     *
     * @param email the email to search
     * @return the User object if found, otherwise null
     * @throws SQLException if a database error occurs
     */
    User getUserByEmail(String email) throws SQLException;

    /**
     * Updates the OTP for a user and marks them as unverified.
     *
     * @param email the user's email
     * @param otp the OTP code to set
     * @throws SQLException if a database error occurs
     */
    void updateOTP(String email, String otp) throws SQLException;

    /**
     * Sets the verified status for a user and clears the OTP.
     *
     * @param email the user's email
     * @param verified true to mark verified, false otherwise
     * @throws SQLException if a database error occurs
     */
    void setVerified(String email, boolean verified) throws SQLException;
}
