package Services;

import util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;
import jakarta.mail.Session;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Transport;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.InternetAddress;

public class OTPService {

    /**
     * Generate a 6-digit OTP, store it in users table (otp column) and return it.
     */
    public static String generateOTP(String email) throws Exception {
        String otp = String.format("%06d", new Random().nextInt(1_000_000));

        // store in DB via updateOTP style
        try (Connection con = DBConnection.getConnection()) {
            String sql = "UPDATE users SET otp = ?, verified = FALSE WHERE email = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, otp);
            ps.setString(2, email);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new Exception("No user with email: " + email);
            }
        }

        return otp;
    }

    /**
     * sendOTP: generates and sends OTP using EmailService
     */
    public static void sendOTP(String email) throws Exception {
        String otp = generateOTP(email);
        try {
            EmailService.sendOTP(email, otp);
        } catch (MessagingException mex) {
            throw new Exception("Failed to send OTP email: " + mex.getMessage(), mex);
        }
    }

    /**
     * Validate OTP the user entered; if matches DB, sets verified = true and clears otp.
     */
    public static boolean validateOTP(String email, String inputOtp) throws Exception {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT otp FROM users WHERE email = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String dbOtp = rs.getString("otp");
                if (dbOtp != null && dbOtp.equals(inputOtp)) {
                    // update verified flag and clear otp
                    PreparedStatement up = con.prepareStatement("UPDATE users SET verified = TRUE, otp = NULL WHERE email = ?");
                    up.setString(1, email);
                    up.executeUpdate();
                    return true;
                }
            }
        } catch (Exception e) {
            throw new Exception("Error validating OTP: " + e.getMessage(), e);
        }
        return false;
    }
}
