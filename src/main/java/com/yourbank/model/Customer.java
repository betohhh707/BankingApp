package com.yourbank.model;

public class Customer {
    private String customerId;
    private String firstName;
    private String lastName;
    private String username;
    private String hashedPassword;
    private String email;

    // Constructor 1 used when the DAO loads an existing customer from the database
    public Customer(String customerId, String firstName, String lastName,
                    String username, String hashedPassword, String email) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.email = email;
    }

    // Constructor 2 used when the service layer creates a brand-new customer, before an ID exists
    public Customer(String firstName, String lastName, String username,
                    String hashedPassword, String email) {
        this(null, firstName, lastName, username, hashedPassword, email);
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}