package com.mycompany.app;

public class Order {
    private String orderId;       // Unique ID for the order
    private String orderSide;     // "BUY" or "SELL"
    private String orderTicker;   // The stock ticker symbol (e.g., "AAPL")
    private double orderPrice;    // Price for the order
    private int orderQuantity;    // Number of shares or units
    private String orderType;     // "LIMIT" or "MARKET"

    // Constructor to initialize an Order object
    public Order(String side, String ticker, double price, int quantity, String type) {
        this.orderId = generateOrderId(); // Generate a unique order ID
        this.orderSide = side;
        this.orderTicker = ticker;
        this.orderPrice = price;
        this.orderQuantity = quantity;
        this.orderType = type;
    }

    // Getter methods
    public String getOrderId() {
        return orderId;
    }

    public String getSide() {
        return orderSide;
    }

    public String getTicker() {
        return orderTicker;
    }

    public double getPrice() {
        return orderPrice;
    }

    public int getQuantity() {
        return orderQuantity;
    }

    public String getType() {
        return orderType;
    }

    // Setter methods
    public void setSide(String side) {
        this.orderSide = side;
    }

    public void setTicker(String ticker) {
        this.orderTicker = ticker;
    }

    public void setPrice(double price) {
        this.orderPrice = price;
    }

    public void setQuantity(int quantity) {
        this.orderQuantity = quantity;
    }

    public void setType(String type) {
        this.orderType = type;
    }

    // Generate a unique order ID (for simplicity, using a timestamp here)
    private String generateOrderId() {
        return "ORDER_" + System.currentTimeMillis();
    }

    // Method to represent the order as a string for easy display
    @Override
    public String toString() {
        return "Order ID: " + orderId +
               ", Side: " + orderSide +
               ", Ticker: " + orderTicker +
               ", Price: " + orderPrice +
               ", Quantity: " + orderQuantity +
               ", Type: " + orderType;
    }
}
