package com.monetra.core.repository;

import com.monetra.core.model.Budget;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcBudgetRepository implements BudgetRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Budget> budgetRowMapper = (rs, rowNum) -> {
        Budget b = new Budget();
        b.setId(rs.getLong("id"));
        b.setUserId(rs.getLong("user_id"));
        long catId = rs.getLong("category_id");
        if (!rs.wasNull()) {
            b.setCategoryId(catId);
        }
        b.setName(rs.getString("name"));
        b.setAmount(rs.getBigDecimal("amount"));
        b.setMonth(rs.getInt("budget_month"));
        b.setYear(rs.getInt("budget_year"));
        Timestamp cat = rs.getTimestamp("created_at");
        if (cat != null) b.setCreatedAt(cat.toLocalDateTime());
        Timestamp uat = rs.getTimestamp("updated_at");
        if (uat != null) b.setUpdatedAt(uat.toLocalDateTime());

        try {
            b.setCategoryName(rs.getString("category_name"));
        } catch (Exception ignored) {}

        return b;
    };

    public JdbcBudgetRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private String getBaseSelectQuery() {
        return "SELECT b.*, c.name AS category_name FROM budgets b " +
               "LEFT JOIN categories c ON b.category_id = c.id ";
    }

    @Override
    public Optional<Budget> findById(Long id) {
        String sql = getBaseSelectQuery() + "WHERE b.id = ?";
        try {
            Budget budget = jdbcTemplate.queryForObject(sql, budgetRowMapper, id);
            return Optional.ofNullable(budget);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Budget> findByIdAndUserId(Long id, Long userId) {
        String sql = getBaseSelectQuery() + "WHERE b.id = ? AND b.user_id = ?";
        try {
            Budget budget = jdbcTemplate.queryForObject(sql, budgetRowMapper, id, userId);
            return Optional.ofNullable(budget);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Budget> findByUserIdAndPeriod(Long userId, int month, int year) {
        String sql = getBaseSelectQuery() + "WHERE b.user_id = ? AND b.budget_month = ? AND b.budget_year = ? ORDER BY b.id ASC";
        return jdbcTemplate.query(sql, budgetRowMapper, userId, month, year);
    }

    @Override
    public Optional<Budget> findByUserCategoryAndPeriod(Long userId, Long categoryId, int month, int year) {
        String sql;
        Object[] params;
        if (categoryId != null) {
            sql = getBaseSelectQuery() + "WHERE b.user_id = ? AND b.category_id = ? AND b.budget_month = ? AND b.budget_year = ?";
            params = new Object[]{userId, categoryId, month, year};
        } else {
            sql = getBaseSelectQuery() + "WHERE b.user_id = ? AND b.category_id IS NULL AND b.budget_month = ? AND b.budget_year = ?";
            params = new Object[]{userId, month, year};
        }

        try {
            Budget budget = jdbcTemplate.queryForObject(sql, budgetRowMapper, params);
            return Optional.ofNullable(budget);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Budget save(Budget budget) {
        String sql = "INSERT INTO budgets (user_id, category_id, name, amount, budget_month, budget_year, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        LocalDateTime now = LocalDateTime.now();
        budget.setCreatedAt(now);
        budget.setUpdatedAt(now);

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, budget.getUserId());
            if (budget.getCategoryId() != null) {
                ps.setLong(2, budget.getCategoryId());
            } else {
                ps.setNull(2, java.sql.Types.BIGINT);
            }
            ps.setString(3, budget.getName());
            ps.setBigDecimal(4, budget.getAmount());
            ps.setInt(5, budget.getMonth());
            ps.setInt(6, budget.getYear());
            ps.setTimestamp(7, Timestamp.valueOf(now));
            ps.setTimestamp(8, Timestamp.valueOf(now));
            return ps;
        }, keyHolder);

        Long generatedId = KeyHolderHelper.extractId(keyHolder);
        if (generatedId != null) {
            budget.setId(generatedId);
        }
        return budget;
    }

    @Override
    public void update(Budget budget) {
        String sql = "UPDATE budgets SET category_id = ?, name = ?, amount = ?, budget_month = ?, budget_year = ?, updated_at = ? " +
                     "WHERE id = ? AND user_id = ?";
        LocalDateTime now = LocalDateTime.now();
        budget.setUpdatedAt(now);
        jdbcTemplate.update(sql,
                budget.getCategoryId(),
                budget.getName(),
                budget.getAmount(),
                budget.getMonth(),
                budget.getYear(),
                Timestamp.valueOf(now),
                budget.getId(),
                budget.getUserId());
    }

    @Override
    public void delete(Long id, Long userId) {
        String sql = "DELETE FROM budgets WHERE id = ? AND user_id = ?";
        jdbcTemplate.update(sql, id, userId);
    }
}
