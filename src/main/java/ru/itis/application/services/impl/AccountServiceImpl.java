package ru.itis.application.services.impl;

import ru.itis.application.dao.AccountDao;
import ru.itis.application.dao.impl.AccountDaoImpl;
import ru.itis.application.models.Account;
import ru.itis.application.services.AccountService;

public class AccountServiceImpl implements AccountService {

    private AccountDao accountDao;

    public AccountServiceImpl() {
        accountDao = new AccountDaoImpl();
    }

    @Override
    public void newAccount(Account account) {
        accountDao.save(account);
    }

    @Override
    public void confirmAccount(String email) {
        Account account = getAccountByEmail(email);
        accountDao.update(account);
    }

    @Override
    public Account getAccountByEmail(String email) {
        return accountDao.findByEmail(email).orElseThrow(() ->
                new IllegalArgumentException("Wrong email")
        );
    }
}
