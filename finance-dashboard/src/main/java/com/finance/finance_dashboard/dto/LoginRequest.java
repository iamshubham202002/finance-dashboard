// dto/LoginRequest.java
package com.finance.finance_dashboard.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}