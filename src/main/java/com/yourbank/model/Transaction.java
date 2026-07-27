package com.yourbank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private String accountId;
    private String transactionId;
    private BigDecimal amount;
    private LocalDateTime date;
    private TransactionType type;
    private BigDecimal resultingBalance;

    // constructor 1: when the DAO loads an existing transaction
    public Transaction(String transactionId, String accountId, BigDecimal amount,
                       LocalDateTime date, TransactionType type, BigDecimal resultingBalance) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.amount = amount;
        this.date = date;
        this.type = type;
        this.resultingBalance = resultingBalance;
    }

    // constructor 2: used when we make a new transaction, before a transaction id exists
    public Transaction(String accountId, BigDecimal amount, LocalDateTime date,
                       TransactionType type, BigDecimal resultingBalance) {
        this(null, accountId, amount, date, type, resultingBalance);
    }

    public String getAccountId(){
        return accountId;
    }

    public String getTransactionId(){
        return transactionId;
    }

    public BigDecimal getAmount(){
        return amount;
    }

    public LocalDateTime getDate(){
        return date;
    }

    public TransactionType getType(){
        return type;
    }

    public BigDecimal getResultingBalance(){
        return resultingBalance;
    }
}