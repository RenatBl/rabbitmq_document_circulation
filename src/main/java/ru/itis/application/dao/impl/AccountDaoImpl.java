package ru.itis.application.dao.impl;

import ru.itis.application.dao.AccountDao;
import ru.itis.application.dao.RowMapper;
import ru.itis.application.models.Account;
import ru.itis.application.models.enums.Status;
import ru.itis.application.utils.ConnectionUtil;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class AccountDaoImpl implements AccountDao {

    private Connection connection;

    private RowMapper<Account> accountRowMapper = row -> {
        Long id = row.getLong("id");
        String name = row.getString("user_name");
        String surname = row.getString("surname");
        String email = row.getString("email");
        Status confirmation = Status.valueOf(row.getString("confirmation"));
        return new Account(id, name, surname, email, confirmation);
    };

    public AccountDaoImpl() {
        this.connection = ConnectionUtil.getConnection();
    }

    @Override
    public Optional<Account> find(Long id) {
        return Optional.empty();
    }

    @Override
    public void save(Account model) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO accounts (user_name, surname, email, confirmation) VALUES (?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, model.getName());
            statement.setString(2, model.getSurname());
            statement.setString(3, model.getEmail());
            statement.setString(4, Status.NOT_CONFIRMED.name());
            int updRows = statement.executeUpdate();
            if (updRows == 0) {
                throw new SQLException();
            }
            try (ResultSet set = statement.getGeneratedKeys()) {
                if (set.next()) {
                    model.setId(set.getLong(1));
                } else {
                    throw new SQLException();
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void update(Account model) {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE accounts SET confirmation = ? WHERE  id = ?")) {
            statement.setString(1, Status.CONFIRMED.name());
            statement.setLong(2, model.getId());
            int updRows = statement.executeUpdate();
            if (updRows == 0) {
                throw new SQLException();
            }
            try (ResultSet set = statement.getGeneratedKeys()) {
                if (set.next()) {
                    model.setId(set.getLong(1));
                } else {
                    throw new SQLException();
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public List<Account> findAll() {
        return null;
    }

    @Override
    public Optional<Account> findByEmail(String email) {
        Account account = null;
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM accounts WHERE email = ?")) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                account = accountRowMapper.mapRow(resultSet);
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return Optional.ofNullable(account);
    }
}
