package ru.itis.application.services;

import ru.itis.application.CourseDto;
import ru.itis.application.models.Course;

public interface CourseService {
    void completeCourse(CourseDto courseDto);
    Course getCourseByUserIdAndCourseName(Long id, String name);
}
