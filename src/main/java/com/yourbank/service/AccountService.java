package com.yourbank.service;

import com.yourbank.dao.AccountDao;
import com.yourbank.dao.CustomerDao;
import com.yourbank.dao.postgres.PostgresAccountDao;
import com.yourbank.dao.postgres.PostgresCustomerDao;
import com.yourbank.model.AccountStatus;
import com.yourbank.model.AccountType;
import com.yourbank.model.BankAccount;
import com.yourbank.model.Customer;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class AccountService {
    private AccountDao accountDao = new PostgresAccountDao();
    private CustomerDao customerDao = new PostgresCustomerDao();

    //gets existing account given account and customer id. both ids have to connected to
    //each other otherwise it'll throw and exception or there even is account related to account id
    //returns the account after validation
    private BankAccount getOwnedAccount(String accountId, String customerId){
        //creates object of type optional because customer can have multiple accounts
        Optional<BankAccount> found = accountDao.getAccountById(accountId);

        //checks if there is an existing account
        if(found.isEmpty()){
            throw new IllegalArgumentException("Account not found");
        }

        //account object
        BankAccount account = found.get();
        //checks if the account matches the customer id
        if(!account.getCustomerId().equals(customerId)){
            throw new IllegalArgumentException(("Account does not belong to this customer"));
        }
        return account;
    }

    //opens account if customer id exists then returns an account object
    //returns the newly created account
    public BankAccount openAccount(String customerId, AccountType accountType){
        if(customerDao.getCustomerById(customerId).isEmpty()){
            throw new IllegalArgumentException("Customer not found");
        }
        //pass in the required parameters needed to build new account object
        BankAccount newAccount = new BankAccount(customerId, accountType);
        return accountDao.openAccount(newAccount);
    }

    //closing account just needs to update account by using getters and setters and use
    // enum to then return the bank account object
    public BankAccount closeAccount(String accountId, String customerId){
        //fetch
        BankAccount account = getOwnedAccount(accountId,customerId);
        //eligibility check: balance must be zero to close
        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalArgumentException("Account must have a zero balance to close");
        }
        //set
        account.setAccountStatus(AccountStatus.CLOSED);
        //sends it to the database
        accountDao.updateAccount(account);
        return account;
    }

    public List<BankAccount> viewAccounts(String customerId){
        return accountDao.getAccountsByCustomerId(customerId);
    }

    public BankAccount viewAccount(String accountId,String customerId){
        return getOwnedAccount(accountId,customerId);
    }
}
