package ru.itis.application.dao.impl;

import ru.itis.application.dao.RowMapper;
import ru.itis.application.dao.SubjectDao;
import ru.itis.application.models.Subject;
import ru.itis.application.utils.ConnectionUtil;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class SubjectDaoImpl implements SubjectDao {

    private Connection connection;

    private RowMapper<Subject> subjectRowMapper = row -> {
        Long id = row.getLong("id");
        String courseName = row.getString("course_name");

        return new Subject(id, courseName);
    };

    public SubjectDaoImpl() {
        connection = ConnectionUtil.getConnection();
    }

    @Override
    public Optional<Subject> getSubjectByName(String name) {
        Subject subject = null;
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM subjects WHERE course_name = ?")) {
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                subject = subjectRowMapper.mapRow(resultSet);
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return Optional.ofNullable(subject);
    }

    @Override
    public Optional<Subject> find(Long id) {
        return Optional.empty();
    }

    @Override
    public void save(Subject model) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO subjects (course_name) VALUES (?)",
                Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, model.getCourseName());
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
    public void update(Subject model) {

    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public List<Subject> findAll() {
        return null;
    }
}
