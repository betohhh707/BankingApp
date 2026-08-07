package com.bank.model;

import java.math.BigDecimal;

public class BankAccount {
    private String accountId;
    private String customerId;
    private AccountType accountType;
    private AccountStatus accountStatus;
    private BigDecimal balance;

    //Constructor for new account
    public BankAccount(String accountId,String customerId,AccountType accountType, AccountStatus accountStatus,BigDecimal balance) {
        this.accountId = accountId;
        this.customerId = customerId;
        this.accountType = accountType;
        this.accountStatus = accountStatus;
        this.balance = balance;
    }

    //Constructor for existing account
    public BankAccount(String customerId, AccountType accountType) {
        this(null, customerId, accountType, AccountStatus.OPEN, BigDecimal.ZERO);
    }

    public String getAccountId(){
        return accountId;
    }

    public String getCustomerId(){
        return  customerId;
    }

    public AccountType getAccountType(){
        return  accountType;
    }

    public void setAccountType(AccountType accountType){
        this.accountType = accountType;
    }

    public AccountStatus getAccountStatus(){
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public BigDecimal getBalance(){
        return balance;
    }

    public void setBalance(BigDecimal balance){
        this.balance = balance;
    }
}
