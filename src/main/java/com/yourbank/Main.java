package com.yourbank;

import com.yourbank.config.DatabaseConnection;
import com.yourbank.dao.postgres.PostgresAccountDao;
import com.yourbank.dao.postgres.PostgresCustomerDao;
import com.yourbank.dao.postgres.PostgresTransactionDao;
import com.yourbank.model.*;
import org.mindrot.jbcrypt.BCrypt;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            System.out.println("Connected successfully!");
            conn.close();
        } catch (SQLException e) {
            System.out.println("Connection failed:");
            e.printStackTrace();
        }


        PostgresCustomerDao customerDao = new PostgresCustomerDao();

        //Customer newCustomer = new Customer("Test", "User", "testuser1",
               //"fakeHashedPassword123", "testuser1@example.com");

        //Customer savedCustomer = customerDao.registerCustomer(newCustomer);

        //System.out.println("Saved customer ID: " + savedCustomer.getCustomerId());
        //System.out.println("Name: " + savedCustomer.getFirstName() + " " + savedCustomer.getLastName());
        //System.out.println("Username: " + savedCustomer.getUsername());

        /*Optional<Customer> found = customerDao.getCustomerById("1");
        if (found.isPresent()) {
            System.out.println("Found: " + found.get().getFirstName()
                    + " " + found.get().getLastName()+
                    " "+found.get().getUsername());
        } else {
            System.out.println("No customer found with that ID.");
        }
        if (found.isPresent()) {
            Customer customer = found.get();
            customer.setEmail("updated@example.com");   // or setFirstName, whichever you want to test

            customerDao.updateCustomer(customer);
            System.out.println("Update sent.");
        } else {
            System.out.println("No customer found with that ID.");
        }

         */
        // 1. Open a new checking account for customer 1 (your test customer)
       /* PostgresAccountDao accountDao = new PostgresAccountDao();

        BankAccount newAccount = new BankAccount("1", AccountType.CHECKING);
        BankAccount savedAccount = accountDao.openAccount(newAccount);
        System.out.println("Opened account ID: " + savedAccount.getAccountId());
        System.out.println("Type: " + savedAccount.getAccountType() + ", Status: " + savedAccount.getAccountStatus() + ", Balance: " + savedAccount.getBalance());

        // 2. Fetch it back by ID
        Optional<BankAccount> found = accountDao.getAccountById(savedAccount.getAccountId());
        System.out.println("Found by ID: " + found.isPresent());

        // 3. Fetch all accounts for customer 1
        List<BankAccount> customerAccounts = accountDao.getAccountsByCustomerId("1");
        System.out.println("Customer 1 has " + customerAccounts.size() + " account(s).");
        */
        PostgresTransactionDao transactionDao = new PostgresTransactionDao();

        // 1. Record a new deposit transaction
        /*Transaction newTransaction = new Transaction("1", new BigDecimal("100.00"),
                LocalDateTime.now(), TransactionType.DEPOSIT, new BigDecimal("100.00"));
        Transaction saved = transactionDao.recordTransaction(newTransaction);
        System.out.println("Recorded transaction ID: " + saved.getTransactionId());
        System.out.println("Amount: " + saved.getAmount() + ", Type: " + saved.getType() + ", Resulting balance: " + saved.getResultingBalance());

        // 2. Fetch it back by ID
        Optional<Transaction> found = transactionDao.getTransactionByTransactionId(saved.getTransactionId());
        System.out.println("Found by ID: " + found.isPresent());

        // 3. Fetch all transactions for account 1
        List<Transaction> accountTransactions = transactionDao.getTransactionsByAccountId("1");
        System.out.println("Account 1 has " + accountTransactions.size() + " transaction(s).");
        */

        String rawPassword = "mySecret123";

        String hashed = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        System.out.println("Hashed: " + hashed);

        boolean matches = BCrypt.checkpw(rawPassword, hashed);
        System.out.println("Password matches: " + matches);

        boolean wrongMatch = BCrypt.checkpw("wrongPassword", hashed);
        System.out.println("Wrong password matches: " + wrongMatch);
    }
}