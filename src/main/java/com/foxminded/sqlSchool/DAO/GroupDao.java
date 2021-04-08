package com.foxminded.sqlSchool.DAO;

import com.foxminded.sqlSchool.DTO.Group;
import com.foxminded.sqlSchool.connection.ConnectionBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GroupDao implements GenericDao<Group> {
    private static final String FIND_GROUP_BY_ID = "SELECT * FROM groups WHERE GROUP_ID = ?";
    private static final String INSERT_GROUP = "INSERT INTO groups (GROUP_NAME) VALUES (?);";
    private static final String FIND_ALL_GROUPS = "SELECT * FROM groups";
    private static final String DELETE_GROUP_BY_ID = "DELETE FROM groups WHERE GROUP_ID = ?";

    @Override
    public Optional<Group> getById(IdKey id) {
        Group group;
        try (Connection connection = ConnectionBuilder.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_GROUP_BY_ID)) {
            group = new Group();
            preparedStatement.setInt(1, id.getFirstId());

            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();

            group.setId(resultSet.getInt("GROUP_ID"));
            group.setGroupName(resultSet.getString("GROUP_NAME"));

            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error getting group from the database " + e.getLocalizedMessage());
        }

        return Optional.of(group);
    }

    @Override
    public List<Group> getAll() {
        List<Group> groups = new ArrayList<>();
        try (Connection connection = ConnectionBuilder.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_GROUPS);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Group group = new Group();
                group.setId(resultSet.getInt("GROUP_ID"));
                group.setGroupName(resultSet.getString("GROUP_NAME"));
                groups.add(group);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting all groups from the database " + e.getLocalizedMessage());
        }

        return groups;
    }

    @Override
    public void insert(Group group) {
        try (Connection connection = ConnectionBuilder.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_GROUP)) {
            preparedStatement.setString(1, group.getGroupName());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting group to the database " + e.getLocalizedMessage());
        }
    }

    @Override
    public void delete(Group group) {
        try (Connection connection = ConnectionBuilder.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_GROUP_BY_ID)) {
            preparedStatement.setInt(1, group.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting group from the database " + e.getLocalizedMessage());
        }
    }

}
