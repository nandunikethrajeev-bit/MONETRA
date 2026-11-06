package com.monetra.core.repository;

import com.monetra.core.model.Category;
import com.monetra.core.model.TransactionType;
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
public class JdbcCategoryRepository implements CategoryRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Category> categoryRowMapper = (rs, rowNum) -> {
        Category c = new Category();
        c.setId(rs.getLong("id"));
        long uid = rs.getLong("user_id");
        if (!rs.wasNull()) {
            c.setUserId(uid);
        }
        c.setName(rs.getString("name"));
        String typeStr = rs.getString("type");
        if (typeStr != null) c.setType(TransactionType.valueOf(typeStr));
        c.setIcon(rs.getString("icon"));
        c.setColor(rs.getString("color"));
        c.setDefault(rs.getBoolean("is_default"));
        Timestamp cat = rs.getTimestamp("created_at");
        if (cat != null) c.setCreatedAt(cat.toLocalDateTime());
        return c;
    };

    public JdbcCategoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Category> findById(Long id) {
        String sql = "SELECT * FROM categories WHERE id = ?";
        try {
            Category cat = jdbcTemplate.queryForObject(sql, categoryRowMapper, id);
            return Optional.ofNullable(cat);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Category> findByIdAndUserId(Long id, Long userId) {
        String sql = "SELECT * FROM categories WHERE id = ? AND (user_id IS NULL OR user_id = ?)";
        try {
            Category cat = jdbcTemplate.queryForObject(sql, categoryRowMapper, id, userId);
            return Optional.ofNullable(cat);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Category> findByUserId(Long userId) {
        String sql = "SELECT * FROM categories WHERE user_id IS NULL OR user_id = ? ORDER BY is_default DESC, name ASC";
        return jdbcTemplate.query(sql, categoryRowMapper, userId);
    }

    @Override
    public List<Category> findDefaultCategories() {
        String sql = "SELECT * FROM categories WHERE is_default = TRUE ORDER BY name ASC";
        return jdbcTemplate.query(sql, categoryRowMapper);
    }

    @Override
    public Category save(Category category) {
        String sql = "INSERT INTO categories (user_id, name, type, icon, color, is_default, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        LocalDateTime now = LocalDateTime.now();
        category.setCreatedAt(now);

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            if (category.getUserId() != null) {
                ps.setLong(1, category.getUserId());
            } else {
                ps.setNull(1, java.sql.Types.BIGINT);
            }
            ps.setString(2, category.getName());
            ps.setString(3, category.getType().name());
            ps.setString(4, category.getIcon() != null ? category.getIcon() : "tag");
            ps.setString(5, category.getColor() != null ? category.getColor() : "#E5A93C");
            ps.setBoolean(6, category.isDefault());
            ps.setTimestamp(7, Timestamp.valueOf(now));
            return ps;
        }, keyHolder);

        Long generatedId = KeyHolderHelper.extractId(keyHolder);
        if (generatedId != null) {
            category.setId(generatedId);
        }
        return category;
    }

    @Override
    public void update(Category category) {
        // Can only update user custom categories
        String sql = "UPDATE categories SET name = ?, type = ?, icon = ?, color = ? WHERE id = ? AND user_id = ?";
        jdbcTemplate.update(sql,
                category.getName(),
                category.getType().name(),
                category.getIcon(),
                category.getColor(),
                category.getId(),
                category.getUserId());
    }

    @Override
    public void delete(Long id, Long userId) {
        // Can only delete custom categories belonging to user (not default categories)
        String sql = "DELETE FROM categories WHERE id = ? AND user_id = ? AND is_default = FALSE";
        jdbcTemplate.update(sql, id, userId);
    }

    @Override
    public boolean isCategoryInUse(Long categoryId) {
        String sql = "SELECT COUNT(*) FROM transactions WHERE category_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, categoryId);
        return count != null && count > 0;
    }
}
