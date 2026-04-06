// controller/AuthController.java
package com.finance.finance_dashboard.controller;

import com.finance.finance_dashboard.dto.LoginRequest;
import com.finance.finance_dashboard.dto.ApiResponse;
import com.finance.finance_dashboard.model.User;
import com.finance.finance_dashboard.service.AuthService;
import com.finance.finance_dashboard.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/login")
    public ApiResponse login(@RequestBody LoginRequest request) {
        try {
            // FIXED: Now returns token String
            String token = authService.login(request.getUsername(), request.getPassword());

            // Get user for response
            User user = userService.getUserByUsername(request.getUsername());

            // Create response with token and user info
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("token", token);
            responseData.put("username", user.getUsername());
            responseData.put("role", user.getRole().name());
            responseData.put("email", user.getEmail());

            return ApiResponse.success("Login successful", responseData);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ApiResponse register(@RequestBody LoginRequest request) {
        try {
            User user = userService.createUser(
                    request.getUsername(),
                    request.getPassword(),
                    request.getUsername() + "@example.com",
                    com.finance.finance_dashboard.model.Role.VIEWER
            );

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("username", user.getUsername());
            responseData.put("role", user.getRole().name());

            return ApiResponse.success("User registered successfully", responseData);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ApiResponse logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                authService.logout(token);
            }
            return ApiResponse.success("Logout successful", null);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}