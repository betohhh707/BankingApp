package com.bank.testing;

import com.bank.dao.AccountDao;
import com.bank.dao.CustomerDao;
import com.bank.model.AccountStatus;
import com.bank.model.AccountType;
import com.bank.model.BankAccount;
import com.bank.service.AccountService;
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
public class AccountServiceTest {

    @Mock
    private AccountDao accountDao;

    @Mock
    private CustomerDao customerDao;

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountService(accountDao, customerDao);
    }

    //By naming this method with underscores readability is much higher
    //than reading the code and what the code does: self-explanatory method name
    //runs againist fake 50 bucks
    //testing for throws
    @Test
    void closeAccount_withNonZeroBalance_throwsException(){
        //setting the mock up
        BankAccount testAccount = new BankAccount("1","1",AccountType.CHECKING,AccountStatus.OPEN, new BigDecimal("50.00"));
        //when the code runs it doesn't send it to database instead sends it back here for testing
        when(accountDao.getAccountById("1")).thenReturn(Optional.of(testAccount));
        //JUnit asks to run this code, test only passes if this type of exception gets thrown.
        //lambda ()->{}, block of code passed as an argument
        assertThrows(IllegalArgumentException.class, () -> {
            accountService.closeAccount("1","1");
        });
    }
    //testing for success in calling an operation
    //using verify
    @Test
    void closeAccount_withZeroBalance_succeedsAndUpdatesStatus(){
        //building it
        BankAccount testAccount = new BankAccount("1","1",AccountType.CHECKING, AccountStatus.OPEN,BigDecimal.ZERO);
        //receive it instead of database
        when(accountDao.getAccountById("1")).thenReturn(Optional.of(testAccount));
        //execute it
        BankAccount result = accountService.closeAccount("1","1");
        //making sure it matches to closed
        assertEquals(AccountStatus.CLOSED,result.getAccountStatus());
        //checking if operation happened
        verify(accountDao).updateAccount(testAccount);
    }

    @Test
    void openAccount_reg
}