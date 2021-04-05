package com.foxminded.sqlSchool.DAO;

import com.foxminded.sqlSchool.ConnectionBuilder;
import com.foxminded.sqlSchool.DTO.Group;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GroupDao implements GenericDao<Group>{
    private static final String INSERT_GROUP = "INSERT INTO groups" + " (GROUP_NAME) VALUES (?);";
    private static final String FIND_GROUP_BY_NAME = "SELECT GROUP_ID, GROUP_NAME FROM groups WHERE GROUP_NAME = ?";
    private static final String FIND_ALL_GROUPS = "SELECT * FROM groups";

    @Override
    public Optional<Group> getById(int id) {
        return Optional.empty();
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
            throw new RuntimeException("Error getting all groups from the database ", e);
        }

        return groups;
    }

    public Group findGroupByName(String groupName) { /////////fix!!!!!!!!!!!!
        Group group = null;
        try (Connection connection = ConnectionBuilder.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_GROUP_BY_NAME);) {
            preparedStatement.setString(1, groupName);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                group = new Group(resultSet.getInt(1), resultSet.getString(2));
            }
        } catch (SQLException ex) {
            System.out.println("+++++++++" + ex.getLocalizedMessage());
        }
        System.out.println(group.getGroupName());
        return group;
    }

    @Override
    public void insert(Group group) {
        try (Connection connection = ConnectionBuilder.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_GROUP);) {
            preparedStatement.setString(1, group.getGroupName());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting group to the database ", e);
        }
    }

    @Override
    public void delete(Group group) {

    }

    @Override
    public void update(Group group) {

    }
}
