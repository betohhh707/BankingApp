package com.bank.dao.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.bank.config.MongoDBConnection;
import com.bank.dao.AccountDao;
import com.bank.model.AccountStatus;
import com.bank.model.AccountType;
import com.bank.model.BankAccount;
import org.bson.Document;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MongoAccountDao implements AccountDao {
    //create connection objects
    private MongoCollection<Document> accounts = MongoDBConnection.getDatabase().getCollection("accounts");

    //method openAccount utilizes helper method .append to build document for mongoDB then sends
    //the fields to be added to db
    @Override
    public BankAccount openAccount(BankAccount account) {
            Document doc = new Document("customerId",account.getCustomerId())
                    .append("accountType",account.getAccountType().name())
                    .append("accountStatus",account.getAccountStatus().name())
                    .append("balance",account.getBalance()!= null ? new Decimal128(account.getBalance()) : new Decimal128(BigDecimal.ZERO));
            accounts.insertOne(doc);
            String newId = doc.getObjectId("_id").toHexString();
            return new BankAccount(newId, account.getCustomerId(),account.getAccountType(),account.getAccountStatus(),account.getBalance());
    }
    //returns optional because customer can have null account
    @Override
    public Optional<BankAccount> getAccountById(String accountId) {
        Document found = accounts.find(Filters.eq("_id", new ObjectId(accountId))).first();
        BankAccount account = documentToAccount(found);
        return account != null ? Optional.of(account):Optional.empty();
    }
    // Updates a specific account document by its unique ID.
    // Modifies exactly 3 fields as one single piece using the Java driver's Updates.combine helper.
    @Override
    public void updateAccount(BankAccount account) {
        //update the account where account id = ?
        accounts.updateOne(Filters.eq("_id", new ObjectId(account.getAccountId())),
                //if you want to add more than one field update gotta use combine
                Updates.combine(
                        Updates.set("accountType", account.getAccountType().name()),
                        Updates.set("accountStatus", account.getAccountStatus().name()),
                        Updates.set("balance", new Decimal128(account.getBalance()))
                )
        );
    }
    //loops through all accounts of customer given customer id and can return multiple accounts
    @Override
    public List<BankAccount> getAccountsByCustomerId(String customerId) {
        List<BankAccount> results = new ArrayList<>();
        for(Document doc : accounts.find(Filters.eq("customerId",customerId))){
            results.add(documentToAccount(doc));
        }
        return results;
    }

    //helper method for reusable code
    private BankAccount documentToAccount(Document doc) {
        if (doc == null) {
            return null;
        }
        String accountId = doc.getObjectId("_id").toHexString();
        String customerId = doc.getString("customerId");
        AccountType accountType = AccountType.valueOf(doc.getString("accountType"));
        AccountStatus accountStatus = AccountStatus.valueOf(doc.getString("accountStatus"));

        // Handles numeric balance conversion safely
        BigDecimal balance = doc.get("balance", Decimal128.class).bigDecimalValue();

        return new BankAccount(accountId, customerId, accountType, accountStatus, balance);
    }
}
