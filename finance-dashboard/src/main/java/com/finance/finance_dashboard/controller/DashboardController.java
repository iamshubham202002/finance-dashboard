// controller/DashboardController.java
package com.finance.finance_dashboard.controller;

import com.finance.finance_dashboard.dto.ApiResponse;
import com.finance.finance_dashboard.dto.DashboardSummary;
import com.finance.finance_dashboard.model.User;
import com.finance.finance_dashboard.service.DashboardService;
import com.finance.finance_dashboard.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final AuthService authService;

    @GetMapping("/summary")
    public ApiResponse getDashboardSummary(@RequestHeader("Authorization") String token) {
        try {
            User user = authService.getCurrentUser(token);
            if (user == null) {
                return ApiResponse.error("Please login first");
            }

            DashboardSummary summary = dashboardService.getDashboardSummary(user);
            return ApiResponse.success("Dashboard summary", summary);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}