package ru.itis.application.services.impl;

import ru.itis.application.dao.SubjectDao;
import ru.itis.application.dao.impl.SubjectDaoImpl;
import ru.itis.application.models.Subject;
import ru.itis.application.services.SubjectService;

public class SubjectServiceImpl implements SubjectService {

    private SubjectDao subjectDao;

    public SubjectServiceImpl() {
        subjectDao = new SubjectDaoImpl();
    }

    @Override
    public void createNewSubject(Subject subject) {
        subjectDao.save(subject);
    }

    @Override
    public Subject getSubjectByName(String name) {
        return subjectDao.getSubjectByName(name)
                .orElseThrow(() ->
                        new IllegalArgumentException("Wrong name")
                );
    }
}
