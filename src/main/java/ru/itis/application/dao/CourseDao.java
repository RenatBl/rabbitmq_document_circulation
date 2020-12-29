package ru.itis.application.dao;

import ru.itis.application.models.Course;

import java.util.List;

public interface CourseDao extends CrudDao<Course> {
    List<Course> getCoursesByUser(Long user);
}
