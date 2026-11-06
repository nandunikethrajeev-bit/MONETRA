package com.monetra.core.repository;

import com.monetra.core.model.RecurrenceFrequency;
import com.monetra.core.model.RecurringTransaction;
import com.monetra.core.model.TransactionType;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcRecurringTransactionRepository implements RecurringTransactionRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<RecurringTransaction> recurringRowMapper = (rs, rowNum) -> {
        RecurringTransaction r = new RecurringTransaction();
        r.setId(rs.getLong("id"));
        r.setUserId(rs.getLong("user_id"));
        r.setAccountId(rs.getLong("account_id"));
        r.setCategoryId(rs.getLong("category_id"));
        r.setAmount(rs.getBigDecimal("amount"));
        String typeStr = rs.getString("type");
        if (typeStr != null) r.setType(TransactionType.valueOf(typeStr));
        String freqStr = rs.getString("frequency");
        if (freqStr != null) r.setFrequency(RecurrenceFrequency.valueOf(freqStr));
        Date sDate = rs.getDate("start_date");
        if (sDate != null) r.setStartDate(sDate.toLocalDate());
        Date eDate = rs.getDate("end_date");
        if (eDate != null) r.setEndDate(eDate.toLocalDate());
        Date dueDate = rs.getDate("next_due_date");
        if (dueDate != null) r.setNextDueDate(dueDate.toLocalDate());
        Date procDate = rs.getDate("last_processed_date");
        if (procDate != null) r.setLastProcessedDate(procDate.toLocalDate());
        r.setDescription(rs.getString("description"));
        r.setActive(rs.getBoolean("is_active"));
        Timestamp cat = rs.getTimestamp("created_at");
        if (cat != null) r.setCreatedAt(cat.toLocalDateTime());
        Timestamp uat = rs.getTimestamp("updated_at");
        if (uat != null) r.setUpdatedAt(uat.toLocalDateTime());

        try {
            r.setAccountName(rs.getString("account_name"));
        } catch (Exception ignored) {}
        try {
            r.setCategoryName(rs.getString("category_name"));
        } catch (Exception ignored) {}

        return r;
    };

    public JdbcRecurringTransactionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private String getBaseSelectQuery() {
        return "SELECT r.*, a.name AS account_name, c.name AS category_name FROM recurring_transactions r " +
               "LEFT JOIN accounts a ON r.account_id = a.id " +
               "LEFT JOIN categories c ON r.category_id = c.id ";
    }

    @Override
    public Optional<RecurringTransaction> findById(Long id) {
        String sql = getBaseSelectQuery() + "WHERE r.id = ?";
        try {
            RecurringTransaction r = jdbcTemplate.queryForObject(sql, recurringRowMapper, id);
            return Optional.ofNullable(r);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<RecurringTransaction> findByIdAndUserId(Long id, Long userId) {
        String sql = getBaseSelectQuery() + "WHERE r.id = ? AND r.user_id = ?";
        try {
            RecurringTransaction r = jdbcTemplate.queryForObject(sql, recurringRowMapper, id, userId);
            return Optional.ofNullable(r);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<RecurringTransaction> findByUserId(Long userId) {
        String sql = getBaseSelectQuery() + "WHERE r.user_id = ? ORDER BY r.next_due_date ASC, r.id DESC";
        return jdbcTemplate.query(sql, recurringRowMapper, userId);
    }

    @Override
    public List<RecurringTransaction> findDueRecurringTransactions(LocalDate date) {
        String sql = getBaseSelectQuery() + "WHERE r.is_active = TRUE AND r.next_due_date <= ? " +
                     "AND (r.end_date IS NULL OR r.next_due_date <= r.end_date) ORDER BY r.next_due_date ASC";
        return jdbcTemplate.query(sql, recurringRowMapper, Date.valueOf(date));
    }

    @Override
    public RecurringTransaction save(RecurringTransaction recurring) {
        String sql = "INSERT INTO recurring_transactions (user_id, account_id, category_id, amount, type, " +
                     "frequency, start_date, end_date, next_due_date, last_processed_date, description, " +
                     "is_active, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        LocalDateTime now = LocalDateTime.now();
        recurring.setCreatedAt(now);
        recurring.setUpdatedAt(now);

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, recurring.getUserId());
            ps.setLong(2, recurring.getAccountId());
            ps.setLong(3, recurring.getCategoryId());
            ps.setBigDecimal(4, recurring.getAmount());
            ps.setString(5, recurring.getType().name());
            ps.setString(6, recurring.getFrequency().name());
            ps.setDate(7, Date.valueOf(recurring.getStartDate()));
            if (recurring.getEndDate() != null) {
                ps.setDate(8, Date.valueOf(recurring.getEndDate()));
            } else {
                ps.setNull(8, java.sql.Types.DATE);
            }
            ps.setDate(9, Date.valueOf(recurring.getNextDueDate()));
            if (recurring.getLastProcessedDate() != null) {
                ps.setDate(10, Date.valueOf(recurring.getLastProcessedDate()));
            } else {
                ps.setNull(10, java.sql.Types.DATE);
            }
            ps.setString(11, recurring.getDescription());
            ps.setBoolean(12, recurring.isActive());
            ps.setTimestamp(13, Timestamp.valueOf(now));
            ps.setTimestamp(14, Timestamp.valueOf(now));
            return ps;
        }, keyHolder);

        Long generatedId = KeyHolderHelper.extractId(keyHolder);
        if (generatedId != null) {
            recurring.setId(generatedId);
        }
        return recurring;
    }

    @Override
    public void update(RecurringTransaction recurring) {
        String sql = "UPDATE recurring_transactions SET account_id = ?, category_id = ?, amount = ?, type = ?, " +
                     "frequency = ?, start_date = ?, end_date = ?, next_due_date = ?, description = ?, is_active = ?, " +
                     "updated_at = ? WHERE id = ? AND user_id = ?";
        LocalDateTime now = LocalDateTime.now();
        recurring.setUpdatedAt(now);
        jdbcTemplate.update(sql,
                recurring.getAccountId(),
                recurring.getCategoryId(),
                recurring.getAmount(),
                recurring.getType().name(),
                recurring.getFrequency().name(),
                Date.valueOf(recurring.getStartDate()),
                recurring.getEndDate() != null ? Date.valueOf(recurring.getEndDate()) : null,
                Date.valueOf(recurring.getNextDueDate()),
                recurring.getDescription(),
                recurring.isActive(),
                Timestamp.valueOf(now),
                recurring.getId(),
                recurring.getUserId());
    }

    @Override
    public void updateNextDueDate(Long id, LocalDate nextDueDate, LocalDate lastProcessedDate) {
        String sql = "UPDATE recurring_transactions SET next_due_date = ?, last_processed_date = ?, updated_at = ? WHERE id = ?";
        jdbcTemplate.update(sql, Date.valueOf(nextDueDate),
                lastProcessedDate != null ? Date.valueOf(lastProcessedDate) : null,
                Timestamp.valueOf(LocalDateTime.now()), id);
    }

    @Override
    public void delete(Long id, Long userId) {
        String sql = "DELETE FROM recurring_transactions WHERE id = ? AND user_id = ?";
        jdbcTemplate.update(sql, id, userId);
    }
}
