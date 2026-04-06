package com.finance.finance_dashboard.controller;

import com.finance.finance_dashboard.dto.TransactionRequest;
import com.finance.finance_dashboard.dto.ApiResponse;
import com.finance.finance_dashboard.model.Transaction;
import com.finance.finance_dashboard.model.TransactionType;
import com.finance.finance_dashboard.model.User;
import com.finance.finance_dashboard.model.Role;
import com.finance.finance_dashboard.service.TransactionService;
import com.finance.finance_dashboard.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final AuthService authService;

    private User getCurrentUser(String authHeader) {
        if (authHeader == null) {
            throw new RuntimeException("Authorization header is required");
        }

        String token = authHeader;
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        User user = authService.getCurrentUser(token);
        if (user == null) {
            throw new RuntimeException("Please login first");
        }
        return user;
    }

    @PostMapping
    public ApiResponse createTransaction(
            @RequestBody TransactionRequest request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            User user = getCurrentUser(authHeader);
            Transaction transaction = transactionService.createTransaction(request, user);
            return ApiResponse.success("Transaction created", transaction);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping
    public ApiResponse getUserTransactions(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) Long userId) {
        try {
            User currentUser = getCurrentUser(authHeader);

            List<Transaction> transactions;
            if (currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.ANALYST) {
                if (userId != null) {
                    transactions = transactionService.getUserTransactionsById(userId);
                } else {
                    transactions = transactionService.getAllTransactions();
                }
            } else {
                transactions = transactionService.getUserTransactions(currentUser);
            }

            return ApiResponse.success("Transactions fetched", transactions);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse getTransactionById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        try {
            User currentUser = getCurrentUser(authHeader);
            Transaction transaction = transactionService.getTransactionById(id);

            if (transaction == null) {
                return ApiResponse.error("Transaction not found");
            }
            if (currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.ANALYST) {
                return ApiResponse.success("Transaction fetched", transaction);
            }
            if (!transaction.getUser().getId().equals(currentUser.getId())) {
                return ApiResponse.error("You don't have permission to view this transaction");
            }
            return ApiResponse.success("Transaction fetched", transaction);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse updateTransaction(
            @PathVariable Long id,
            @RequestBody TransactionRequest request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            User currentUser = getCurrentUser(authHeader);
            Transaction existingTransaction = transactionService.getTransactionById(id);

            if (existingTransaction == null) {
                return ApiResponse.error("Transaction not found");
            }
            if (currentUser.getRole() == Role.ADMIN) {
                Transaction transaction = transactionService.updateTransaction(id, request, existingTransaction.getUser());
                return ApiResponse.success("Transaction updated", transaction);
            }
            if (!existingTransaction.getUser().getId().equals(currentUser.getId())) {
                return ApiResponse.error("You can only update your own transactions");
            }

            Transaction transaction = transactionService.updateTransaction(id, request, currentUser);
            return ApiResponse.success("Transaction updated", transaction);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteTransaction(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        try {
            User currentUser = getCurrentUser(authHeader);
            Transaction existingTransaction = transactionService.getTransactionById(id);

            if (existingTransaction == null) {
                return ApiResponse.error("Transaction not found");
            }

            // ANALYST cannot delete any transaction
            if (currentUser.getRole() == Role.ANALYST) {
                return ApiResponse.error("Analysts cannot delete transactions");
            }

            // ADMIN can delete any transaction
            if (currentUser.getRole() == Role.ADMIN) {
                transactionService.hardDeleteTransaction(id);
                return ApiResponse.success("Transaction deleted", null);
            }

            // VIEWER can only delete their own
            if (!existingTransaction.getUser().getId().equals(currentUser.getId())) {
                return ApiResponse.error("You can only delete your own transactions");
            }

            transactionService.deleteTransaction(id, currentUser);
            return ApiResponse.success("Transaction deleted", null);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/filter")
    public ApiResponse filterTransactions(
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) Long userId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            User currentUser = getCurrentUser(authHeader);

            List<Transaction> transactions;
            if (currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.ANALYST) {
                if (userId != null) {
                    transactions = transactionService.filterTransactionsByUser(userId, type, category, startDate, endDate);
                } else {
                    transactions = transactionService.filterAllTransactions(type, category, startDate, endDate);
                }
            } else {
                transactions = transactionService.filterTransactions(currentUser, type, category, startDate, endDate);
            }

            return ApiResponse.success("Filtered transactions", transactions);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}