package com.yourbank.service;

import com.yourbank.dao.CustomerDao;
import com.yourbank.dao.postgres.PostgresCustomerDao;
import com.yourbank.model.Customer;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class CustomerService {
    private CustomerDao customerDao = new PostgresCustomerDao();

}