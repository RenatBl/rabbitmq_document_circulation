package ru.itis.application.utils;

import ru.itis.application.models.Account;

public class PassCreator {

    public static void createPass(Account account) {
        PdfConverter.fillInfoInPass(account);
    }
}
