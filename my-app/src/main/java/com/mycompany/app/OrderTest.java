package com.mycompany.app;

import redis.clients.jedis.Jedis;
import java.util.List;
import java.util.Scanner;

public class OrderTest {
    public static void main(String[] args) {
        try (Jedis jedisTrader = new Jedis("localhost", 6379);
             Scanner scanner = new Scanner(System.in)) {

            while (true) {
                // Display the menu
                System.out.println("\nWelcome to the Order Management System! You are currently trading Apple stock.");
                System.out.println("1. Buy Order");
                System.out.println("2. Sell Order");
                System.out.println("3. View Order Book");
                System.out.println("4. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        createOrder(jedisTrader, scanner, "BUY");
                        break;
                    case 2:
                        createOrder(jedisTrader, scanner, "SELL");
                        break;
                    case 3:
                        displayOrderBook(jedisTrader);
                        break;
                    case 4:
                        System.out.println("Exiting the system. Goodbye!");
                        return; // Exit the loop
                    default:
                        System.out.println("Invalid choice! Please try again.");
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred while managing orders: " + e.getMessage());
        }
    }

    // Method to create and add an order to Redis
    private static void createOrder(Jedis jedis, Scanner scanner, String side) {
        scanner.nextLine(); // Consume newline
        // System.out.print("Enter ticker symbol: ");
        String ticker = "AAPL"; //ONE TICKER FOR NOW

        System.out.print("Enter price: "); 
        double price = scanner.nextDouble();

        System.out.print("Enter quantity: ");
        int quantity = scanner.nextInt();

        scanner.nextLine(); // Consume newline
        // System.out.print("Enter order type (LIMIT/MARKET): "); // dont bother with input validation, will just make a UI later. 
        // All orders are limit order (at designated price) for now. Change it to be market or limit order in the UI.
        String type = "LIMIT";

        // Create the order
        Order order = new Order(side, ticker, price, quantity, type);

        // Add the order to Redis
        addOrderToRedis(jedis, order);

        System.out.println("Order added successfully: " + order);
    }

    // Method to add the order to Redis
    private static void addOrderToRedis(Jedis jedis, Order order) {
        // Unique order ID
        String orderId = order.getOrderId();

        // Store the order in a Redis hash
        jedis.hset("order:" + orderId, "side", order.getSide());
        jedis.hset("order:" + orderId, "ticker", order.getTicker());
        jedis.hset("order:" + orderId, "price", String.valueOf(order.getPrice()));
        jedis.hset("order:" + orderId, "quantity", String.valueOf(order.getQuantity()));
        jedis.hset("order:" + orderId, "orderType", order.getType());

        // Add the order to a sorted set (buy or sell)
        if (order.getSide().equals("BUY")) {
            jedis.zadd("buy_orders", -order.getPrice(), orderId); // Negative for highest price priority
        } else if (order.getSide().equals("SELL")) {
            jedis.zadd("sell_orders", order.getPrice(), orderId); // Positive for lowest price priority
        }
    }

    // Method to display the order book (for visualization)
    private static void displayOrderBook(Jedis jedis) {
        System.out.println("\nOrder Book (Top 5 Buy Orders):");
        List<String> buyOrders = jedis.zrevrange("buy_orders", 0, 4);
        for (String orderId : buyOrders) {
            Order order = getOrderFromRedis(jedis, orderId);
            System.out.println(order);
        }

        System.out.println("\nOrder Book (Top 5 Sell Orders):");
        List<String> sellOrders = jedis.zrange("sell_orders", 0, 4);
        for (String orderId : sellOrders) {
            Order order = getOrderFromRedis(jedis, orderId);
            System.out.println(order);
        }
    }

    // Method to fetch an order from Redis based on the order ID
    private static Order getOrderFromRedis(Jedis jedis, String orderId) {
        // Fetch the order details from the Redis hash
        String side = jedis.hget("order:" + orderId, "side");
        String ticker = jedis.hget("order:" + orderId, "ticker");
        double price = Double.parseDouble(jedis.hget("order:" + orderId, "price"));
        int quantity = Integer.parseInt(jedis.hget("order:" + orderId, "quantity"));
        String type = jedis.hget("order:" + orderId, "orderType");

        return new Order(side, ticker, price, quantity, type);
    }
}
