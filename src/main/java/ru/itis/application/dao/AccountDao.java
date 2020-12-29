package ru.itis.application.dao;

import ru.itis.application.models.Account;

import java.util.Optional;

public interface AccountDao extends CrudDao<Account> {
    Optional<Account> findByEmail(String email);
}
