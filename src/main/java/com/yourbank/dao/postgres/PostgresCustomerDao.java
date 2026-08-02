package com.yourbank.dao.postgres;

import com.yourbank.config.DatabaseConnection;
import com.yourbank.dao.CustomerDao;
import com.yourbank.model.Customer;

import javax.swing.text.html.Option;
import java.sql.*;
import java.util.Optional;

public class PostgresCustomerDao implements CustomerDao {
    @Override
    public Optional<Customer> getCustomerById(String customerId) {
        String sql = "SELECT * FROM customers WHERE customerId =?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, Integer.parseInt(customerId));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                 String firstName =rs.getString("firstname");
                 String lastName = rs.getString("lastname");
                 String username = rs.getString("username");
                 String hashedPassword = rs.getString("hashedpassword");
                 String email = rs.getString("email");
                 Customer customer = new Customer(customerId,firstName,lastName,username,hashedPassword,email);
                 return Optional.of(customer);
            }else{
                return Optional.empty();
            }
            // return customer
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get customer by id", e);
        }
    }

    @Override
    public Optional<Customer> getCustomerByUsername(String username) {
        String sql = "SELECT * FROM customers WHERE username =?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                String customerId = String.valueOf(rs.getInt("customerid"));
                String firstName =rs.getString("firstname");
                String lastName = rs.getString("lastname");
                String hashedPassword = rs.getString("hashedpassword");
                String email = rs.getString("email");
                Customer customer = new Customer(customerId,firstName,lastName,username,hashedPassword,email);
                return Optional.of(customer);
            }else{
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get customer by username", e);
        }
    }

    @Override
    public void updateCustomer(Customer customer) {
        String sql = "UPDATE customers SET firstname= ?, lastname=?,username=?, hashedpassword=?,email=? WHERE customerid=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, customer.getFirstName());
            stmt.setString(2, customer.getLastName());
            stmt.setString(3, customer.getUsername());
            stmt.setString(4, customer.getHashedPassword());
            stmt.setString(5, customer.getEmail());
            stmt.setInt(6, Integer.parseInt(customer.getCustomerId()));

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update customer", e);
        }
    }

    @Override
    public Customer registerCustomer(Customer customer) {
        String sql = "INSERT INTO customers (firstname, lastname, username, hashedPassword, email) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // set the 5 parameters using stmt.setString(...)
            stmt.setString(1, customer.getFirstName());
            stmt.setString(2,customer.getLastName());
            stmt.setString(3, customer.getUsername());
            stmt.setString(4,customer.getHashedPassword());
            stmt.setString(5,customer.getEmail());
            // execute the update
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            // get the generated key back
            String newId =null;
            if (keys.next()) {
                newId = String.valueOf(keys.getInt(1));
            }
            // return a new Customer object, now with the ID filled in
            return new Customer(newId, customer.getFirstName(), customer.getLastName(),
                    customer.getUsername(), customer.getHashedPassword(), customer.getEmail());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to register customer", e);
        }
    }
}
