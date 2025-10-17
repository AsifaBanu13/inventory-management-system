package util;

import dao.ProductDAO;
import dao.ProductDAOImpl;
import Models.Product;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ReportGenerator {

    public static void generatePaginatedReportAuto() {
        ProductDAO dao;
        dao = new ProductDAOImpl(); // DAO manages its own connection

        int pageSize = 50; // You can change page size

        try {
            int totalProducts = dao.getTotalProductCount();
            int totalPages = (int) Math.ceil((double) totalProducts / pageSize);

            System.out.println("📊 Total Products: " + totalProducts);
            System.out.println("📄 Generating reports in pages of " + pageSize + " products each...");
            System.out.println("➡️ Total pages to generate: " + totalPages);

            for (int page = 1; page <= totalPages; page++) {
                List<Product> products = dao.getProductsByPage(page, pageSize);
                String fileName = "products_page_" + page + ".csv";

                try (FileWriter writer = new FileWriter(fileName)) {
                    writer.write("ID,Name,Category,Quantity,Price\n");
                    for (Product p : products) {
                        writer.write(p.getId() + "," + p.getName() + "," + p.getCategory() + ","
                                + p.getQuantity() + "," + p.getPrice() + "\n");
                    }
                }

                System.out.println("✅ Generated: " + fileName + " (" + products.size() + " records)");
            }

            System.out.println("🎉 All paginated reports generated successfully!");
        } catch (SQLException | IOException e) {
            System.err.println("❌ Error generating paginated reports: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
