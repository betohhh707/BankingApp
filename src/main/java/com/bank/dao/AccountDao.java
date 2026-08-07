package com.bank.dao;

import com.bank.model.BankAccount;

import java.util.List;
import java.util.Optional;

public interface AccountDao {
    BankAccount openAccount(BankAccount account);
    Optional<BankAccount> getAccountById(String accountId);
    void updateAccount(BankAccount account);
    List<BankAccount> getAccountsByCustomerId(String customerId);
}
