package com.mycompany.app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import redis.clients.jedis.JedisPubSub;

public class OrderUI {
    private final DefaultListModel<String> buyOrdersModel = new DefaultListModel<>();
    private final DefaultListModel<String> sellOrdersModel = new DefaultListModel<>();
    private final OrderDatabaseManager databaseManager;

    public OrderUI(OrderDatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        initializeUI();
    }

    private void initializeUI() {
        JFrame frame = new JFrame("Stock Market Orders");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        // Setup UI components
        JPanel assetPanel = setupAssetPanel();
        JPanel rightPanel = setupOrderCreationPanel();

        frame.add(assetPanel, BorderLayout.CENTER);
        frame.add(rightPanel, BorderLayout.EAST);

        // Subscribe to updates
        subscribeToUpdates();

        frame.setVisible(true);
    }

    private JPanel setupAssetPanel() {
        JPanel assetPanel = new JPanel(new BorderLayout());

        JLabel assetLabel = new JLabel("Asset: Stock XYZ", JLabel.CENTER);

        JList<String> buyList = new JList<>(buyOrdersModel);
        JList<String> sellList = new JList<>(sellOrdersModel);

        JPanel buyPanel = new JPanel(new BorderLayout());
        buyPanel.add(new JLabel("Best Buy Orders", JLabel.CENTER), BorderLayout.NORTH);
        buyPanel.add(new JScrollPane(buyList), BorderLayout.CENTER);

        JPanel sellPanel = new JPanel(new BorderLayout());
        sellPanel.add(new JLabel("Best Sell Orders", JLabel.CENTER), BorderLayout.NORTH);
        sellPanel.add(new JScrollPane(sellList), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, buyPanel, sellPanel);
        splitPane.setResizeWeight(0.5);

        assetPanel.add(assetLabel, BorderLayout.NORTH);
        assetPanel.add(splitPane, BorderLayout.CENTER);

        return assetPanel;
    }

    private JPanel setupOrderCreationPanel() {
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        JLabel newOrderLabel = new JLabel("Create New Order", JLabel.CENTER);
        JButton buyButton = new JButton("Create Buy Order");
        JButton sellButton = new JButton("Create Sell Order");

        buyButton.addActionListener(createOrderAction("Buy"));
        sellButton.addActionListener(createOrderAction("Sell"));

        rightPanel.add(newOrderLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(buyButton);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(sellButton);
        rightPanel.add(Box.createVerticalGlue());

        return rightPanel;
    }

    private ActionListener createOrderAction(String orderType) {
        return e -> {
            try {
                String priceInput = JOptionPane.showInputDialog(null, "Enter price:");
                String quantityInput = JOptionPane.showInputDialog(null, "Enter quantity:");

                if (priceInput != null && quantityInput != null) {
                    double price = Double.parseDouble(priceInput);
                    int quantity = Integer.parseInt(quantityInput);
                    String order = orderType + ": " + quantity + " @ $" + price;
                    databaseManager.publishOrder(order);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        };
    }

    private void subscribeToUpdates() {
        databaseManager.subscribeToOrders(new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                SwingUtilities.invokeLater(() -> {
                    if (message.startsWith("Buy")) {
                        buyOrdersModel.addElement(message);
                    } else if (message.startsWith("Sell")) {
                        sellOrdersModel.addElement(message);
                    }
                });
            }
        });
    }
}
