package com.mycompany.app;

import redis.clients.jedis.Jedis;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Create a Jedis instance pointing to localhost and the default Redis port (6379)
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            // Test connection
            System.out.println("Connected to Redis server successfully!");

            // Use Scanner to get input
            Scanner scanner = new Scanner(System.in);
            String[] keyStrings = new String[3];  // Array to store the names of 3 people

            // Loop to get details for 3 people
            for (int i = 0; i < 3; i++) {
                System.out.println("What is your name?");
                String name = scanner.nextLine();

                // Get age
                System.out.println("What is your age?");
                int age = scanner.nextInt();

                // Consume the newline character left by nextInt()
                scanner.nextLine(); 

                // Get sibling count
                System.out.println("How many siblings do you have?");
                int siblingCount = scanner.nextInt();
                scanner.nextLine();  // Consume the newline character again

                // Store the person's details in a Redis hash
                jedis.hset(name, "age", String.valueOf(age));
                jedis.hset(name, "sibling_count", String.valueOf(siblingCount));

                // Store the name in the array
                keyStrings[i] = name;
            }

            scanner.close();

            // Retrieve and print each person's details
            for (int j = 0; j < 3; j++) {
                String name = keyStrings[j];

                // Fetch age and sibling count from the Redis hash
                String age = jedis.hget(name, "age");
                String siblingCount = jedis.hget(name, "sibling_count");

                System.out.println("Details for " + name + ":");
                System.out.println("Age: " + age);
                System.out.println("Sibling Count: " + siblingCount);
                System.out.println();
            }
        } catch (Exception e) {
            System.err.println("An error occurred while testing Jedis: " + e.getMessage());
        }
    }
}
