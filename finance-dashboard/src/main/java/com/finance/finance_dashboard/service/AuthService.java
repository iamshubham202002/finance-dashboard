// service/AuthService.java
package com.finance.finance_dashboard.service;

import com.finance.finance_dashboard.model.User;
import com.finance.finance_dashboard.model.Role;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final Map<String, User> userSessions = new ConcurrentHashMap<>();

    public String login(String username, String password) {
        User user = userService.getUserByUsername(username);

        if (!user.isActive()) {
            throw new RuntimeException("Account is inactive");
        }

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password");
        }

        String token = java.util.UUID.randomUUID().toString();
        userSessions.put(token, user);

        return token;
    }

    public User getCurrentUser(String token) {
        return userSessions.get(token);
    }

    public void logout(String token) {
        userSessions.remove(token);
    }

    // FIXED: Correct permission logic
    public boolean hasPermission(User user, String action, User targetUser) {
        if (user == null) return false;

        Role role = user.getRole();

        switch (action) {
            case "CREATE_TRANSACTION":
                // All active users can create transactions
                return true;

            case "VIEW_TRANSACTION":
                // VIEWER can only see their own
                // ANALYST and ADMIN can see all
                if (role == Role.VIEWER) {
                    return targetUser != null && user.getId().equals(targetUser.getId());
                }
                return role == Role.ANALYST || role == Role.ADMIN;

            case "UPDATE_TRANSACTION":
                // Users can update their own transactions
                // ADMIN can update anyone's
                if (role == Role.ADMIN) return true;
                return targetUser != null && user.getId().equals(targetUser.getId());

            case "DELETE_TRANSACTION":
                // Users can delete their OWN transactions
                // ONLY ADMIN can delete ANY user's transaction
                if (role == Role.ADMIN) return true;
                return targetUser != null && user.getId().equals(targetUser.getId());

            case "VIEW_ALL_USERS":
                // Only ADMIN can see all users
                return role == Role.ADMIN;

            case "MANAGE_USERS":
                // Only ADMIN can manage users
                return role == Role.ADMIN;

            default:
                return false;
        }
    }

    // Simplified version without targetUser (for non-transaction actions)
    public boolean hasPermission(User user, String action) {
        return hasPermission(user, action, null);
    }
}