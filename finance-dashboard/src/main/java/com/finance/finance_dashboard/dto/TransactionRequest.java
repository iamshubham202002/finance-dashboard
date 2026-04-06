// dto/TransactionRequest.java
package com.finance.finance_dashboard.dto;

import lombok.Data;
import com.finance.finance_dashboard.model.TransactionType;
import java.time.LocalDate;

@Data
public class TransactionRequest {
    private Double amount;
    private TransactionType type;
    private String category;
    private LocalDate date;
    private String description;
}