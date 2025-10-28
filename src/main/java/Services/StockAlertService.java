package Services;

import Models.Product;
import dao.ProductDAO;
import dao.ProductDAOImpl;
import jakarta.mail.MessagingException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Service to monitor product stock levels and automatically notify admin when stock is low.
 */
public class StockAlertService {

    private static final Logger LOGGER = Logger.getLogger(StockAlertService.class.getName());

    private final ProductDAO dao = new ProductDAOImpl();
    private static final String ADMIN_EMAIL = System.getenv("ADMIN_EMAIL"); // set this in environment

    /**
     * ✅ Reusable method: Returns list of products that are below threshold.
     */
    public List<Product> checkLowStock() {
        List<Product> lowStock = new ArrayList<>();
        try {
            List<Product> products = dao.getAllProducts();
            for (Product p : products) {
                if (p.getQuantity() <= p.getThreshold()) {
                    lowStock.add(p);
                }
            }
        } catch (Exception e) {
            LOGGER.warning("Error checking low-stock products: " + e.getMessage());
        }
        return lowStock;
    }

    /**
     * ✅ Automated scheduler-friendly method: checks stock and sends email alert if needed.
     */
    public void checkStockAlerts() {
        try {
            // Step 1: Skip silently if no admin email configured
            if (ADMIN_EMAIL == null || ADMIN_EMAIL.isBlank()) {
                return;
            }

            // Step 2: Get all low-stock items
            List<Product> lowStock = checkLowStock();

            // Step 3: Send a consolidated alert if there are any
            if (!lowStock.isEmpty()) {
                try {
                    EmailService.sendLowStockAlert(lowStock, ADMIN_EMAIL);
                    // Uncomment if you want light logs instead of console spam:
                    // LOGGER.info("Low-stock alert sent to " + ADMIN_EMAIL + " (" + lowStock.size() + " items)");
                } catch (MessagingException e) {
                    LOGGER.warning("Failed to send stock alert email: " + e.getMessage());
                }
            }

        } catch (Exception e) {
            LOGGER.warning("Unexpected error in stock alert check: " + e.getMessage());
        }
    }
}
