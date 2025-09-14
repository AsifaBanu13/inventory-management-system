package Services;

import models.Product;
import java.util.*;

public class inventorymanagementsystem {
    private Map<Integer, Product> productList = new HashMap<>();

    // Input helper to add product with validations
    public Product inputHelper(Scanner scanner) {
        try {
            System.out.print("Enter Product ID: ");
            int id = scanner.nextInt();
            scanner.nextLine(); // consume newline

            if (productList.containsKey(id)) {
                throw new IllegalArgumentException("⚠ Product with this ID already exists.");
            }

            System.out.print("Enter Product Name: ");
            String name = scanner.nextLine();

            System.out.print("Enter Product Type: ");
            String type = scanner.nextLine();

            System.out.print("Enter Quantity: ");
            int qty = scanner.nextInt();
            if (qty < 0) throw new IllegalArgumentException("⚠ Quantity cannot be negative.");

            System.out.print("Enter Price: ");
            double price = scanner.nextDouble();
            if (price < 0) throw new IllegalArgumentException("⚠ Price cannot be negative.");

            return new Product(id, name, type, qty, price);

        } catch (InputMismatchException e) {
            System.out.println("⚠ Invalid input! Please enter correct data type.");
            scanner.nextLine(); // clear buffer
        } catch (IllegalArgumentException e) {
            System.out.println("⚠ " + e.getMessage());
        }
        return null;
    }

    // Add Product
    public void addProduct(Scanner scanner) {
        Product newProduct = inputHelper(scanner);
        if (newProduct != null) {
            productList.put(newProduct.getProductId(), newProduct);
            System.out.println("✅ Product added successfully!");
        } else {
            System.out.println("⚠ Product could not be added.");
        }
    }

    // View All Products
    public void viewAllProducts() {
        if (productList.isEmpty()) {
            System.out.println("⚠ Product List is empty!");
        } else {
            for (Product product : productList.values()) {
                System.out.println(product);
            }
        }
    }

    // Search Product by Name
    public void searchProductByName(String name) {
        boolean found = false;
        for (Product product : productList.values()) {
            if (product.getProductName().equalsIgnoreCase(name)) {
                System.out.println(product);
                found = true;
            }
        }
        if (!found) {
            System.out.println("⚠ No product found with name: " + name);
        }
    }

    // 🔹 Search Product by ID
    public void searchProductById(int id) {
        Product product = productList.get(id);
        if (product != null) {
            System.out.println(product);
        } else {
            System.out.println("⚠ No product found with ID: " + id);
        }
    }

    // Remove Product
    public void removeProduct(int id) {
        if (productList.containsKey(id)) {
            productList.remove(id);
            System.out.println("✅ Product removed successfully!");
        } else {
            System.out.println("⚠ Product with ID " + id + " not found.");
        }
    }

    // Update Product Quantity
    public void updateProductQuantity(int id, int newQty) {
        Product product = productList.get(id);
        if (product == null) {
            System.out.println("⚠ Cannot update. Product with ID " + id + " not found.");
        } else if (newQty < 0) {
            System.out.println("⚠ Quantity cannot be negative.");
        } else {
            product.setAvailableQty(newQty);
            System.out.println("✅ Product updated successfully: " + product);
        }
    }
}
