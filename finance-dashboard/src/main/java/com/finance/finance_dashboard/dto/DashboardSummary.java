// dto/DashboardSummary.java
package com.finance.finance_dashboard.dto;

import lombok.Data;
import java.util.Map;

@Data
public class DashboardSummary {
    private Double totalIncome;
    private Double totalExpense;
    private Double netBalance;
    private Map<String, Double> categoryWiseExpense;
    private Long recentTransactionsCount;
}