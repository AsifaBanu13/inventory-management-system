package Services;

import jakarta.mail.*;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMultipart;

import java.io.File;
import java.util.Properties;

/**
 * EmailService can send a plain email (no attachment) or an email with one attachment.
 * sendReport(): attachmentPath may be null (used by OTP) or a valid path (CSV).
 */
public class EmailService {

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";

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
     * Sends an email. If attachmentPath is null, sends a simple text email.
     */
    public static void sendReport(String toEmail, String subject, String body, String attachmentPath) throws MessagingException {
        Session session = createSession();
        String fromEmail = System.getenv("MAIL_USER");

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);

        if (attachmentPath == null || attachmentPath.isBlank()) {
            // plain text email
            message.setText(body);
        } else {
            // multipart with attachment
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
    }

    /**
     * Convenience wrapper for sending OTP (no attachment).
     */
    public static void sendOTP(String toEmail, String otp) throws MessagingException {
        String subject = "Your Inventory System OTP";
        String body = "Dear User,\n\nYour OTP for email verification is: " + otp +
                "\n\nIf you did not request this, ignore this email.\n\nRegards,\nInventory Team";
        sendReport(toEmail, subject, body, null);
    }
}
