package com.bank.dao;
import com.bank.model.Customer;
import java.util.Optional;

public interface CustomerDao {
    Optional<Customer> getCustomerById(String customerId);
    Optional<Customer> getCustomerByUsername(String username);
    void updateCustomer(Customer customer);
    Customer registerCustomer(Customer customer);
    Optional<Customer> getCustomerByEmail(String email);
}
