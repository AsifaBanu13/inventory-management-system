package util;

import Models.Product;
import exception.ValidationException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVHelper {

    private static final String FILE_NAME = "products.csv";

    // Save all products (rewrites file)
    public static void saveProducts(List<Product> products) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            // Header
            writer.println("ID,Name,Category,Quantity,Price");
            for (Product p : products) {
                // Escape commas in strings to prevent CSV corruption
                String name = p.getName().replace(",", " ");
                String category = p.getCategory().replace(",", " ");
                writer.println(p.getId() + "," + name + "," + category + "," + p.getQuantity() + "," + p.getPrice());
            }
        }
    }

    // Load products from CSV
    public static List<Product> loadProducts() throws IOException {
        List<Product> products = new ArrayList<>();
        File file = new File(FILE_NAME);

        if (!file.exists()) return products; // Return empty list if file does not exist

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Skip CSV header
                }

                String[] parts = line.split(",", -1); // -1 keeps empty strings
                if (parts.length < 5) {
                    System.err.println("⚠ Skipping invalid CSV line (less than 5 columns): " + line);
                    continue;
                }

                try {
                    int id = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    String category = parts[2].trim();
                    int quantity = Integer.parseInt(parts[3].trim());
                    double price = Double.parseDouble(parts[4].trim());

                    // ✅ Validate name and category contain at least one letter
                    if (!name.matches(".*[a-zA-Z].*")) {
                        throw new ValidationException("Product name must contain at least one letter: " + name);
                    }
                    if (!category.matches(".*[a-zA-Z].*")) {
                        throw new ValidationException("Category must contain at least one letter: " + category);
                    }

                    // Create product
                    products.add(new Product(id, name, category, quantity, price));

                } catch (NumberFormatException ex) {
                    System.err.println("⚠ Skipping line due to invalid number: " + line);
                } catch (ValidationException ex) {
                    System.err.println("⚠ Skipping line due to validation error: " + line + " | Reason: " + ex.getMessage());
                }
            }
        }

        return products;
    }
}
