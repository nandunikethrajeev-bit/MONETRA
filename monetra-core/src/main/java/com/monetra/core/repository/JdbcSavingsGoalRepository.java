package com.monetra.core.repository;

import com.monetra.core.model.GoalStatus;
import com.monetra.core.model.SavingsGoal;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcSavingsGoalRepository implements SavingsGoalRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<SavingsGoal> savingsGoalRowMapper = (rs, rowNum) -> {
        SavingsGoal g = new SavingsGoal();
        g.setId(rs.getLong("id"));
        g.setUserId(rs.getLong("user_id"));
        long accId = rs.getLong("account_id");
        if (!rs.wasNull()) {
            g.setAccountId(accId);
        }
        g.setName(rs.getString("name"));
        g.setTargetAmount(rs.getBigDecimal("target_amount"));
        g.setCurrentAmount(rs.getBigDecimal("current_amount"));
        Date targetDate = rs.getDate("target_date");
        if (targetDate != null) g.setTargetDate(targetDate.toLocalDate());
        String statusStr = rs.getString("status");
        if (statusStr != null) g.setStatus(GoalStatus.valueOf(statusStr));
        g.setNotes(rs.getString("notes"));
        Timestamp cat = rs.getTimestamp("created_at");
        if (cat != null) g.setCreatedAt(cat.toLocalDateTime());
        Timestamp uat = rs.getTimestamp("updated_at");
        if (uat != null) g.setUpdatedAt(uat.toLocalDateTime());

        try {
            g.setAccountName(rs.getString("account_name"));
        } catch (Exception ignored) {}

        return g;
    };

    public JdbcSavingsGoalRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private String getBaseSelectQuery() {
        return "SELECT g.*, a.name AS account_name FROM savings_goals g " +
               "LEFT JOIN accounts a ON g.account_id = a.id ";
    }

    @Override
    public Optional<SavingsGoal> findById(Long id) {
        String sql = getBaseSelectQuery() + "WHERE g.id = ?";
        try {
            SavingsGoal g = jdbcTemplate.queryForObject(sql, savingsGoalRowMapper, id);
            return Optional.ofNullable(g);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<SavingsGoal> findByIdAndUserId(Long id, Long userId) {
        String sql = getBaseSelectQuery() + "WHERE g.id = ? AND g.user_id = ?";
        try {
            SavingsGoal g = jdbcTemplate.queryForObject(sql, savingsGoalRowMapper, id, userId);
            return Optional.ofNullable(g);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<SavingsGoal> findByUserId(Long userId) {
        String sql = getBaseSelectQuery() + "WHERE g.user_id = ? ORDER BY g.target_date ASC, g.id DESC";
        return jdbcTemplate.query(sql, savingsGoalRowMapper, userId);
    }

    @Override
    public SavingsGoal save(SavingsGoal goal) {
        String sql = "INSERT INTO savings_goals (user_id, account_id, name, target_amount, current_amount, " +
                     "target_date, status, notes, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        LocalDateTime now = LocalDateTime.now();
        goal.setCreatedAt(now);
        goal.setUpdatedAt(now);

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, goal.getUserId());
            if (goal.getAccountId() != null) {
                ps.setLong(2, goal.getAccountId());
            } else {
                ps.setNull(2, java.sql.Types.BIGINT);
            }
            ps.setString(3, goal.getName());
            ps.setBigDecimal(4, goal.getTargetAmount());
            ps.setBigDecimal(5, goal.getCurrentAmount() != null ? goal.getCurrentAmount() : BigDecimal.ZERO);
            ps.setDate(6, Date.valueOf(goal.getTargetDate()));
            ps.setString(7, goal.getStatus().name());
            ps.setString(8, goal.getNotes());
            ps.setTimestamp(9, Timestamp.valueOf(now));
            ps.setTimestamp(10, Timestamp.valueOf(now));
            return ps;
        }, keyHolder);

        Long generatedId = KeyHolderHelper.extractId(keyHolder);
        if (generatedId != null) {
            goal.setId(generatedId);
        }
        return goal;
    }

    @Override
    public void update(SavingsGoal goal) {
        String sql = "UPDATE savings_goals SET account_id = ?, name = ?, target_amount = ?, target_date = ?, " +
                     "status = ?, notes = ?, updated_at = ? WHERE id = ? AND user_id = ?";
        LocalDateTime now = LocalDateTime.now();
        goal.setUpdatedAt(now);
        jdbcTemplate.update(sql,
                goal.getAccountId(),
                goal.getName(),
                goal.getTargetAmount(),
                Date.valueOf(goal.getTargetDate()),
                goal.getStatus().name(),
                goal.getNotes(),
                Timestamp.valueOf(now),
                goal.getId(),
                goal.getUserId());
    }

    @Override
    public void updateAmount(Long id, BigDecimal newCurrentAmount, GoalStatus newStatus) {
        String sql = "UPDATE savings_goals SET current_amount = ?, status = ?, updated_at = ? WHERE id = ?";
        jdbcTemplate.update(sql, newCurrentAmount, newStatus.name(), Timestamp.valueOf(LocalDateTime.now()), id);
    }

    @Override
    public void delete(Long id, Long userId) {
        String sql = "DELETE FROM savings_goals WHERE id = ? AND user_id = ?";
        jdbcTemplate.update(sql, id, userId);
    }
}
