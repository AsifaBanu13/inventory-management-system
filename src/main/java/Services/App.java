package Services;

import Models.Product;
import Models.User;
import dao.ProductDAOImpl;
import exception.ValidationException;
import util.CSVHelper;
import util.DBConnection;

import java.sql.Connection;
import java.util.List;
import java.util.Scanner;

public class App {

    private static final Scanner scanner = new Scanner(System.in);
    private static final UserService userService = new UserService();
    private static final inventorymanagementsystem inventoryManager = new inventorymanagementsystem();

    // ANSI colors
    private static final String RESET = "\u001B[0m";
    private static final String CYAN = "\u001B[36m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String MAGENTA = "\u001B[35m";

    public static void main(String[] args) {
        while (true) {
            printMainMenu();
            System.out.print(CYAN + "👉 Enter your choice: " + RESET);
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> loginUser();
                case "2" -> registerUser();
                case "3" -> verifyEmail();
                case "4" -> {
                    System.out.println(GREEN + "👋 Thank you for using Inventory System. Goodbye!" + RESET);
                    return;
                }
                default -> System.out.println(RED + "⚠️ Invalid choice. Please select 1-4." + RESET);
            }
        }
    }

    // ========================= MAIN MENU =========================
    private static void printMainMenu() {
        System.out.println(MAGENTA + "╔════════════════════════════════════════════════════╗" + RESET);
        System.out.println(MAGENTA + "║" + RESET + "  " + GREEN + "🌟  INVENTORY MANAGEMENT SYSTEM  🌟" + RESET
                + "  " + MAGENTA + "║" + RESET);
        System.out.println(MAGENTA + "╠════════════════════════════════════════════════════╣" + RESET);
        System.out.println(YELLOW + " 1. Login" + RESET);
        System.out.println(YELLOW + " 2. Register" + RESET);
        System.out.println(YELLOW + " 3. Verify Email" + RESET);
        System.out.println(YELLOW + " 4. Exit" + RESET);
        System.out.println(MAGENTA + "╚════════════════════════════════════════════════════╝" + RESET);
    }

    // ========================= LOGIN =========================
    private static void loginUser() {
        try {
            System.out.println(CYAN + "\n🔐 LOGIN" + RESET);
            System.out.print("Username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Password: ");
            String password = scanner.nextLine().trim();

            User user = userService.login(username, password);
            if (user != null) {
                System.out.println(GREEN + "✅ Login successful! Welcome, " + user.getUsername() + " (" + user.getRole() + ")" + RESET);

                if (user.getRole().equalsIgnoreCase("ADMIN")) {
                    adminMenu();
                } else {
                    userMenu();
                }
            } else {
                System.out.println(RED + "❌ Invalid username or password!" + RESET);
            }
        } catch (Exception e) {
            System.out.println(RED + "❌ Login failed: " + e.getMessage() + RESET);
        }
    }

    // ========================= REGISTER =========================
    private static void registerUser() {
        try {
            System.out.println(CYAN + "\n📝 REGISTER" + RESET);
            System.out.print("Username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Password: ");
            String password = scanner.nextLine().trim();
            System.out.print("Role (ADMIN/USER): ");
            String role = scanner.nextLine().trim().toUpperCase();
            System.out.print("Email: ");
            String email = scanner.nextLine().trim();

            User newUser = new User(username, password, role, email);
            userService.register(newUser);

            System.out.println(GREEN + "✅ User registered successfully!" + RESET);
            System.out.println(YELLOW + "📧 Please verify your email before login." + RESET);
        } catch (ValidationException ve) {
            System.out.println(RED + "⚠️ " + ve.getMessage() + RESET);
        } catch (Exception e) {
            System.out.println(RED + "❌ Registration failed: " + e.getMessage() + RESET);
        }
    }

    // ========================= VERIFY EMAIL =========================
    private static void verifyEmail() {
        try {
            System.out.println(CYAN + "\n📩 VERIFY EMAIL" + RESET);
            System.out.print("Enter your registered email: ");
            String email = scanner.nextLine().trim();

            User user = userService.getUserByEmail(email);
            if (user == null) {
                System.out.println(RED + "❌ No account found with this email." + RESET);
                return;
            }
            if (user.isVerified()) {
                System.out.println(GREEN + "✅ Email already verified. You can login now." + RESET);
                return;
            }

            // Send OTP
            OTPService.sendOTP(email);
            System.out.println(YELLOW + "📨 OTP sent to: " + email + RESET);

            System.out.print("Enter OTP: ");
            String enteredOTP = scanner.nextLine().trim();

            if (OTPService.validateOTP(email, enteredOTP)) {
                userService.setVerified(email);
                System.out.println(GREEN + "✅ Email verified successfully! You can now login." + RESET);
            } else {
                System.out.println(RED + "❌ Invalid OTP. Please try again." + RESET);
            }

        } catch (Exception e) {
            System.out.println(RED + "❌ Verification failed: " + e.getMessage() + RESET);
        }
    }

