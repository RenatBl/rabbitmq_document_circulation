package ru.itis.application.services;

import ru.itis.application.models.Subject;

public interface SubjectService {
    void createNewSubject(Subject subject);
    Subject getSubjectByName(String name);
}
