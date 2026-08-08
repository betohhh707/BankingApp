package com.bank.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

public class MongoDBConnection {
    //built once used forever
    private static MongoClient mongoClient;

    static {
        Properties props = new Properties();

        try {
            //read text file for connection string
            InputStream input = MongoDBConnection.class.getClassLoader().getResourceAsStream("db.properties");
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load db.properties", e);
        }

        String uri = props.getProperty("mongo.uri");
        //actual connection to mongo using mongos driver
        mongoClient = MongoClients.create(uri);
    }
    //Here code is telling us what database to use for Mongo. In this case banking_app from MongoDB Compass
    public static MongoDatabase getDatabase() {
        return mongoClient.getDatabase("banking_app");
    }
}