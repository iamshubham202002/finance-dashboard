# Finance Dashboard Backend

A REST API backend for finance dashboard with role-based access control (RBAC).

## Features

- User authentication with JWT token
- Role-based access (VIEWER, ANALYST, ADMIN)
- Financial transaction management (CRUD)
- Dashboard analytics and filters
- Admin user management

## Tech Stack

- Java 17
- Spring Boot 3.1.5
- PostgreSQL / H2 Database
- Maven

## Roles & Permissions

| Action | VIEWER | ANALYST | ADMIN |
|--------|--------|---------|-------|
| View dashboard | ✅ | ✅ | ✅ |
| Create transaction | ✅ | ✅ | ✅ |
| View own transactions | ✅ | ✅ | ✅ |
| View all transactions | ❌ | ✅ | ✅ |
| Update own transaction | ✅ | ✅ | ✅ |
| Delete own transaction | ✅ | ❌ | ✅ |
| Delete any transaction | ❌ | ❌ | ✅ |
| Manage users | ❌ | ❌ | ✅ |

