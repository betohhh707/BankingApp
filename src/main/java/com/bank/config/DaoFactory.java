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

//DaoFactory's job is to create and hand back the correct DAO objects
//(Postgres or Mongo) so other classes never need to know which
//concrete implementation they're actually using.
public class DaoFactory {

    private static String dbType;
    //if static is not used here, every caller would need their own DaoFactory instance:
    //DaoFactory factory = new DaoFactory();
    //CustomerDao customerDao = factory.getCustomerDao();
    //static works well here because DaoFactory holds no per-instance state — every part
    //of the app should always agree on the same db.type, so one shared, class-level
    //answer is correct rather than each instance potentially disagreeing
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