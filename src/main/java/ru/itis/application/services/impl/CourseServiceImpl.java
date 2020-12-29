package ru.itis.application.services.impl;

import ru.itis.application.CourseDto;
import ru.itis.application.dao.CourseDao;
import ru.itis.application.dao.impl.CourseDaoImpl;
import ru.itis.application.models.Account;
import ru.itis.application.models.Course;
import ru.itis.application.models.Subject;
import ru.itis.application.services.AccountService;
import ru.itis.application.services.CourseService;
import ru.itis.application.services.SubjectService;

import java.time.LocalDateTime;
import java.util.List;

public class CourseServiceImpl implements CourseService {

    private CourseDao courseDao;
    private SubjectService subjectService;
    private AccountService accountService;

    public CourseServiceImpl() {
        courseDao = new CourseDaoImpl();
        subjectService = new SubjectServiceImpl();
        accountService = new AccountServiceImpl();
    }

    @Override
    public void completeCourse(CourseDto courseDto) {
        Subject subject = subjectService.getSubjectByName(courseDto.getCourseName());
        Account account = accountService.getAccountByEmail(courseDto.getUserEmail());
        courseDao.save(Course.builder()
                .courseName(subject.getCourseName())
                .date(LocalDateTime.now())
                .userId(account.getId())
                .build());
    }

    @Override
    public Course getCourseByUserIdAndCourseName(Long id, String name) {
        List<Course> courses = courseDao.getCoursesByUser(id);
        return courses.stream()
                .filter(course -> course.getCourseName().equals(name))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("No such course")
                );
    }
}
