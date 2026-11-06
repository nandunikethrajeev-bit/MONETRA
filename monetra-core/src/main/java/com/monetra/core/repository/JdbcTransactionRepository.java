package com.monetra.core.repository;

import com.monetra.core.dto.CategorySpendingDto;
import com.monetra.core.dto.MonthlyTrendDto;
import com.monetra.core.dto.TransactionFilterDto;
import com.monetra.core.model.Transaction;
import com.monetra.core.model.TransactionType;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcTransactionRepository implements TransactionRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Transaction> transactionRowMapper = (rs, rowNum) -> {
        Transaction t = new Transaction();
        t.setId(rs.getLong("id"));
        t.setUserId(rs.getLong("user_id"));
        t.setAccountId(rs.getLong("account_id"));
        t.setCategoryId(rs.getLong("category_id"));
        t.setAmount(rs.getBigDecimal("amount"));
        String typeStr = rs.getString("type");
        if (typeStr != null) t.setType(TransactionType.valueOf(typeStr));
        Date txDate = rs.getDate("transaction_date");
        if (txDate != null) t.setTransactionDate(txDate.toLocalDate());
        t.setDescription(rs.getString("description"));
        t.setNotes(rs.getString("notes"));
        t.setRecurringInstance(rs.getBoolean("is_recurring_instance"));
        long recId = rs.getLong("recurring_transaction_id");
        if (!rs.wasNull()) t.setRecurringTransactionId(recId);
        Timestamp cat = rs.getTimestamp("created_at");
        if (cat != null) t.setCreatedAt(cat.toLocalDateTime());
        Timestamp uat = rs.getTimestamp("updated_at");
        if (uat != null) t.setUpdatedAt(uat.toLocalDateTime());

        // Joined columns if available
        try {
            t.setAccountName(rs.getString("account_name"));
        } catch (Exception ignored) {}
        try {
            t.setCategoryName(rs.getString("category_name"));
            t.setCategoryColor(rs.getString("category_color"));
            t.setCategoryIcon(rs.getString("category_icon"));
        } catch (Exception ignored) {}

        return t;
    };

    public JdbcTransactionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private String getBaseSelectQuery() {
        return "SELECT t.*, a.name AS account_name, c.name AS category_name, c.color AS category_color, c.icon AS category_icon " +
               "FROM transactions t " +
               "LEFT JOIN accounts a ON t.account_id = a.id " +
               "LEFT JOIN categories c ON t.category_id = c.id ";
    }

    @Override
    public Optional<Transaction> findById(Long id) {
        String sql = getBaseSelectQuery() + "WHERE t.id = ?";
        try {
            Transaction t = jdbcTemplate.queryForObject(sql, transactionRowMapper, id);
            return Optional.ofNullable(t);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Transaction> findByIdAndUserId(Long id, Long userId) {
        String sql = getBaseSelectQuery() + "WHERE t.id = ? AND t.user_id = ?";
        try {
            Transaction t = jdbcTemplate.queryForObject(sql, transactionRowMapper, id, userId);
            return Optional.ofNullable(t);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Transaction> findByUserId(Long userId) {
        String sql = getBaseSelectQuery() + "WHERE t.user_id = ? ORDER BY t.transaction_date DESC, t.id DESC";
        return jdbcTemplate.query(sql, transactionRowMapper, userId);
    }

    @Override
    public List<Transaction> findFiltered(Long userId, TransactionFilterDto filter) {
        StringBuilder sql = new StringBuilder(getBaseSelectQuery()).append("WHERE t.user_id = ? ");
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (filter != null) {
            if (filter.getAccountId() != null) {
                sql.append("AND t.account_id = ? ");
                params.add(filter.getAccountId());
            }
            if (filter.getCategoryId() != null) {
                sql.append("AND t.category_id = ? ");
                params.add(filter.getCategoryId());
            }
            if (filter.getType() != null) {
                sql.append("AND t.type = ? ");
                params.add(filter.getType().name());
            }
            if (filter.getStartDate() != null) {
                sql.append("AND t.transaction_date >= ? ");
                params.add(Date.valueOf(filter.getStartDate()));
            }
            if (filter.getEndDate() != null) {
                sql.append("AND t.transaction_date <= ? ");
                params.add(Date.valueOf(filter.getEndDate()));
            }
            if (filter.getSearchQuery() != null && !filter.getSearchQuery().trim().isEmpty()) {
                sql.append("AND (LOWER(t.description) LIKE ? OR LOWER(t.notes) LIKE ?) ");
                String pattern = "%" + filter.getSearchQuery().trim().toLowerCase() + "%";
                params.add(pattern);
                params.add(pattern);
            }
        }

        sql.append("ORDER BY t.transaction_date DESC, t.id DESC");
        return jdbcTemplate.query(sql.toString(), transactionRowMapper, params.toArray());
    }

    @Override
    public List<Transaction> findRecent(Long userId, int limit) {
        String sql = getBaseSelectQuery() + "WHERE t.user_id = ? ORDER BY t.transaction_date DESC, t.id DESC LIMIT ?";
        return jdbcTemplate.query(sql, transactionRowMapper, userId, limit);
    }

    @Override
    public Transaction save(Transaction transaction) {
        String sql = "INSERT INTO transactions (user_id, account_id, category_id, amount, type, transaction_date, " +
                     "description, notes, is_recurring_instance, recurring_transaction_id, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        LocalDateTime now = LocalDateTime.now();
        transaction.setCreatedAt(now);
        transaction.setUpdatedAt(now);

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, transaction.getUserId());
            ps.setLong(2, transaction.getAccountId());
            ps.setLong(3, transaction.getCategoryId());
            ps.setBigDecimal(4, transaction.getAmount());
            ps.setString(5, transaction.getType().name());
            ps.setDate(6, Date.valueOf(transaction.getTransactionDate()));
            ps.setString(7, transaction.getDescription());
            ps.setString(8, transaction.getNotes());
            ps.setBoolean(9, transaction.isRecurringInstance());
            if (transaction.getRecurringTransactionId() != null) {
                ps.setLong(10, transaction.getRecurringTransactionId());
            } else {
                ps.setNull(10, java.sql.Types.BIGINT);
            }
            ps.setTimestamp(11, Timestamp.valueOf(now));
            ps.setTimestamp(12, Timestamp.valueOf(now));
            return ps;
        }, keyHolder);

        Long generatedId = KeyHolderHelper.extractId(keyHolder);
        if (generatedId != null) {
            transaction.setId(generatedId);
        }
        return transaction;
    }

    @Override
    public void update(Transaction transaction) {
        String sql = "UPDATE transactions SET account_id = ?, category_id = ?, amount = ?, type = ?, " +
                     "transaction_date = ?, description = ?, notes = ?, updated_at = ? " +
                     "WHERE id = ? AND user_id = ?";
        LocalDateTime now = LocalDateTime.now();
        transaction.setUpdatedAt(now);
        jdbcTemplate.update(sql,
                transaction.getAccountId(),
                transaction.getCategoryId(),
                transaction.getAmount(),
                transaction.getType().name(),
                Date.valueOf(transaction.getTransactionDate()),
                transaction.getDescription(),
                transaction.getNotes(),
                Timestamp.valueOf(now),
                transaction.getId(),
                transaction.getUserId());
    }

    @Override
    public void delete(Long id, Long userId) {
        String sql = "DELETE FROM transactions WHERE id = ? AND user_id = ?";
        jdbcTemplate.update(sql, id, userId);
    }

    @Override
    public BigDecimal calculateTotalIncome(Long userId) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE user_id = ? AND type = 'INCOME'";
        BigDecimal sum = jdbcTemplate.queryForObject(sql, BigDecimal.class, userId);
        return sum != null ? sum : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal calculateTotalExpense(Long userId) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE user_id = ? AND type = 'EXPENSE'";
        BigDecimal sum = jdbcTemplate.queryForObject(sql, BigDecimal.class, userId);
        return sum != null ? sum : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal calculateMonthlyIncome(Long userId, int month, int year) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1).minusDays(1);
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM transactions " +
                     "WHERE user_id = ? AND type = 'INCOME' AND transaction_date >= ? AND transaction_date <= ?";
        BigDecimal sum = jdbcTemplate.queryForObject(sql, BigDecimal.class, userId, Date.valueOf(start), Date.valueOf(end));
        return sum != null ? sum : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal calculateMonthlyExpense(Long userId, int month, int year) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1).minusDays(1);
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM transactions " +
                     "WHERE user_id = ? AND type = 'EXPENSE' AND transaction_date >= ? AND transaction_date <= ?";
        BigDecimal sum = jdbcTemplate.queryForObject(sql, BigDecimal.class, userId, Date.valueOf(start), Date.valueOf(end));
        return sum != null ? sum : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal calculateCategorySpending(Long userId, Long categoryId, int month, int year) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1).minusDays(1);
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM transactions " +
                     "WHERE user_id = ? AND category_id = ? AND type = 'EXPENSE' AND transaction_date >= ? AND transaction_date <= ?";
        BigDecimal sum = jdbcTemplate.queryForObject(sql, BigDecimal.class, userId, categoryId, Date.valueOf(start), Date.valueOf(end));
        return sum != null ? sum : BigDecimal.ZERO;
    }

    @Override
    public List<CategorySpendingDto> getCategorySpendingBreakdown(Long userId, int month, int year) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1).minusDays(1);

        String totalSql = "SELECT COALESCE(SUM(amount), 0) FROM transactions " +
                          "WHERE user_id = ? AND type = 'EXPENSE' AND transaction_date >= ? AND transaction_date <= ?";
        BigDecimal totalExpense = jdbcTemplate.queryForObject(totalSql, BigDecimal.class, userId, Date.valueOf(start), Date.valueOf(end));
        if (totalExpense == null) totalExpense = BigDecimal.ZERO;

        String sql = "SELECT c.id AS category_id, c.name AS category_name, c.color AS category_color, c.icon AS category_icon, " +
                     "COALESCE(SUM(t.amount), 0) AS total_amount " +
                     "FROM transactions t " +
                     "JOIN categories c ON t.category_id = c.id " +
                     "WHERE t.user_id = ? AND t.type = 'EXPENSE' AND t.transaction_date >= ? AND t.transaction_date <= ? " +
                     "GROUP BY c.id, c.name, c.color, c.icon " +
                     "ORDER BY total_amount DESC";

        final BigDecimal finalTotal = totalExpense;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            CategorySpendingDto dto = new CategorySpendingDto();
            dto.setCategoryId(rs.getLong("category_id"));
            dto.setCategoryName(rs.getString("category_name"));
            dto.setCategoryColor(rs.getString("category_color"));
            dto.setCategoryIcon(rs.getString("category_icon"));
            BigDecimal amt = rs.getBigDecimal("total_amount");
            dto.setAmount(amt != null ? amt : BigDecimal.ZERO);
            if (finalTotal.compareTo(BigDecimal.ZERO) > 0 && amt != null) {
                dto.setPercentage(amt.multiply(BigDecimal.valueOf(100)).divide(finalTotal, 2, RoundingMode.HALF_UP));
            } else {
                dto.setPercentage(BigDecimal.ZERO);
            }
            return dto;
        }, userId, Date.valueOf(start), Date.valueOf(end));
    }

    @Override
    public List<MonthlyTrendDto> getMonthlyTrends(Long userId, int monthsCount) {
        List<MonthlyTrendDto> results = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");

        for (int i = monthsCount - 1; i >= 0; i--) {
            LocalDate target = today.minusMonths(i);
            int m = target.getMonthValue();
            int y = target.getYear();
            String label = target.format(formatter);

            BigDecimal inc = calculateMonthlyIncome(userId, m, y);
            BigDecimal exp = calculateMonthlyExpense(userId, m, y);
            BigDecimal savings = inc.subtract(exp);

            results.add(new MonthlyTrendDto(y, m, label, inc, exp, savings));
        }

        return results;
    }
}
