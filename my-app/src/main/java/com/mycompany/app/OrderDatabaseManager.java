package com.mycompany.app;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class OrderDatabaseManager {
    private final Jedis jedisPublisher;
    private final Jedis jedisSubscriber;

    public OrderDatabaseManager() {
        this.jedisPublisher = new Jedis("localhost", 6379);
        this.jedisSubscriber = new Jedis("localhost", 6379);
    }

    public void publishOrder(String order) {
        jedisPublisher.publish("order_channel", order);
    }

    public void subscribeToOrders(JedisPubSub subscriber) {
        new Thread(() -> jedisSubscriber.subscribe(subscriber, "order_channel")).start();
    }

    public void closeConnections() {
        jedisPublisher.close();
        jedisSubscriber.close();
    }
}
