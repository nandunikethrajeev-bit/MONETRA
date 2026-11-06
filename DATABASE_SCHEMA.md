# MONETRA — Database Design & Schema Documentation

This document describes the relational database design, table definitions, foreign keys, indexes, and migration strategy for **MONETRA — Personal Finance Management System**.

---

## 1. Design Principles

1. **Third Normal Form (3NF)**: Redundancy is minimized while preserving clean relationships.
2. **Explicit Financial Types**: All financial values are stored as `DECIMAL(14, 2)` to eliminate floating-point rounding errors.
3. **Multi-Tenancy & User Isolation**: Every entity table contains a `user_id` foreign key with `ON DELETE CASCADE`.
4. **Audit Columns**: `created_at` and `updated_at` timestamps are present on all records.
5. **Referential Integrity**: Cascading deletes ensure that deleting an account or user cleanly purges dependent transactions and budgets without leaving orphaned rows.
6. **SQL Keyword Safety**: Columns such as `budget_month` and `budget_year` avoid ANSI/H2 SQL reserved function collisions.

---

## 2. Table Specifications

### 2.1 `users`
Stores user profile credentials and currency preference.

| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY AUTO_INCREMENT` | Unique user identifier |
| `username` | `VARCHAR(50)` | `NOT NULL UNIQUE` | Login username |
| `email` | `VARCHAR(100)` | `NOT NULL UNIQUE` | Unique email address |
| `password_hash` | `VARCHAR(255)`| `NOT NULL` | BCrypt work factor 12 hash |
| `full_name` | `VARCHAR(100)` | `NOT NULL` | User's full display name |
| `currency` | `VARCHAR(3)` | `NOT NULL DEFAULT 'USD'` | 3-letter ISO currency code |
| `created_at` | `TIMESTAMP` | `NOT NULL DEFAULT CURRENT_TIMESTAMP` | Account creation timestamp |
| `updated_at` | `TIMESTAMP` | `NOT NULL DEFAULT CURRENT_TIMESTAMP` | Last profile update timestamp |

**Indexes**:
- `uk_users_username` on `username`
- `uk_users_email` on `email`

---

### 2.2 `accounts`
Stores financial accounts (Checking, Savings, Credit Cards, Cash, Investments).

| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY AUTO_INCREMENT` | Unique account identifier |
| `user_id` | `BIGINT` | `NOT NULL, FK -> users(id)` | Owning user |
| `name` | `VARCHAR(100)` | `NOT NULL` | Account name (e.g., "Primary Checking") |
| `type` | `VARCHAR(30)` | `NOT NULL` | Enum: `CHECKING`, `SAVINGS`, `CREDIT_CARD`, `CASH`, `INVESTMENT` |
| `balance` | `DECIMAL(14,2)`| `NOT NULL DEFAULT 0.00` | Current account balance |
| `currency` | `VARCHAR(3)` | `NOT NULL DEFAULT 'USD'` | Account currency |
| `description` | `VARCHAR(255)`| `NULL` | Optional account note |
| `created_at` | `TIMESTAMP` | `NOT NULL DEFAULT CURRENT_TIMESTAMP` | Timestamp |
| `updated_at` | `TIMESTAMP` | `NOT NULL DEFAULT CURRENT_TIMESTAMP` | Timestamp |

**Indexes**:
- `idx_accounts_user` on `user_id`
- `fk_accounts_user` foreign key `(user_id) REFERENCES users(id) ON DELETE CASCADE`

---

### 2.3 `categories`
Stores expense and income categories. System defaults have `user_id = NULL` and `is_default = TRUE`.

| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY AUTO_INCREMENT` | Unique category identifier |
| `user_id` | `BIGINT` | `NULL, FK -> users(id)` | Owning user, or `NULL` if default |
| `name` | `VARCHAR(50)` | `NOT NULL` | Category name |
| `type` | `VARCHAR(20)` | `NOT NULL` | Enum: `INCOME`, `EXPENSE` |
| `icon` | `VARCHAR(50)` | `NULL` | Visual icon identifier |
| `color` | `VARCHAR(20)` | `NULL` | Hex color code (e.g. `#E5A93C`) |
| `is_default` | `BOOLEAN` | `NOT NULL DEFAULT FALSE` | Flag for pre-seeded categories |
| `created_at` | `TIMESTAMP` | `NOT NULL DEFAULT CURRENT_TIMESTAMP` | Timestamp |

**Indexes**:
- `idx_categories_user` on `user_id`
- `idx_categories_type` on `type`
- `fk_categories_user` foreign key `(user_id) REFERENCES users(id) ON DELETE CASCADE`

---

### 2.4 `transactions`
Stores all income and expense events.

| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY AUTO_INCREMENT` | Unique transaction ID |
| `user_id` | `BIGINT` | `NOT NULL, FK -> users(id)` | Owning user |
| `account_id` | `BIGINT` | `NOT NULL, FK -> accounts(id)` | Source/target account |
| `category_id` | `BIGINT` | `NOT NULL, FK -> categories(id)`| Classification category |
| `amount` | `DECIMAL(14,2)`| `NOT NULL` | Transaction amount |
| `type` | `VARCHAR(20)` | `NOT NULL` | Enum: `INCOME`, `EXPENSE` |
| `description` | `VARCHAR(255)`| `NOT NULL` | Memo/description |
| `transaction_date` | `DATE` | `NOT NULL` | Transaction date |
| `created_at` | `TIMESTAMP` | `NOT NULL DEFAULT CURRENT_TIMESTAMP` | Record creation timestamp |
| `updated_at` | `TIMESTAMP` | `NOT NULL DEFAULT CURRENT_TIMESTAMP` | Last updated timestamp |

**Indexes**:
- `idx_transactions_user` on `user_id`
- `idx_transactions_date` on `transaction_date`
- `idx_transactions_account` on `account_id`
- `idx_transactions_category` on `category_id`
- Foreign keys with `ON DELETE CASCADE` to `users`, `accounts`, and `categories`.

---

### 2.5 `budgets`
Stores category spending limits for specific calendar months.

| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY AUTO_INCREMENT` | Unique budget ID |
| `user_id` | `BIGINT` | `NOT NULL, FK -> users(id)` | Owning user |
| `category_id` | `BIGINT` | `NOT NULL, FK -> categories(id)`| Targeted expense category |
| `amount` | `DECIMAL(14,2)`| `NOT NULL` | Maximum budget limit |
| `budget_month` | `INT` | `NOT NULL` | Month (1-12) |
| `budget_year` | `INT` | `NOT NULL` | Year (e.g. 2026) |
| `created_at` | `TIMESTAMP` | `NOT NULL DEFAULT CURRENT_TIMESTAMP` | Timestamp |
| `updated_at` | `TIMESTAMP` | `NOT NULL DEFAULT CURRENT_TIMESTAMP` | Timestamp |

**Indexes**:
- `uk_budget_period` unique constraint on `(user_id, category_id, budget_month, budget_year)`
- `idx_budgets_user` on `user_id`

---

### 2.6 `savings_goals`
Tracks financial savings targets, contributions, and deadlines.

| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY AUTO_INCREMENT` | Unique goal ID |
| `user_id` | `BIGINT` | `NOT NULL, FK -> users(id)` | Owning user |
| `name` | `VARCHAR(100)` | `NOT NULL` | Goal title (e.g. "Emergency Fund") |
| `target_amount`| `DECIMAL(14,2)`| `NOT NULL` | Target savings amount |
| `current_amount`| `DECIMAL(14,2)`| `NOT NULL DEFAULT 0.00` | Accumulated contributions |
| `target_date` | `DATE` | `NULL` | Planned completion date |
| `status` | `VARCHAR(20)` | `NOT NULL DEFAULT 'IN_PROGRESS'` | Enum: `IN_PROGRESS`, `COMPLETED`, `CANCELLED` |
| `created_at` | `TIMESTAMP` | `NOT NULL DEFAULT CURRENT_TIMESTAMP` | Timestamp |
| `updated_at` | `TIMESTAMP` | `NOT NULL DEFAULT CURRENT_TIMESTAMP` | Timestamp |

**Indexes**:
- `idx_savings_goals_user` on `user_id`

---

### 2.7 `recurring_transactions`
Manages scheduled recurring expenses and incomes.

| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY AUTO_INCREMENT` | Unique recurring transaction ID |
| `user_id` | `BIGINT` | `NOT NULL, FK -> users(id)` | Owning user |
| `account_id` | `BIGINT` | `NOT NULL, FK -> accounts(id)` | Linked account |
| `category_id` | `BIGINT` | `NOT NULL, FK -> categories(id)`| Linked category |
| `amount` | `DECIMAL(14,2)`| `NOT NULL` | Recurring amount |
| `type` | `VARCHAR(20)` | `NOT NULL` | Enum: `INCOME`, `EXPENSE` |
| `description` | `VARCHAR(255)`| `NOT NULL` | Recurring payment memo |
| `frequency` | `VARCHAR(20)` | `NOT NULL` | Enum: `WEEKLY`, `MONTHLY`, `YEARLY` |
| `start_date` | `DATE` | `NOT NULL` | Start date |
| `next_due_date`| `DATE` | `NOT NULL` | Next execution date |
| `end_date` | `DATE` | `NULL` | Optional expiration date |
| `is_active` | `BOOLEAN` | `NOT NULL DEFAULT TRUE` | Status toggle |
| `created_at` | `TIMESTAMP` | `NOT NULL DEFAULT CURRENT_TIMESTAMP` | Timestamp |

**Indexes**:
- `idx_recurring_user` on `user_id`
- `idx_recurring_due` on `next_due_date`

---

## 3. Flyway Migration Strategy

Database migrations are located in `monetra-core/src/main/resources/db/migration`:
- **`V1__init_schema.sql`**: Normalized DDL creating all 7 tables with foreign keys and performance indexes.
- **`V2__seed_default_categories.sql`**: Seeds 11 system categories (Salary, Freelancing, Food & Dining, Transportation, Shopping, Education, Entertainment, Healthcare, Housing, Utilities, Other).

Migrations are applied automatically upon application startup across both MySQL and H2.

