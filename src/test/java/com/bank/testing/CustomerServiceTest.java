    package com.bank.testing;

    import com.bank.dao.AccountDao;
    import com.bank.dao.CustomerDao;
    import com.bank.model.AccountStatus;
    import com.bank.model.AccountType;
    import com.bank.model.BankAccount;
    import com.bank.model.Customer;
    import com.bank.service.AccountService;
    import com.bank.service.CustomerService;
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;
    import org.junit.jupiter.api.extension.ExtendWith;
    import org.mindrot.jbcrypt.BCrypt;
    import org.mockito.Mock;
    import org.mockito.junit.jupiter.MockitoExtension;

    import java.math.BigDecimal;
    import java.util.Optional;

    import static org.junit.jupiter.api.Assertions.*;
    import static org.mockito.Mockito.*;
    @ExtendWith(MockitoExtension.class)
    public class CustomerServiceTest {

        @Mock
        private CustomerDao customerDao;

        private CustomerService customerService;

        @BeforeEach
        void setUp() {
            customerService = new CustomerService(customerDao);
        }
        //tests if fields are filled in, if there's a username, and email
        @Test
        void successfulRegistrationTest(){
            //setting up the mock
            String firstName = "Lone";
            String lastName = "Wonderer";
            String username ="LoneWonderer";
            String hashedPassword = "normanBates@1";
            String email = "butterflyfan@gmail.com";
            Customer customer = new Customer(firstName,lastName, username, hashedPassword, email);
            when(customerDao.getCustomerByUsername(username)).thenReturn(Optional.empty());
            when(customerDao.getCustomerByEmail(email)).thenReturn(Optional.empty());
            when(customerDao.registerCustomer(any(Customer.class))).thenReturn(customer);
            //call the method were testing and setting it to an object
            Customer result = customerService.registerCustomer(firstName, lastName, username,hashedPassword,email);
            //validation
            assertNotNull(result);
            assertEquals(username,result.getUsername());
            assertEquals(email, result.getEmail());
            //confirmation
            verify(customerDao, times(1)).registerCustomer(any(Customer.class));
        }

        //checks for duplicate email.
        @Test
        void registerCustomer_ThrowsException_WhenEmailIsTaken(){
            //setting up the mock
            String firstName = "Lone";
            String lastName = "Wonderer";
            String username ="LoneWonderer";
            String hashedPassword = "normanBates@1";
            String email = "butterflyfan@gmail.com";
            Customer customer = new Customer("client","customer", "clientuser", "password@1", email);
            //username checks out and email is found
            when(customerDao.getCustomerByUsername(username)).thenReturn(Optional.empty());
            when(customerDao.getCustomerByEmail(email)).thenReturn(Optional.of(customer));
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                customerService.registerCustomer(firstName, lastName, username, hashedPassword, email);
            });

            //making sure CustomerService throws
            assertEquals("Email already taken",exception.getMessage());
            //verifying if registerCustomer was never called to Dao
            verify(customerDao, never()).registerCustomer(any(Customer.class));
        }
        //checks for login
        @Test
        void login_success(){
            //setting up the mock
            String username = "quiGonGin";
            String password = "superman12!";
            String hashedPassword = BCrypt.hashpw(password,BCrypt.gensalt());

            Customer customer = new Customer("Lone","Wonderer",username,hashedPassword, "yarn@gmail.com");
            when(customerDao.getCustomerByUsername(username)).thenReturn(Optional.of(customer));
            Optional<Customer> result = customerService.login(username,password);
            assertTrue(result.isPresent());
            assertEquals(username, result.get().getUsername());
        }

    }
