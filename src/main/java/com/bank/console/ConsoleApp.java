// ConsoleApp.java
package com.bank.console;

import com.bank.model.*;
import com.bank.service.CustomerService;
import com.bank.service.AccountService;
import com.bank.service.TransactionService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleApp {
    private Scanner scanner = new Scanner(System.in);
    private CustomerService customerService = new CustomerService();
    private AccountService accountService = new AccountService();
    private TransactionService transactionService = new TransactionService();

    public void run() {
        while (true) {
            System.out.println("1. Register\n2. Login\n3. Exit");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    // Registration
                    System.out.print("Enter first Name:");
                    String firstName = scanner.nextLine();

                    System.out.print("Enter last Name:");
                    String lastName = scanner.nextLine();

                    System.out.print("Enter a username:");
                    String username = scanner.nextLine();

                    System.out.print("Enter a password:");
                    String password= scanner.nextLine();

                    System.out.print("Enter a email:");
                    String email = scanner.nextLine();
                    try {
                        Customer newCustomer = customerService.registerCustomer(firstName, lastName, username, password, email);
                        System.out.println("Registration successful! Your customer ID is: " + newCustomer.getCustomerId());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Registration failed: " + e.getMessage());
                    }
                    break;
                case "2":
                    // login
                    System.out.print("Enter your username: ");
                    String customerUsername = scanner.nextLine();
                    System.out.print("Enter your password:");
                    String customerPassword = scanner.nextLine();
                    Optional<Customer> customer = customerService.login(customerUsername, customerPassword);
                    //if customer exists
                    if(customer.isPresent()) {
                        //fetch customer
                        Customer loggedInCustomer = customer.get();
                        System.out.println("Login successful! welcome " + loggedInCustomer.getFirstName());
                        boolean loggedIn = true;
                        //user choices
                        while(loggedIn){
                            System.out.println("1. View Accounts\n2. Open Account\n3. Deposit\n4. " +
                                    "Withdraw\n5. Transfer\n6. Transaction History\n7. Close Account\n8. Logout");
                            String innerInput = scanner.nextLine();
                            switch (innerInput) {
                                case "1":
                                    // view accounts
                                    List<BankAccount> accounts = accountService.viewAccounts(loggedInCustomer.getCustomerId());
                                    //iterate thru the customers accounts
                                    for(BankAccount account:accounts){
                                        System.out.println("Account ID:"+account.getAccountId()+", Type:"+ account.getAccountType()
                                                +", Status:"+ account.getAccountStatus()+", Balance:$"+ account.getBalance());
                                    }
                                    break;
                                case "2":
                                    // Open account
                                    System.out.print("Enter account type(CHECKING or SAVINGS): ");
                                    String type = scanner.nextLine();
                                    try {
                                        AccountType accountType = AccountType.valueOf(type.toUpperCase());
                                        BankAccount newAccount = accountService.openAccount(loggedInCustomer.getCustomerId(), accountType);
                                        System.out.println("Account opened. Account ID: " + newAccount.getAccountId());
                                    } catch (IllegalArgumentException e) {
                                        System.out.println("Invalid account type. Please enter CHECKING or SAVINGS.");
                                    }
                                    break;
                                case "3":
                                    //Deposit
                                    System.out.print("Enter account ID: ");
                                    String depositAccountId = scanner.nextLine();
                                    System.out.print("Enter deposit amount: ");
                                    String depositAmount = scanner.nextLine();

                                    try {
                                        BigDecimal amount = new BigDecimal(depositAmount);
                                        Transaction newDeposit = transactionService.deposit(depositAccountId,loggedInCustomer.getCustomerId(),amount);
                                        System.out.println("Deposit successful. New Balance: $"+ newDeposit.getResultingBalance());

                                    }catch (IllegalArgumentException e) {
                                        System.out.println("Error: " + e.getMessage());
                                    }
                                    break;
                                case "4":
                                    //Withdraw
                                    System.out.print("Enter account ID: ");
                                    String withdrawalAccountId = scanner.nextLine();
                                    System.out.print("Enter withdrawal amount: ");
                                    String withdrawalAmount = scanner.nextLine();
                                    try {
                                        BigDecimal amount = new BigDecimal(withdrawalAmount);
                                        Transaction newWithdrawal = transactionService.withdraw(withdrawalAccountId, loggedInCustomer.getCustomerId(), amount);
                                        System.out.println("Withdrawal successful. New Balance: $" + newWithdrawal.getResultingBalance());
                                    }catch (IllegalArgumentException e){
                                        System.out.println("Error: " + e.getMessage());
                                    }
                                    break;
                                case "5":
                                    //Transfer
                                    System.out.print("Enter source account ID: ");
                                    String sourceId = scanner.nextLine();
                                    System.out.print("Enter destination account ID: ");
                                    String destinationId = scanner.nextLine();
                                    System.out.print("Enter transfer amount: ");
                                    String transferAmount = scanner.nextLine();
                                    try {
                                        BigDecimal amount = new BigDecimal(transferAmount);
                                        List<Transaction> transactions = transactionService.transfer(sourceId, destinationId,loggedInCustomer.getCustomerId(), amount);
                                        for(Transaction transaction: transactions){
                                            System.out.println("Transaction ID: " + transaction.getTransactionId() + ", Type: " + transaction.getType() + ", Resulting Balance: $" + transaction.getResultingBalance());
                                        }
                                    } catch (IllegalArgumentException e) {
                                        System.out.println("Error: " + e.getMessage());
                                    }
                                    break;
                                case "6":
                                    //Transaction History
                                    System.out.print("Enter account ID: ");
                                    String transactionAccountId = scanner.nextLine();
                                    try {
                                        List<Transaction> transactionHistory = transactionService.history(transactionAccountId, loggedInCustomer.getCustomerId());
                                        for (Transaction transaction : transactionHistory) {
                                            System.out.println("Transaction ID: " + transaction.getTransactionId() + ", Type: "+transaction.getType()+", Amount: $"+ transaction.getAmount());
                                        }
                                    }catch (IllegalArgumentException e){
                                        System.out.println("Error: "+ e.getMessage());
                                    }
                                    break;
                                case "7":
                                    //close account
                                    System.out.print("Enter account ID: ");
                                    String closingAccountId = scanner.nextLine();
                                    try {
                                        BankAccount closeAccount = accountService.closeAccount(closingAccountId,loggedInCustomer.getCustomerId());
                                        System.out.println("Account closed. Status: "+closeAccount.getAccountStatus());
                                    }catch (IllegalArgumentException e){
                                        System.out.println("Error: "+ e.getMessage());
                                    }
                                    break;
                                case "8":
                                    loggedIn = false;
                                    System.out.println("Logged out successfully");
                                    break;
                                default:
                                    System.out.println("Invalid option, try again.");
                            }

                        }
                    } else {
                        System.out.println("Invalid username or password.");
                    }
                    break;
                case "3":
                    // ends the while loop, no break since break will only leave the switch
                    // and stay in the while loop
                    return;
                default :
                    System.out.println("Invalid option, try again.");
            }
        }
    }
}