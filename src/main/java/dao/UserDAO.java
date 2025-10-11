package dao;

import Models.User;
import java.sql.SQLException;

public interface UserDAO {
    void addUser(User user) throws SQLException;
    User getUserByUsername(String username) throws SQLException;
}
