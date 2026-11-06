package com.monetra.core.repository;

import com.monetra.core.model.Account;
import com.monetra.core.model.AccountType;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcAccountRepository implements AccountRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Account> accountRowMapper = (rs, rowNum) -> {
        Account a = new Account();
        a.setId(rs.getLong("id"));
        a.setUserId(rs.getLong("user_id"));
        a.setName(rs.getString("name"));
        String typeStr = rs.getString("type");
        if (typeStr != null) a.setType(AccountType.valueOf(typeStr));
        a.setBalance(rs.getBigDecimal("balance"));
        a.setCurrency(rs.getString("currency"));
        a.setDescription(rs.getString("description"));
        Timestamp cat = rs.getTimestamp("created_at");
        if (cat != null) a.setCreatedAt(cat.toLocalDateTime());
        Timestamp uat = rs.getTimestamp("updated_at");
        if (uat != null) a.setUpdatedAt(uat.toLocalDateTime());
        return a;
    };

    public JdbcAccountRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Account> findById(Long id) {
        String sql = "SELECT * FROM accounts WHERE id = ?";
        try {
            Account account = jdbcTemplate.queryForObject(sql, accountRowMapper, id);
            return Optional.ofNullable(account);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Account> findByIdAndUserId(Long id, Long userId) {
        String sql = "SELECT * FROM accounts WHERE id = ? AND user_id = ?";
        try {
            Account account = jdbcTemplate.queryForObject(sql, accountRowMapper, id, userId);
            return Optional.ofNullable(account);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Account> findByUserId(Long userId) {
        String sql = "SELECT * FROM accounts WHERE user_id = ? ORDER BY id ASC";
        return jdbcTemplate.query(sql, accountRowMapper, userId);
    }

    @Override
    public Account save(Account account) {
        String sql = "INSERT INTO accounts (user_id, name, type, balance, currency, description, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        LocalDateTime now = LocalDateTime.now();
        account.setCreatedAt(now);
        account.setUpdatedAt(now);

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, account.getUserId());
            ps.setString(2, account.getName());
            ps.setString(3, account.getType().name());
            ps.setBigDecimal(4, account.getBalance() != null ? account.getBalance() : BigDecimal.ZERO);
            ps.setString(5, account.getCurrency() != null ? account.getCurrency() : "USD");
            ps.setString(6, account.getDescription());
            ps.setTimestamp(7, Timestamp.valueOf(now));
            ps.setTimestamp(8, Timestamp.valueOf(now));
            return ps;
        }, keyHolder);

        Long generatedId = KeyHolderHelper.extractId(keyHolder);
        if (generatedId != null) {
            account.setId(generatedId);
        }
        return account;
    }

    @Override
    public void update(Account account) {
        String sql = "UPDATE accounts SET name = ?, type = ?, description = ?, currency = ?, updated_at = ? " +
                     "WHERE id = ? AND user_id = ?";
        LocalDateTime now = LocalDateTime.now();
        account.setUpdatedAt(now);
        jdbcTemplate.update(sql,
                account.getName(),
                account.getType().name(),
                account.getDescription(),
                account.getCurrency(),
                Timestamp.valueOf(now),
                account.getId(),
                account.getUserId());
    }

    @Override
    public void updateBalance(Long accountId, BigDecimal newBalance) {
        String sql = "UPDATE accounts SET balance = ?, updated_at = ? WHERE id = ?";
        jdbcTemplate.update(sql, newBalance, Timestamp.valueOf(LocalDateTime.now()), accountId);
    }

    @Override
    public void delete(Long id, Long userId) {
        String sql = "DELETE FROM accounts WHERE id = ? AND user_id = ?";
        jdbcTemplate.update(sql, id, userId);
    }
}
