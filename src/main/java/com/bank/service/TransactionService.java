package com.bank.service;

import com.bank.config.DaoFactory;
import com.bank.dao.AccountDao;
import com.bank.dao.TransactionDao;
import com.bank.model.BankAccount;
import com.bank.model.Transaction;
import com.bank.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionService {
    private AccountDao accountDao;
    private TransactionDao transactionDao;
    private AccountService accountService;

    //Constructor for the app
    public TransactionService(){
        this.accountDao = DaoFactory.getAccountDao();
        this.transactionDao = DaoFactory.getTransactionDao();
        this.accountService = new AccountService();
    }
    //constructor for the mock
    public TransactionService(AccountDao accountDao,TransactionDao transactionDao, AccountService accountService){
        this.accountDao =accountDao;
        this.transactionDao=transactionDao;
        this.accountService=accountService;
    }

    //deposits for existing account compares deposit it amount so amount is not negative
    //update the account using setters and getters and records the transaction
    public Transaction deposit(String accountId, String customerId, BigDecimal amount){
        BankAccount account= accountService.viewAccount(accountId,customerId);
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        BigDecimal depositAmount = account.getBalance().add(amount);
        account.setBalance(depositAmount);
        accountDao.updateAccount(account);
        Transaction transaction = new Transaction(accountId,amount,LocalDateTime.now(),TransactionType.DEPOSIT,depositAmount);
        return transactionDao.recordTransaction(transaction);
    }

    //withdraws given amount. compares the amount withdrawal to 0 to prevent overdraft
    //update the account and records the transaction
    public Transaction withdraw(String accountId, String customerId, BigDecimal amount){
        BankAccount account = accountService.viewAccount(accountId,customerId);
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        if(account.getBalance().compareTo(amount)<0){
            throw new IllegalArgumentException(("Insufficient funds"));
        }

        BigDecimal withdrawalAmount = account.getBalance().subtract(amount);
        account.setBalance(withdrawalAmount);
        accountDao.updateAccount(account);
        Transaction transaction = new Transaction(accountId,amount,LocalDateTime.now(),TransactionType.WITHDRAWAL,withdrawalAmount);
        return transactionDao.recordTransaction(transaction);
    }
    //transfers are the most involved throughout the project
    //grabs two accounts and withdraws from one and deposits to the other
    //acts as a single step so the money is not lost in case of big or error: atomicity
    //not calling the deposit and withdrawal methods for the purpose of atomicity
    //instead use the logic of both withdrawal and deposit
    public List<Transaction> transfer(String sourceAccountId,String destinationAccountId, String customerId, BigDecimal amount){
        //the source account must use validation: owner checked
        BankAccount source = accountService.viewAccount(sourceAccountId,customerId);

        //bank account just need to exist, no valid ownership: permissive
        Optional<BankAccount> destinationOpt = accountDao.getAccountById(destinationAccountId);
        //if the account exists
        if (destinationOpt.isEmpty()) {
            throw new IllegalArgumentException("Destination account not found");
        }
        //the account that we are transferring to
        BankAccount destinationAccount = destinationOpt.get();
        //can't go below 0
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        //if the source has enough to transfer
        if(source.getBalance().compareTo(amount)<0){
            throw new IllegalArgumentException(("Insufficient funds"));
        }
        BigDecimal withdrawalAmount = source.getBalance().subtract(amount);
        source.setBalance(withdrawalAmount);
        accountDao.updateAccount(source);


        BigDecimal depositAmount = destinationAccount.getBalance().add(amount);
        destinationAccount.setBalance(depositAmount);
        accountDao.updateAccount(destinationAccount);
        //record the transactions and getting the ready to get sent out together in arraylist
        Transaction sourceTransaction = new Transaction(sourceAccountId,amount,LocalDateTime.now(),TransactionType.WITHDRAWAL,withdrawalAmount);
        Transaction destinationTransaction = new Transaction(destinationAccountId,amount,LocalDateTime.now(),TransactionType.DEPOSIT,depositAmount);

        Transaction savedSourceTransaction = transactionDao.recordTransaction(sourceTransaction);
        Transaction savedDestinationTransaction = transactionDao.recordTransaction(destinationTransaction);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(savedSourceTransaction);
        transactions.add(savedDestinationTransaction);
        return transactions;
    }
    //calls the viewAccount for strict ownership validation and sends it
    public List<Transaction> history(String accountId, String customerId){
        accountService.viewAccount(accountId,customerId);
        return transactionDao.getTransactionsByAccountId(accountId);
    }
}