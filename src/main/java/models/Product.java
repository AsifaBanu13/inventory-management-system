package models;

public class Product {
    private int productId;
    private String productName;
    private String productType;
    private int availableQty;
    private double price;

    // Constructor
    public Product(int productId, String productName, String productType, int availableQty, double price) {
        this.productId = productId;
        this.productName = productName;
        this.productType = productType;
        this.availableQty = availableQty;
        this.price = price;
    }

    // Getters
    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductType() {
        return productType;
    }

    public int getAvailableQty() {
        return availableQty;
    }

    public double getPrice() {
        return price;
    }

    // Setters
    public void setAvailableQty(int availableQty) {
        this.availableQty = availableQty;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // To string method for display
    @Override
    public String toString() {
        return "Product ID: " + productId +
                ", Name: " + productName +
                ", Type: " + productType +
                ", Quantity: " + availableQty +
                ", Price: " + price;
    }
}
