package com.yourbank.dao.postgres;

import com.yourbank.dao.CustomerDao;
import com.yourbank.model.Customer;

import java.util.Optional;

public class PostgresCustomerDao implements CustomerDao {
    @Override
    public Optional<Customer> getCustomerById(String customerId) {
        return Optional.empty();
    }

    @Override
    public Optional<Customer> getCustomerByUsername(String username) {
        return Optional.empty();
    }

    @Override
    public void updateCustomer(Customer customer) {

    }

    @Override
    public Customer registerCustomer(Customer customer) {
        return null;
    }
}
