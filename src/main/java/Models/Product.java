package Models;
import exception.ValidationException;
public class Product {

    private int id;
    private String name;
    private String category;
    private int quantity;
    private double price;

    // Full constructor (id provided by user)
    public Product(int id, String name, String category, int quantity, double price) throws ValidationException{
        if (id <= 0) throw new ValidationException("❌ Product ID must be positive.");

        if (name == null || name.trim().isEmpty()) throw new ValidationException("❌ Product name cannot be empty.");

        if (category == null || category.trim().isEmpty()) throw new ValidationException("❌ Category cannot be empty.");

        if (quantity < 0) throw new ValidationException("❌ Quantity cannot be negative.");

        if (price < 0) throw new ValidationException("❌ Price cannot be negative.");

        this.id = id;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.price = price;
    }
    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    @Override
    public String toString() {
        return String.format("Product{id=%d, name='%s', category='%s', quantity=%d, price=%.2f}",
                id, name, category, quantity, price);
    }
}
