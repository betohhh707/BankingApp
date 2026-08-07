package com.bank.dao;

import com.bank.model.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionDao {
    List<Transaction> getTransactionsByAccountId(String accountId);
    Optional<Transaction> getTransactionByTransactionId(String transactionId);
    Transaction recordTransaction(Transaction transaction);
}
