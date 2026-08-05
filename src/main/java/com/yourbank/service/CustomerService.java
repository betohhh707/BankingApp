package com.yourbank.service;

import com.yourbank.dao.CustomerDao;
import com.yourbank.dao.postgres.PostgresCustomerDao;
import com.yourbank.model.Customer;
import org.mindrot.jbcrypt.BCrypt;

import javax.crypto.spec.OAEPParameterSpec;
import java.util.Optional;

public class CustomerService {
    private CustomerDao customerDao = new PostgresCustomerDao();

    public Customer registerCustomer(String firstName, String lastName, String username, String rawPassword, String email) {
        if (customerDao.getCustomerByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (customerDao.getCustomerByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already taken");
        }
        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        Customer newCustomer = new Customer(firstName, lastName, username, hashedPassword, email);
        return customerDao.registerCustomer(newCustomer);
    }

    public Optional<Customer> login(String username, String rawPassword) {
        Optional<Customer> foundCustomer = customerDao.getCustomerByUsername(username);

        if (foundCustomer.isEmpty()) {
            return Optional.empty();
        }
        Customer customer = foundCustomer.get();
        boolean matches = BCrypt.checkpw(rawPassword, customer.getHashedPassword());
        if (matches) {
            return Optional.of(customer);
        } else {
            return Optional.empty();
        }
    }

    public void updateCustomer(Customer customer) {
        Optional<Customer> existing = customerDao.getCustomerByUsername(customer.getUsername());

        //condition is false if it finds that the customer found is the same person
        //allowing them to keep their same username
        //.equals for comparing strings not ==
        //checks for username
        if (existing.isPresent() && !existing.get().getCustomerId().equals(customer.getCustomerId())) {
            throw new IllegalArgumentException("Username already taken");
        }

        Optional<Customer> existingEmail = customerDao.getCustomerByEmail(customer.getEmail());
        //checks for email
        //same logic as the username check
        if (existingEmail.isPresent() && !existingEmail.get().getCustomerId().equals(customer.getCustomerId())){
            throw new IllegalArgumentException("Email already taken");
        }

        customerDao.updateCustomer(customer);
    }
}