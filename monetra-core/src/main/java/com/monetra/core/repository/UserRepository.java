package com.monetra.core.repository;

import com.monetra.core.model.User;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    User save(User user);
    void update(User user);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}

