package com.mycompany.app;

public class MainApp {
    public static void main(String[] args) {
        OrderDatabaseManager databaseManager = new OrderDatabaseManager();

        Runtime.getRuntime().addShutdownHook(new Thread(databaseManager::closeConnections));

        new OrderUI(databaseManager);
    }
}
