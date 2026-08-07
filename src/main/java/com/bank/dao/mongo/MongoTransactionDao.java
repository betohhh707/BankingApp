package com.bank.dao.mongo;

import com.mongodb.client.model.Filters;
import com.bank.config.MongoDBConnection;
import com.bank.dao.TransactionDao;

import com.mongodb.client.MongoCollection;
import com.bank.model.Transaction;
import com.bank.model.TransactionType;
import org.bson.Document;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class MongoTransactionDao implements TransactionDao {

    private MongoCollection<Document> transactions = MongoDBConnection.getDatabase().getCollection("transactions");

    //most get methods follow the same get from AccountDao. in this case getAccountsByCustomerId was used for reference
    //creates new list of transactions. The logic then loops for however many accounts there is
    //then adds it to the result variable to sends that variable where requested
    @Override
    public List<Transaction> getTransactionsByAccountId(String accountId) {
        List<Transaction> results = new ArrayList<>();
        for(Document doc : transactions.find(Filters.eq("accountId",accountId))){
            results.add(documentToTransaction(doc));
        }
        return results;
    }

    //same idea as the get account by id but this one is looking for transaction given transaction id
    @Override
    public Optional<Transaction> getTransactionByTransactionId(String transactionId) {
        Document found = transactions.find(Filters.eq("_id",new ObjectId(transactionId))).first();
        Transaction transaction = documentToTransaction(found);
        return transaction != null ? Optional.of(transaction):Optional.empty();
    }


    //inserts transaction doc to mongo collection, does conversions for amount and balance.
    //creates new transaction id using generated mongo hex string conversion
    @Override
    public Transaction recordTransaction(Transaction transaction) {
        //converting date for mongo
        Date mongoDate = Date.from(transaction.getDate().atZone(ZoneId.systemDefault()).toInstant());
        Document doc = new Document("accountId", transaction.getAccountId())
                .append("amount", transaction.getAmount()!= null ? new Decimal128(transaction.getAmount()) : new Decimal128(BigDecimal.ZERO))
                .append("date", mongoDate)
                .append("type", transaction.getType().name())
                .append("resultingBalance", transaction.getResultingBalance()!= null ? new Decimal128(transaction.getResultingBalance()) : new Decimal128(BigDecimal.ZERO));
        transactions.insertOne(doc);
        //string conversion
        String newId = doc.getObjectId("_id").toHexString();
        return new Transaction(newId, transaction.getAccountId(), transaction.getAmount(),transaction.getDate(),transaction.getType(),transaction.getResultingBalance());
    }

    //helper for reusable code
    private Transaction documentToTransaction(Document doc) {

        if (doc == null) {
            return null;
        }
        String transactionId = doc.get("_id").toString();
        String accountId = doc.getString("accountId");
        BigDecimal amount = doc.get("amount", Decimal128.class).bigDecimalValue();
        // reading: Data field from the document
        Date mongoDate = doc.getDate("date");
        // reading: Date -> LocalDateTime
        LocalDateTime date = mongoDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        TransactionType type = TransactionType.valueOf(doc.getString("type"));
        BigDecimal resultingBalance = doc.get("resultingBalance", Decimal128.class).bigDecimalValue();

        return new Transaction(transactionId, accountId, amount, date,type, resultingBalance);
    }
}
