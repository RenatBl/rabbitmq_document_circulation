package ru.itis.application.services;

import ru.itis.application.models.Account;

public interface AccountService {
    void newAccount(Account account);
    void confirmAccount(String email);
    Account getAccountByEmail(String email);
}