    // ========================= ADMIN MENU =========================
    private static void adminMenu() {
        while (true) {
            System.out.println(MAGENTA + "\n===== 🧑‍💼 ADMIN INVENTORY MENU =====" + RESET);
            System.out.println(YELLOW + "1️⃣ Add Product" + RESET);
            System.out.println(YELLOW + "2️⃣ View All Products" + RESET);
            System.out.println(YELLOW + "3️⃣ Search Product by ID" + RESET);
            System.out.println(YELLOW + "4️⃣ Update Product" + RESET);
            System.out.println(YELLOW + "5️⃣ Delete Product" + RESET);
            System.out.println(YELLOW + "6️⃣ Filter by Price Range" + RESET);
            System.out.println(YELLOW + "7️⃣ Export Report (CSV)" + RESET);
            System.out.println(YELLOW + "8️⃣ Logout" + RESET);
            System.out.print(CYAN + "👉 Enter your choice: " + RESET);
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
                    System.out.println(GREEN + "👋 Logging out..." + RESET);
                    return;
                }
                default -> System.out.println(RED + "⚠️ Invalid choice! Try again." + RESET);
            }
        }
    }

    // ========================= USER MENU =========================
    private static void userMenu() {
        while (true) {
            System.out.println(MAGENTA + "\n===== 👤 USER MENU =====" + RESET);
            System.out.println(YELLOW + "1️⃣ View All Products" + RESET);
            System.out.println(YELLOW + "2️⃣ Search Product by ID" + RESET);
            System.out.println(YELLOW + "3️⃣ Filter by Price Range" + RESET);
            System.out.println(YELLOW + "4️⃣ Logout" + RESET);
            System.out.print(CYAN + "👉 Enter your choice: " + RESET);
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> viewAllProducts();
                case "2" -> searchProductById();
                case "3" -> filterByPriceRange();
                case "4" -> {
                    System.out.println(GREEN + "👋 Logging out..." + RESET);
                    return;
                }
                default -> System.out.println(RED + "⚠️ Invalid choice! Try again." + RESET);
            }
        }
    }

    // ========================= PRODUCT METHODS =========================
    private static void addProduct() {
        try {
            System.out.print("🆔 Product ID: ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("📦 Name: ");
            String name = scanner.nextLine().trim();
            System.out.print("🏷️ Category: ");
            String category = scanner.nextLine().trim();
            System.out.print("🔢 Quantity: ");
            int quantity = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("💰 Price: ");
            double price = Double.parseDouble(scanner.nextLine().trim());

            Product product = new Product(id, name, category, quantity, price);
            inventoryManager.addProduct(product);
          //  System.out.println(GREEN + "✅ Product added successfully!" + RESET);
        } catch (Exception e) {
            System.out.println(RED + "⚠️ Error adding product: " + e.getMessage() + RESET);
        }
    }

    private static void viewAllProducts() {
        List<Product> products = inventoryManager.getAllProductsForReport();
        if (products.isEmpty()) {
            System.out.println(RED + "⚠️ No products available!" + RESET);
            return;
        }

        System.out.println("╔══════════════════════════════════════════════════════════════════════════════╗");
        System.out.printf("║ %-5s │ %-25s │ %-15s │ %-8s │ %-10s ║%n",
                "ID", "Name", "Category", "Qty", "Price");
        System.out.println("╠═════╪═════════════════════════════════╪═════════════════╪════════╪════════════╣");

        for (Product p : products) {
            System.out.printf("║ %-5d │ %-25s │ %-15s │ %-8d │ %-10.2f ║%n",
                    p.getId(),
                    p.getName(),
                    p.getCategory(),
                    p.getQuantity(),
                    p.getPrice());
        }

        System.out.println("╚═════╧═════════════════════════════════╧═════════════════╧════════╧════════════╝");
    }


    private static void searchProductById() {
        try {
            System.out.print("🔍 Enter Product ID: ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            inventoryManager.viewProductById(id);
        } catch (NumberFormatException e) {
            System.out.println(RED + "⚠️ Invalid ID! Please enter a number." + RESET);
        }
    }

    private static void updateProduct() {
        try {
            System.out.print("🆔 Enter Product ID to Update: ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("📦 New Name: ");
            String name = scanner.nextLine().trim();
            System.out.print("🏷️ New Category: ");
            String category = scanner.nextLine().trim();
            System.out.print("🔢 New Quantity: ");
            int quantity = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("💰 New Price: ");
            double price = Double.parseDouble(scanner.nextLine().trim());

            inventoryManager.updateProduct(id, name, category, quantity, price);
           // System.out.println(GREEN + "✅ Product updated successfully!" + RESET);
        } catch (Exception e) {
            System.out.println(RED + "⚠️ Error updating product: " + e.getMessage() + RESET);
        }
    }

    private static void deleteProduct() {
        try {
            System.out.print("🗑️ Enter Product ID to Delete: ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            inventoryManager.deleteProduct(id);
         //   System.out.println(GREEN + "✅ Product deleted successfully!" + RESET);
        } catch (Exception e) {
            System.out.println(RED + "⚠️ Error deleting product: " + e.getMessage() + RESET);
        }
    }

    private static void filterByPriceRange() {
        try {
            System.out.print("💸 Min Price: ");
            double min = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("💸 Max Price: ");
            double max = Double.parseDouble(scanner.nextLine().trim());
            inventoryManager.filterProductsByPriceRange(min, max);
        } catch (NumberFormatException e) {
            System.out.println(RED + "⚠️ Enter valid numbers for price range!" + RESET);
        }
    }

    private static void exportReport() {
        try {
            List<Product> products = inventoryManager.getAllProductsForReport();
            if (products.isEmpty()) {
                System.out.println(RED + "⚠️ No products available to generate report." + RESET);
                return;
            }
            String filePath = CSVHelper.saveProductsReport(products, "Admin");

            System.out.print("📧 Enter recipient email: ");
            String toEmail = scanner.nextLine().trim();

            EmailService.sendReport(
                    toEmail,
                    "📦 Inventory Report",
                    "Hello,\n\nPlease find attached the inventory report.\n\nBest regards,\nInventory System",
                    filePath
            );

            System.out.println(GREEN + "✅ Report generated and emailed successfully to " + toEmail + RESET);
        } catch (Exception e) {
            System.out.println(RED + "❌ Failed to generate or send report: " + e.getMessage() + RESET);
        }
    }
}
