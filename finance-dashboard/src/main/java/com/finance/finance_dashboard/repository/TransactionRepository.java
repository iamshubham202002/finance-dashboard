package com.finance.finance_dashboard.repository;

import com.finance.finance_dashboard.model.Transaction;
import com.finance.finance_dashboard.model.TransactionType;
import com.finance.finance_dashboard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Get all transactions for a user
    List<Transaction> findByUser(User user);

    // Get transactions by user and date range
    List<Transaction> findByUserAndDateBetween(User user, LocalDate start, LocalDate end);

    // Get transactions by user and type
    List<Transaction> findByUserAndType(User user, TransactionType type);

    // Get transactions by user and category
    List<Transaction> findByUserAndCategory(User user, String category);

    // Sum by user and type (for dashboard)
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.user = :user AND t.type = :type")
    Double sumByUserAndType(@Param("user") User user, @Param("type") TransactionType type);

    // Category breakdown (for dashboard)
    @Query("SELECT t.category, SUM(t.amount), COUNT(t) FROM Transaction t " +
            "WHERE t.user = :user AND t.date >= :startDate " +
            "GROUP BY t.category ORDER BY SUM(t.amount) DESC")
    List<Object[]> getCategoryBreakdown(@Param("user") User user, @Param("startDate") LocalDate startDate);
}