// service/DashboardService.java
package com.finance.finance_dashboard.service;

import com.finance.finance_dashboard.model.Transaction;
import com.finance.finance_dashboard.model.TransactionType;
import com.finance.finance_dashboard.model.User;
import com.finance.finance_dashboard.repository.TransactionRepository;
import com.finance.finance_dashboard.dto.DashboardSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TransactionRepository transactionRepository;

    public DashboardSummary getDashboardSummary(User user) {
        DashboardSummary summary = new DashboardSummary();

        // Calculate totals
        Double totalIncome = transactionRepository.sumByUserAndType(user, TransactionType.INCOME);
        Double totalExpense = transactionRepository.sumByUserAndType(user, TransactionType.EXPENSE);

        summary.setTotalIncome(totalIncome != null ? totalIncome : 0.0);
        summary.setTotalExpense(totalExpense != null ? totalExpense : 0.0);
        summary.setNetBalance(summary.getTotalIncome() - summary.getTotalExpense());

        // Category wise expense
        List<Transaction> expenses = transactionRepository.findByUserAndType(user, TransactionType.EXPENSE);
        Map<String, Double> categoryExpense = expenses.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)
                ));
        summary.setCategoryWiseExpense(categoryExpense);

        // Recent activity count (last 30 days)
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -30);
        Date thirtyDaysAgo = cal.getTime();

        long recentCount = transactionRepository.findByUser(user).stream()
                .filter(t -> t.getCreatedAt().toLocalDate().isAfter(java.time.LocalDate.now().minusDays(30)))
                .count();
        summary.setRecentTransactionsCount(recentCount);

        return summary;
    }
}