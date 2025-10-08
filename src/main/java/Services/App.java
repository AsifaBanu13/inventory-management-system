package Services;

import Models.Product;
import Models.User;
import exception.ValidationException;
import util.CSVHelper;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
       inventorymanagementsystem ims = new inventorymanagementsystem();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== 📊 Inventory Management System =====");
            System.out.println("1️⃣ Add Product");
            System.out.println("2️⃣ View All Products");
            System.out.println("3️⃣ Update Product");
            System.out.println("4️⃣ Delete Product");
            System.out.println("5️⃣ Search Product");
            System.out.println("6️⃣ View Product By ID");
            System.out.println("7️⃣ Generate Report (Export CSV)");
            System.out.println("8️⃣ Exit");
            System.out.print("👉 Enter your choice: ");



            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.err.println("⚠️ Please enter a valid number!");
                continue;
            }

            switch (choice) {
                case 1 -> {
                    try {
                        System.out.print("🆔 Enter Product ID: ");
                        int id = Integer.parseInt(sc.nextLine().trim());

                        System.out.print("📦 Enter Product Name: ");
                        String name = sc.nextLine().trim();

                        System.out.print("🏷️ Enter Category: ");
                        String category = sc.nextLine().trim();

                        System.out.print("🔢 Enter Quantity: ");
                        int quantity = Integer.parseInt(sc.nextLine().trim());

                        System.out.print("💰 Enter Price: ");
                        double price = Double.parseDouble(sc.nextLine().trim());

                        Product product = new Product(id, name, category, quantity, price);
                        ims.addProduct(product);
                     //   System.out.println("✅ Product added successfully!");
                    } catch (ValidationException e) {
                        System.err.println(e.getMessage());
                    } catch (NumberFormatException e) {
                        System.err.println("⚠️ Please enter numeric values for ID, Quantity, and Price!");
                    } catch (Exception e) {
                        System.err.println("⚠️ Unexpected error: " + e.getMessage());
                    }
                }

                case 2 -> ims.viewProducts();

                case 3 -> {
                    try {
                        System.out.print("🆔 Enter Product ID to Update: ");
                        int id = Integer.parseInt(sc.nextLine().trim());

                        System.out.print("📦 Enter New Name: ");
                        String name = sc.nextLine().trim();

                        System.out.print("🏷️ Enter New Category: ");
                        String category = sc.nextLine().trim();

                        System.out.print("🔢 Enter New Quantity: ");
                        int quantity = Integer.parseInt(sc.nextLine().trim());

                        System.out.print("💰 Enter New Price: ");
                        double price = Double.parseDouble(sc.nextLine().trim());

                        ims.updateProduct(id, name, category, quantity, price);
                    } catch (NumberFormatException e) {
                        System.err.println("⚠️ Invalid input! Enter numbers for ID, Quantity, and Price.");
                    }
                }

                case 4 -> {
                    try {
                        System.out.print("🆔 Enter Product ID to Delete: ");
                        int id = Integer.parseInt(sc.nextLine().trim());
                        ims.deleteProduct(id);
                    } catch (NumberFormatException e) {
                        System.err.println("⚠️ Product ID must be a number!");
                    }
                }

                case 5 -> {
                    System.out.print("📦 Enter Product Name to Search: ");
                    String name = sc.nextLine().trim();
                    ims.searchProduct(name);
                }

                case 6 -> {
                    try {
                        System.out.print("🆔 Enter Product ID: ");
                        int id = Integer.parseInt(sc.nextLine().trim());
                        ims.viewProductById(id);
                    } catch (NumberFormatException e) {
                        System.err.println("⚠️ Product ID must be numeric!");
                    }
                }

                case 7 -> generateReport(ims);

                case 8 -> {
                    System.out.println("👋 Exiting... Goodbye!");
                    sc.close();
                    System.exit(0);
                }

                default -> System.out.println("❌ Invalid choice! Please try again.");
            }
        }
    }

    private static void generateReport(inventorymanagementsystem ims) {
        List<Product> products = ims.getAllProductsForReport();
        try {
            CSVHelper.saveProducts(products);
            System.out.println("📑 Report generated successfully: products.csv");
        } catch (IOException e) {
            System.err.println("⚠️ Failed to generate report: " + e.getMessage());
        }


      //  User user=new User(1,"test","test","Admin");
      //  System.out.println(user.toString());
    }
}
