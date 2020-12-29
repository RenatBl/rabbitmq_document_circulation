package ru.itis.application.dao;

import ru.itis.application.models.Subject;

import java.util.Optional;

public interface SubjectDao extends CrudDao<Subject> {
    Optional<Subject> getSubjectByName(String name);
}
