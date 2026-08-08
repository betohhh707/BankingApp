package com.bank.testing;

import com.bank.dao.AccountDao;
import com.bank.dao.TransactionDao;
import com.bank.model.AccountStatus;
import com.bank.model.AccountType;
import com.bank.model.BankAccount;
import com.bank.model.Transaction;
import com.bank.service.AccountService;
import com.bank.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private AccountDao accountDao;

    @Mock
    private TransactionDao transactionDao;

    @Mock
    private AccountService accountService;

    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionService(accountDao, transactionDao, accountService);
    }

    //tests if the balance is lower than the desired withdrawal. Throws
    //IllegalArgumentException if found to be that case.
    @Test
    void withdraw_OverdraftCheck_ThrowsException(){
        //setting everything up before running it
        BankAccount testAccount = new BankAccount("1","1", AccountType.CHECKING,AccountStatus.OPEN, new BigDecimal("50.00"));
        when(accountService.viewAccount("1","1")).thenReturn(testAccount);

        //does the validation and confirmation
        assertThrows(IllegalArgumentException.class, () ->{
           transactionService.withdraw("1","1", new BigDecimal("100.00"));
        });
    }
    //tests if the deposit is a positive number. Throws exception if found not be positive
    @Test
    void deposit_PositiveAmountCheck_ThrowsException(){
        BankAccount testAccount = new BankAccount("1","1", AccountType.CHECKING,AccountStatus.OPEN,new BigDecimal("100.00"));
        when(accountService.viewAccount("1","1")).thenReturn(testAccount);

        assertThrows(IllegalArgumentException.class, () ->{
           transactionService.deposit("1","1",new BigDecimal("-50.00"));
        });
    }

    //tests if a transfer is being done to a non-existing account
    @Test
    void transfer_DestinationDoesNotExist_ThrowsException(){
        BankAccount testAccount = new BankAccount("1","1",AccountType.CHECKING,AccountStatus.OPEN,new BigDecimal("100.00"));
        //the source account
        when(accountService.viewAccount("1","1")).thenReturn(testAccount);
        //the non-existing account
        when(accountDao.getAccountById("2")).thenReturn(Optional.empty());
        //checks if the destination account exists
        assertThrows(IllegalArgumentException.class, ()->{
            transactionService.transfer("1","2","1",new BigDecimal("100"));
        });
    }

    @Test
    void transfer_InsufficientFunds_ThrowsException(){
        //source object
        BankAccount testAccount1 = new BankAccount("1","1",AccountType.CHECKING, AccountStatus.OPEN, new BigDecimal("1.00"));
        //destination object
        BankAccount testAccount2 = new BankAccount("2","2",AccountType.CHECKING,AccountStatus.OPEN,new BigDecimal("5.00"));

        when(accountService.viewAccount("1","1")).thenReturn(testAccount1);
        when(accountDao.getAccountById("2")).thenReturn(Optional.of(testAccount2));
        assertThrows(IllegalArgumentException.class,()->{
            transactionService.transfer("1","2","1", new BigDecimal("400.00"));
        });
    }
}