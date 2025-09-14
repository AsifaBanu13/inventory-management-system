package Services;
import java.util.*;


import java.util.Scanner;
import Services.inventorymanagementsystem;
//import java.sql.*;
public class Main {
  /*  private static final String url = "jdbc:mysql://localhost:3306/mydb";
    private static final String username = "root";

    private static final String password = "Asifa@1325";

*/
    public static void main(String[] args)  {
      /*  try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        try{
            Connection connection=DriverManager.getConnection(url, username, password);
       //     Statement statement = connection.createStatement();
        //    String query= "INSERT INTO students(name, age, marks) VALUES(Ankita, 21, 76.5)";
         //   String query= "UPDATE students SET marks = ? WHERE id= ?";
            String query = "DELETE FROM students WHERE id=?";
          //  Statement statement = connection.createStatement();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setDouble(1,87.5);
            preparedStatement.setInt(2, 3);

            int rowsAffected = preparedStatement.executeUpdate();
            if(rowsAffected>0){
                System.out.println("Data Updated Successfully");
            }else{
                System.out.println("Data Not Updated");
           }


        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
*/
                inventorymanagementsystem inventory = new inventorymanagementsystem();
                Scanner scanner = new Scanner(System.in);

                while (true) {
                    System.out.println("\n===== Inventory Management System =====");
                    System.out.println("1. Add Product");
                    System.out.println("2. View All Products");
                    System.out.println("3. Search Product by Name");
                    System.out.println("4. Search Product by ID");   // 🔹 New option
                    System.out.println("5. Remove Product");
                    System.out.println("6. Update Product Quantity");
                    System.out.println("7. Exit");
                    System.out.print("Enter your choice: ");

                    int choice;
                    try {
                        choice = scanner.nextInt();
                        scanner.nextLine(); // consume newline
                    } catch (InputMismatchException e) {
                        System.out.println("⚠ Invalid choice! Please enter a number.");
                        scanner.nextLine(); // clear buffer
                        continue;
                    }

                    switch (choice) {
                        case 1:
                            inventory.addProduct(scanner);
                            break;
                        case 2:
                            inventory.viewAllProducts();
                            break;
                        case 3:
                            System.out.print("Enter Product Name to search: ");
                            String name = scanner.nextLine();
                            inventory.searchProductByName(name);
                            break;
                        case 4:
                            System.out.print("Enter Product ID to search: ");
                            int searchId = scanner.nextInt();
                            inventory.searchProductById(searchId);
                            break;
                        case 5:
                            System.out.print("Enter Product ID to remove: ");
                            int removeId = scanner.nextInt();
                            inventory.removeProduct(removeId);
                            break;
                        case 6:
                            System.out.print("Enter Product ID to update: ");
                            int updateId = scanner.nextInt();
                            System.out.print("Enter new Quantity: ");
                            int newQty = scanner.nextInt();
                            inventory.updateProductQuantity(updateId, newQty);
                            break;
                        case 7:
                            System.out.println("✅ Exiting... Thank you!");
                            scanner.close();
                            System.exit(0);
                        default:
                            System.out.println("⚠ Invalid choice! Try again.");
                    }
                }
            }
        }

