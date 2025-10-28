package Services;

import Models.Product;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.File;
import java.util.List;
import java.util.Properties;

/**
 * EmailService can send plain or multipart emails.
 * Supports OTPs, reports (with optional attachments), and automatic stock alerts.
 */
public class EmailService {

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";

    // ✅ Create mail session with authentication
    private static Session createSession() throws MessagingException {
        final String fromEmail = System.getenv("MAIL_USER");
        final String password = System.getenv("MAIL_PASS");

        if (fromEmail == null || password == null) {
            throw new MessagingException("Email credentials not set. Set MAIL_USER and MAIL_PASS environment variables.");
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        return Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });
    }

    /**
     * ✅ Sends an email (optionally with attachment)
     */
    public static void sendReport(String toEmail, String subject, String body, String attachmentPath) throws MessagingException {
        Session session = createSession();
        String fromEmail = System.getenv("MAIL_USER");

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);

        if (attachmentPath == null || attachmentPath.isBlank()) {
            // Plain text email
            message.setText(body);
        } else {
            // Multipart email with attachment
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(body);

            MimeBodyPart attachmentPart = new MimeBodyPart();
            try {
                File file = new File(attachmentPath);
                if (!file.exists()) {
                    throw new MessagingException("Attachment not found: " + attachmentPath);
                }
                attachmentPart.attachFile(file);
            } catch (Exception e) {
                throw new MessagingException("Failed to attach file: " + e.getMessage(), e);
            }

            Multipart mp = new MimeMultipart();
            mp.addBodyPart(textPart);
            mp.addBodyPart(attachmentPart);
            message.setContent(mp);
        }

        Transport.send(message);
        // (silent — no console message)
    }

    /**
     * ✅ Sends OTP (simple wrapper, no attachment)
     */
    public static void sendOTP(String toEmail, String otp) throws MessagingException {
        String subject = "Your Inventory System OTP";
        String body = "Dear User,\n\nYour OTP for email verification is: " + otp +
                "\n\nIf you did not request this, ignore this email.\n\nRegards,\nInventory Team";
        sendReport(toEmail, subject, body, null);
    }

    /**
     * ✅ Sends a single product stock alert (used only if you want per-product alerts)
     */
    public static void sendAlert(String toEmail, String productName, int currentQty, int recommendedReorderQty) throws MessagingException {
        String subject = "⚠️ Low Stock Alert: " + productName;
        String body = "Dear Admin,\n\n"
                + "Product: " + productName + "\n"
                + "Current Quantity: " + currentQty + "\n"
                + "Recommended Reorder Quantity: " + recommendedReorderQty + "\n\n"
                + "Please restock this item soon to avoid shortage.\n\n"
                + "Regards,\nInventory Management System";

        sendReport(toEmail, subject, body, null);
    }

    /**
     * ✅ Sends one consolidated low-stock alert for multiple products.
     */
    public static void sendLowStockAlert(List<Product> lowStockProducts, String toEmail) throws MessagingException {
        if (lowStockProducts == null || lowStockProducts.isEmpty()) {
            return; // nothing to send
        }

        String subject = "⚠️ Low Stock Alert - Inventory System";

        StringBuilder body = new StringBuilder();
        body.append("Dear Admin,\n\nThe following products are running low on stock:\n\n");

        for (Product p : lowStockProducts) {
            body.append("• Product: ").append(p.getName())
                    .append(" | Quantity: ").append(p.getQuantity())
                    .append(" | Threshold: ").append(p.getThreshold())
                    .append("\n");
        }

        body.append("\nPlease restock these items soon to avoid shortage.\n\nRegards,\nInventory Management System");

        sendReport(toEmail, subject, body.toString(), null);
    }
}
