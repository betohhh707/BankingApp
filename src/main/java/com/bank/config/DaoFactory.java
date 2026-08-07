package com.bank.config;

import com.bank.dao.CustomerDao;
import com.bank.dao.mongo.MongoAccountDao;
import com.bank.dao.mongo.MongoCustomerDao;
import com.bank.dao.mongo.MongoTransactionDao;
import com.bank.dao.postgres.PostgresCustomerDao;
import com.bank.dao.AccountDao;
import com.bank.dao.postgres.PostgresAccountDao;
import com.bank.dao.TransactionDao;
import com.bank.dao.postgres.PostgresTransactionDao;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DaoFactory {

    private static String dbType;

    static {
        InputStream input = DaoFactory.class.getClassLoader().getResourceAsStream("app.properties");
        Properties props = new Properties();
        try {
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        dbType = props.getProperty("db.type");
    }

    public static CustomerDao getCustomerDao() {
        if(dbType.equals("postgres")){
            return new PostgresCustomerDao();
        } else if (dbType.equals("mongo")) {
            return new MongoCustomerDao();
        }
        throw new IllegalStateException("Unknown database type: "+ dbType);
    }

    public static AccountDao getAccountDao(){
        if(dbType.equals("postgres")){
            return new PostgresAccountDao();
        }else if(dbType.equals("mongo")){
            return new MongoAccountDao();
        }
        throw new IllegalStateException("Unknown database type: "+ dbType);
    }

    public static TransactionDao getTransactionDao(){
        if(dbType.equals("postgres")){
            return new PostgresTransactionDao();
        } else if (dbType.equals("mongo")) {
            return new MongoTransactionDao();
        }
        throw new IllegalStateException("Unknown database type: "+ dbType);
    }
}