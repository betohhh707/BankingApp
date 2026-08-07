package com.bank.dao.mongo;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.bank.config.MongoDBConnection;
import com.bank.dao.CustomerDao;
import com.bank.model.Customer;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.Optional;

public class MongoCustomerDao implements CustomerDao{

    private MongoCollection<Document> customers = MongoDBConnection.getDatabase().getCollection("customers");
    @Override
    public Optional<Customer> getCustomerById(String customerId) {
        try{
            Document found = customers.find(Filters.eq("_id", new ObjectId(customerId))).first();
            return Optional.ofNullable(documentToCustomer(found));
        }catch (IllegalArgumentException e){
            return Optional.empty();
        }
    }


    @Override
    public Optional<Customer> getCustomerByUsername(String username) {
           Document found = customers.find(Filters.eq("username", username)).first();
            return Optional.ofNullable(documentToCustomer(found));
    }

    @Override
    public void updateCustomer(Customer customer) {
        Bson filter = Filters.eq("_id", new ObjectId(customer.getCustomerId()));
        Bson update = Updates.combine(
          Updates.set("firstName", customer.getFirstName()),
          Updates.set("lastName", customer.getLastName()),
          Updates.set("username",customer.getUsername()),
          Updates.set("hashedPassword",customer.getHashedPassword()),
          Updates.set("email",customer.getEmail())
        );
        customers.updateOne(filter,update);
    }

    @Override
    public Customer registerCustomer(Customer customer) {
        //similar to postgres but a lot less code
        Document doc = new Document("firstName", customer.getFirstName())
                .append("lastName",customer.getLastName())
                .append("username",customer.getUsername())
                .append("hashedPassword",customer.getHashedPassword())
                .append("email",customer.getEmail());
        customers.insertOne(doc);

        String newId = doc.getObjectId("_id").toHexString();

        return new Customer(newId, customer.getFirstName(), customer.getLastName(), customer.getUsername(), customer.getHashedPassword(), customer.getEmail());
    }

    @Override
    public Optional<Customer> getCustomerByEmail(String email) {
        Document found = customers.find(Filters.eq("email", email)).first();
        return Optional.ofNullable(documentToCustomer(found));
    }

    //helper for reusable code
    private Customer documentToCustomer(Document doc) {
        if (doc == null) {
            return null;
        }
        String customerId = doc.get("_id").toString();
        String firstName = doc.getString("firstName");
        String lastName = doc.getString("lastName");
        String username = doc.getString("username");
        String hashedPassword = doc.getString("hashedPassword");
        String email = doc.getString("email");

        return new Customer(customerId, firstName, lastName, username, hashedPassword, email);
    }
}
