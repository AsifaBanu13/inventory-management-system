package util;

import dao.ProductDAO;
import Models.Product;
import dao.ProductDAOImpl;

import java.io.FileWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ReportGenerator  {

    public static void generatePaginatedReportAuto(Connection conn) throws SQLException {
        ProductDAO dao = new ProductDAOImpl();
        int pageSize = 50; // 🔹 You can change this number as needed

        try {
            int totalProducts = dao.getTotalProductCount(conn);
            int totalPages = (int) Math.ceil((double) totalProducts / pageSize);

            System.out.println("📊 Total Products: " + totalProducts);
            System.out.println("📄 Generating reports in pages of " + pageSize + " products each...");
            System.out.println("➡️ Total pages to generate: " + totalPages);

            for (int page = 1; page <= totalPages; page++) {
                List<Product> products = dao.getProductsByPage(conn, page, pageSize);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
