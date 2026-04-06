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

## API Endpoints

### Authentication

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register/user` | Register as normal user |
| POST | `/api/auth/register/analyst` | Register as analyst |
| POST | `/api/auth/register/admin` | Register as admin (requires secret key) |
| POST | `/api/auth/login` | Login and get token |
| GET | `/api/auth/me` | Get current user info |

### Transactions

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/transactions` | Create transaction |
| GET | `/api/transactions` | Get all transactions |
| GET | `/api/transactions/{id}` | Get transaction by ID |
| PUT | `/api/transactions/{id}` | Update transaction |
| DELETE | `/api/transactions/{id}` | Delete transaction |
| GET | `/api/transactions/filter` | Filter transactions |

### Admin (Admin only)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/admin/users` | Get all users |
| PUT | `/api/admin/users/{id}/role` | Update user role |
| PUT | `/api/admin/users/{id}/status` | Activate/deactivate user |
| DELETE | `/api/admin/users/{id}` | Delete user |

### Dashboard

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/dashboard/summary` | Get dashboard summary |
| GET | `/api/dashboard/trends` | Get monthly trends |

## Setup Instructions

### Prerequisites

- Java 17
- Maven
- PostgreSQL (or use H2 for testing)

### Database Setup (PostgreSQL)

```sql
CREATE DATABASE finance_db;
