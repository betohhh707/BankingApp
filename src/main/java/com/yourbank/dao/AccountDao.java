package com.yourbank.dao;

import com.yourbank.model.AccountStatus;
import com.yourbank.model.BankAccount;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountDao {
    BankAccount openAccount(BankAccount account);
    Optional<BankAccount> getAccountById(String accountId);
    void updateAccount(BankAccount account);
    List<BankAccount> getAccountsByCustomerId(String customerId);
}
