package ru.itis.application.producers;

import ru.itis.application.models.Subject;
import ru.itis.application.services.SubjectService;
import ru.itis.application.services.impl.SubjectServiceImpl;

import java.util.Scanner;

public class SubjectCreator {

    private static SubjectService subjectService = new SubjectServiceImpl();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        subjectService.createNewSubject(Subject.builder()
                .courseName(scanner.nextLine())
                .build());
    }
}
