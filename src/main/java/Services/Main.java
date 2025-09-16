package Services;

import Services.inventorymanagementsystem;
import Models.Product;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        inventorymanagementsystem ims = new inventorymanagementsystem();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== Inventory Management System =====");
            System.out.println("1. Add Product");
            System.out.println("2. View All Products");
            System.out.println("3. Update Product");
            System.out.println("4. Delete Product");
            System.out.println("5. Search Product");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter Product Name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter Category: ");
                    String category = sc.nextLine();
                    System.out.print("Enter Quantity: ");
                    int quantity = sc.nextInt();
                    System.out.print("Enter Price: ");
                    double price = sc.nextDouble();
                    sc.nextLine();

                    Product product = new Product(name, category, quantity, price);
                    ims.addProduct(product);
                }
                case 2 -> ims.viewProducts();
                case 3 -> {
                    System.out.print("Enter Product Name to Update: ");
                    String name = sc.nextLine();
                    System.out.print("Enter New Quantity: ");
                    int quantity = sc.nextInt();
                    System.out.print("Enter New Price: ");
                    double price = sc.nextDouble();
                    sc.nextLine();
                    ims.updateProduct(name, quantity, price);
                }
                case 4 -> {
                    System.out.print("Enter Product Name to Delete: ");
                    String name = sc.nextLine();
                    ims.deleteProduct(name);
                }
                case 5 -> {
                    System.out.print("Enter Product Name to Search: ");
                    String name = sc.nextLine();
                    ims.searchProduct(name);
                }
                case 6 -> {
                    System.out.println("Exiting...");
                    sc.close();
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice! Try again.");
            }
        }
    }
}
