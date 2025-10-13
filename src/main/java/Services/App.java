package Services;

import Models.Product;
import Models.User;
import util.CSVHelper;
import util.DBConnection;
import dao.ProductDAOImpl;
import dao.UserDAOImpl;
import Services.inventorymanagementsystem;
import Services.UserService;
import exception.ValidationException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class App {

    private static final Scanner scanner = new Scanner(System.in);
    private static final UserService userService = new UserService();
    private static final inventorymanagementsystem inventoryManager = new inventorymanagementsystem();

    public static void main(String[] args) throws SQLException {
        System.out.println("==============================================");
        System.out.println("🛍️   Welcome to our E-Commerce App   🛍️");
        System.out.println("==============================================");

        while (true) {
            System.out.println("\n1️⃣ Login");
            System.out.println("2️⃣ Register");
            System.out.println("3️⃣ Exit");
            System.out.print("👉 Enter your choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> loginUser();
                case "2" -> registerUser();
                case "3" -> {
                    System.out.println("👋 Thank you for visiting! Goodbye!");
                    return;
                }
                default -> System.out.println("⚠️ Invalid choice! Please try again.");
            }
        }
    }

    private static void loginUser() throws SQLException {
        System.out.print("👤 Enter Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("🔑 Enter Password: ");
        String password = scanner.nextLine().trim();

        User loggedInUser = userService.login(username, password);

        if (loggedInUser == null) {
            System.out.println("❌ Invalid username or password. Please try again!");
            return;
        }

        System.out.println("\n✅ Login successful! Welcome, " + loggedInUser.getUsername());
        System.out.println("🎭 Role: " + loggedInUser.getRole());

        if (loggedInUser.getRole().equalsIgnoreCase("ADMIN")) {
            adminMenu();
        } else {
            userMenu();
        }
    }

    private static void registerUser() throws SQLException {
        System.out.println("\n===== 📝 Register New User =====");
        System.out.print("👤 Enter Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("🔑 Enter Password: ");
        String password = scanner.nextLine().trim();

        System.out.print("🎭 Enter Role (ADMIN/USER): ");
        String role = scanner.nextLine().trim().toUpperCase();

        User newUser = new User(0, username, password, role);

        try {
            userService.register(newUser);
            System.out.println("✅ Registration successful! You can now login.");
        } catch (ValidationException e) {
            System.out.println("⚠️ " + e.getMessage());
        }

        // After registration, go to login
        loginUser();
    }

    // ========================= ADMIN MENU =========================
    private static void adminMenu() {
        while (true) {
            System.out.println("\n===== 🧑‍💼 ADMIN INVENTORY MENU =====");
            System.out.println("1️⃣ Add Product");
            System.out.println("2️⃣ View All Products");
            System.out.println("3️⃣ Search Product by ID");
            System.out.println("4️⃣ Update Product");
            System.out.println("5️⃣ Delete Product");
            System.out.println("6️⃣ Filter by Price Range");
            System.out.println("7️⃣ Export Report (CSV)");
            System.out.println("8️⃣ Logout");
            System.out.print("👉 Enter your choice: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> addProduct();
                case "2" -> viewAllProducts();
                case "3" -> searchProductById();
                case "4" -> updateProduct();
                case "5" -> deleteProduct();
                case "6" -> filterByPriceRange();
                case "7" -> exportReport();
                case "8" -> {
                    System.out.println("👋 Logging out...");
                    return;
                }
                default -> System.out.println("⚠️ Invalid choice! Try again.");
            }
        }
    }

    // ========================= USER MENU =========================
    private static void userMenu() {
        while (true) {
            System.out.println("\n===== 👤 USER MENU =====");
            System.out.println("1️⃣ View All Products");
            System.out.println("2️⃣ Search Product by ID");
            System.out.println("3️⃣ Filter by Price Range");
            System.out.println("4️⃣ Logout");
            System.out.print("👉 Enter your choice: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> viewAllProducts();
                case "2" -> searchProductById();
                case "3" -> filterByPriceRange();
                case "4" -> {
                    System.out.println("👋 Logging out...");
                    return;
                }
                default -> System.out.println("⚠️ Invalid choice! Try again.");
            }
        }
    }

    // ========================= PRODUCT METHODS =========================
    private static void addProduct() {
        try {
            System.out.print("🆔 Enter Product ID: ");
            int id = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("📦 Enter Product Name: ");
            String name = scanner.nextLine().trim();

            System.out.print("🏷️ Enter Category: ");
            String category = scanner.nextLine().trim();

            System.out.print("🔢 Enter Quantity: ");
            int quantity = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("💰 Enter Price: ");
            double price = Double.parseDouble(scanner.nextLine().trim());

            Product product = new Product(id, name, category, quantity, price);
            inventoryManager.addProduct(product);
            System.out.println("✅ Product added successfully!");
        } catch (Exception e) {
            System.out.println("⚠️ Error adding product: " + e.getMessage());
        }
    }

    private static void viewAllProducts() {
        List<Product> products = inventoryManager.getAllProductsForReport();
        if (products.isEmpty()) {
            System.out.println("⚠️ No products available!");
            return;
        }

        System.out.printf("%-5s %-15s %-15s %-10s %-10s%n", "ID", "Name", "Category", "Quantity", "Price");
        System.out.println("-----------------------------------------------------------");
        products.forEach(p ->
                System.out.printf("%-5d %-15s %-15s %-10d %-10.2f%n",
                        p.getId(), p.getName(), p.getCategory(), p.getQuantity(), p.getPrice())
        );
    }

    private static void searchProductById() {
        try {
            System.out.print("🔍 Enter Product ID: ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            inventoryManager.viewProductById(id);
        } catch (NumberFormatException e) {
            System.out.println("⚠️ Invalid ID! Please enter a number.");
        }
    }

    private static void updateProduct() {
        try {
            System.out.print("🆔 Enter Product ID to Update: ");
            int id = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("📦 Enter New Name: ");
            String name = scanner.nextLine().trim();

            System.out.print("🏷️ Enter New Category: ");
            String category = scanner.nextLine().trim();

            System.out.print("🔢 Enter New Quantity: ");
            int quantity = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("💰 Enter New Price: ");
            double price = Double.parseDouble(scanner.nextLine().trim());

            inventoryManager.updateProduct(id, name, category, quantity, price);
            System.out.println("✅ Product updated successfully!");
        } catch (Exception e) {
            System.out.println("⚠️ Error updating product: " + e.getMessage());
        }
    }

    private static void deleteProduct() {
        try {
            System.out.print("🗑️ Enter Product ID to Delete: ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            inventoryManager.deleteProduct(id);
            System.out.println("✅ Product deleted successfully!");
        } catch (Exception e) {
            System.out.println("⚠️ Error deleting product: " + e.getMessage());
        }
    }

    private static void filterByPriceRange() {
        try {
            System.out.print("💸 Enter Minimum Price: ");
            double min = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("💸 Enter Maximum Price: ");
            double max = Double.parseDouble(scanner.nextLine().trim());
            inventoryManager.filterProductsByPriceRange(min, max);
        } catch (NumberFormatException e) {
            System.out.println("⚠️ Please enter valid numbers for price range!");
        }
    }

    private static void exportReport() {
        List<Product> products = inventoryManager.getAllProductsForReport();
        try {
            CSVHelper.saveProducts(products);
            System.out.println("📑 Report generated successfully: products.csv");
        } catch (IOException e) {
            System.out.println("⚠️ Failed to generate report: " + e.getMessage());
        }
    }
}
