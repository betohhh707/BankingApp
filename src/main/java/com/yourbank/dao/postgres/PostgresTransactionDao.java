package com.yourbank.dao.postgres;

import com.yourbank.config.DatabaseConnection;
import com.yourbank.dao.TransactionDao;
import com.yourbank.model.*;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostgresTransactionDao implements TransactionDao {
    @Override
    public List<Transaction> getTransactionsByAccountId(String accountId) {
        String sql = "SELECT * FROM transactions WHERE accountid = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(accountId));
            ResultSet rs = stmt.executeQuery();
            List<Transaction> transactions = new ArrayList<>();
            while(rs.next()) {
                String transactionId = String.valueOf(rs.getInt("transactionid"));
                BigDecimal amount = rs.getBigDecimal("amount");
                BigDecimal resultingBalance = rs.getBigDecimal("resultingbalance");
                LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
                TransactionType type = TransactionType.valueOf(rs.getString("type"));
                Transaction transaction = new Transaction(transactionId,accountId,amount, date, type, resultingBalance);
                transactions.add(transaction);
            }
            return transactions;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get transaction by id", e);
        }
    }

    @Override
    public Optional<Transaction> getTransactionByTransactionId(String transactionId) {
        String sql = "SELECT * FROM transactions WHERE transactionid = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(transactionId));
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                String accountId =String.valueOf(rs.getInt("accountid")) ;
                BigDecimal amount = rs.getBigDecimal("amount");
                BigDecimal resultingBalance = rs.getBigDecimal("resultingbalance");
                LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
                TransactionType type = TransactionType.valueOf(rs.getString("type"));
                Transaction transaction = new Transaction(transactionId,accountId,amount,date,type,resultingBalance);
                return Optional.of(transaction);
            }else{
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get account by transaction id", e);
        }
    }

    @Override
    public Transaction recordTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions(accountid, amount, date,type,resultingbalance)VALUES(?, ?, ?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

            stmt.setInt(1, Integer.parseInt(transaction.getAccountId()));
            stmt.setBigDecimal(2,transaction.getAmount());
            stmt.setObject(3,transaction.getDate());
            stmt.setString(4, transaction.getType().name());
            stmt.setBigDecimal(5,transaction.getResultingBalance());
            // execute the update
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            // get the generated key back
            String newId =null;
            if (keys.next()) {
                newId = String.valueOf(keys.getInt(1));
            }
            // return a new Transaction object, now with the ID filled in
            return new Transaction(newId, transaction.getAccountId(), transaction.getAmount(),
                    transaction.getDate(), transaction.getType(),transaction.getResultingBalance());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to record transaction", e);
        }
    }
}
