package com.finance.finance_dashboard.service;

import com.finance.finance_dashboard.model.Transaction;
import com.finance.finance_dashboard.model.TransactionType;
import com.finance.finance_dashboard.model.User;
import com.finance.finance_dashboard.repository.TransactionRepository;
import com.finance.finance_dashboard.dto.TransactionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserService userService;

    public Transaction createTransaction(TransactionRequest request, User user) {
        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setCategory(request.getCategory());

        if (request.getDate() != null) {
            transaction.setDate(request.getDate());
        } else {
            transaction.setDate(LocalDate.now());
        }

        transaction.setDescription(request.getDescription());
        transaction.setUser(user);

        return transactionRepository.save(transaction);
    }

    public List<Transaction> getUserTransactions(User user) {
        return transactionRepository.findByUser(user);
    }

    public List<Transaction> getUserTransactionsById(Long userId) {
        User user = userService.getUserById(userId);
        return transactionRepository.findByUser(user);
    }

    // ADD THIS - Get ALL transactions (for ADMIN and ANALYST)
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    public Transaction updateTransaction(Long id, TransactionRequest request, User user) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (request.getAmount() != null) {
            transaction.setAmount(request.getAmount());
        }
        if (request.getType() != null) {
            transaction.setType(request.getType());
        }
        if (request.getCategory() != null) {
            transaction.setCategory(request.getCategory());
        }
        if (request.getDate() != null) {
            transaction.setDate(request.getDate());
        }
        if (request.getDescription() != null) {
            transaction.setDescription(request.getDescription());
        }

        return transactionRepository.save(transaction);
    }

    public void deleteTransaction(Long id, User user) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You don't own this transaction");
        }

        transactionRepository.delete(transaction);
    }

    public void hardDeleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }

    public List<Transaction> filterTransactions(User user, TransactionType type, String category, LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions = transactionRepository.findByUser(user);

        return transactions.stream()
                .filter(t -> type == null || t.getType() == type)
                .filter(t -> category == null || (t.getCategory() != null && t.getCategory().equals(category)))
                .filter(t -> startDate == null || !t.getDate().isBefore(startDate))
                .filter(t -> endDate == null || !t.getDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    // ADD THIS - Filter ALL users' transactions (for ADMIN and ANALYST)
    public List<Transaction> filterAllTransactions(TransactionType type, String category, LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions = transactionRepository.findAll();

        return transactions.stream()
                .filter(t -> type == null || t.getType() == type)
                .filter(t -> category == null || (t.getCategory() != null && t.getCategory().equals(category)))
                .filter(t -> startDate == null || !t.getDate().isBefore(startDate))
                .filter(t -> endDate == null || !t.getDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    public List<Transaction> filterTransactionsByUser(Long userId, TransactionType type, String category, LocalDate startDate, LocalDate endDate) {
        User user = userService.getUserById(userId);
        return filterTransactions(user, type, category, startDate, endDate);
    }
}