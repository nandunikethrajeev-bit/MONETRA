package com.monetra.core.repository;

import com.monetra.core.model.Account;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountRepository {
    Optional<Account> findById(Long id);
    Optional<Account> findByIdAndUserId(Long id, Long userId);
    List<Account> findByUserId(Long userId);
    Account save(Account account);
    void update(Account account);
    void updateBalance(Long accountId, BigDecimal newBalance);
    void delete(Long id, Long userId);
}
