package ru.itis.application.dao.impl;

import ru.itis.application.dao.CourseDao;
import ru.itis.application.dao.RowMapper;
import ru.itis.application.models.Course;
import ru.itis.application.utils.ConnectionUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourseDaoImpl implements CourseDao {

    private Connection connection;

    private RowMapper<Course> courseRowMapper = row -> {
        Long id = row.getLong("id");
        String courseName = row.getString("course_name");
        LocalDateTime date = LocalDateTime.parse(row.getString("course_date"));
        Long userId = row.getLong("user_id");

        return new Course(id, courseName, date, userId);
    };

    public CourseDaoImpl() {
        connection = ConnectionUtil.getConnection();
    }

    @Override
    public Optional<Course> find(Long id) {
        return Optional.empty();
    }

    @Override
    public void save(Course model) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO courses (course_name, course_date, user_id) VALUES (?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, model.getCourseName());
            statement.setString(2, model.getDate().toString());
            statement.setLong(3, model.getUserId());
            int updRows = statement.executeUpdate();
            if (updRows == 0) {
                throw new SQLException();
            }
            try (ResultSet set = statement.getGeneratedKeys()) {
                if (set.next()) {
                    model.setId(set.getLong(1));
                } else {
                    throw new SQLException();
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void update(Course model) {

    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public List<Course> findAll() {
        return null;
    }

    @Override
    public List<Course> getCoursesByUser(Long user) {
        List<Course> courses = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM courses WHERE user_id = ?")) {
            statement.setLong(1, user);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                courses.add(courseRowMapper.mapRow(resultSet));
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
        return courses;
    }
}
