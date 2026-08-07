package com.bank.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

public class MongoDBConnection {

    private static MongoClient mongoClient;

    static {
        Properties props = new Properties();

        try {
            InputStream input = MongoDBConnection.class.getClassLoader().getResourceAsStream("db.properties");
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load db.properties", e);
        }

        String uri = props.getProperty("mongo.uri");
        mongoClient = MongoClients.create(uri);
    }

    public static MongoDatabase getDatabase() {
        return mongoClient.getDatabase("banking_app");
    }
}