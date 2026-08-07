package com.bank.dao.postgres;

import com.bank.config.DatabaseConnection;
import com.bank.dao.AccountDao;
import com.bank.model.BankAccount;
import com.bank.model.AccountType;
import com.bank.model.AccountStatus;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostgresAccountDao implements AccountDao {

    @Override
    public BankAccount openAccount(BankAccount account) {
        String sql = "INSERT INTO accounts(customerid, accounttype, accountstatus, balance)VALUES(?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, Integer.parseInt(account.getCustomerId()));
            stmt.setString(2,account.getAccountType().name());
            stmt.setString(3,account.getAccountStatus().name());
            stmt.setBigDecimal(4, account.getBalance());
            // execute the update
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            // get the generated key back
            String newId =null;
            if (keys.next()) {
                newId = String.valueOf(keys.getInt(1));
            }
            // return a new BankAccount object, now with the ID filled in
            return new BankAccount(newId, account.getCustomerId(), account.getAccountType(),
                    account.getAccountStatus(), account.getBalance());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to open account", e);
        }
    }

    @Override
    public Optional<BankAccount> getAccountById(String accountId) {
        String sql = "SELECT * FROM accounts WHERE accountid = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(accountId));
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                String customerId =String.valueOf(rs.getInt("customerid")) ;
                AccountType type = AccountType.valueOf(rs.getString("accounttype"));
                AccountStatus status = AccountStatus.valueOf(rs.getString("accountstatus"));
                BigDecimal balance =  rs.getBigDecimal("balance");
                BankAccount account = new BankAccount(accountId, customerId,type,status,balance);
                return Optional.of(account);
            }else{
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get account by id", e);
        }
    }

    @Override
    public void updateAccount(BankAccount account) {
        String sql = "UPDATE accounts SET accounttype=?, accountstatus=?, balance=? WHERE accountid=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, account.getAccountType().name());
            stmt.setString(2, account.getAccountStatus().name());
            stmt.setBigDecimal(3, account.getBalance());
            stmt.setInt(4, Integer.parseInt(account.getAccountId()));
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update account", e);
        }
    }

    @Override
    public List<BankAccount> getAccountsByCustomerId(String customerId) {
        String sql = "SELECT * FROM accounts WHERE customerid=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(customerId));
            ResultSet rs = stmt.executeQuery();
            List<BankAccount> accounts = new ArrayList<>();
            while (rs.next()) {
                String accountId = String.valueOf(rs.getInt("accountid"));
                AccountType type = AccountType.valueOf(rs.getString("accounttype"));
                AccountStatus status = AccountStatus.valueOf(rs.getString("accountstatus"));
                BigDecimal balance = rs.getBigDecimal("balance");
                BankAccount account = new BankAccount(accountId, customerId, type, status, balance);
                accounts.add(account);
            }
            return accounts;
        }catch (SQLException e) {
                throw new RuntimeException("Failed to get account by id", e);
        }
    }
}