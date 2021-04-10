package com.foxminded.sqlschool.schoolrepository;

import com.foxminded.sqlschool.connection.ConnectionBuilder;
import com.foxminded.sqlschool.dto.Group;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GroupDao implements GenericDao<Group, Integer> {

    private final static Logger logger = Logger.getLogger(CourseDao.class);

    private static final String FIND_GROUP_BY_ID = "SELECT GROUP_ID, GROUP_NAME FROM groups WHERE GROUP_ID = ?";
    private static final String FIND_ALL_GROUPS = "SELECT GROUP_ID, GROUP_NAME FROM groups";
    private static final String INSERT_GROUP = "INSERT INTO groups (GROUP_NAME) VALUES (?);";
    private static final String DELETE_GROUP_BY_ID = "DELETE FROM groups WHERE GROUP_ID = ?";
    private static final String GET_GROUPS_AMOUNT_LESS_OR_EQUAL = "select students.group_id,\n" +
        "groups.group_name, count (students.group_id) \n" +
        "from students\n" +
        "inner join groups on students.group_id = groups.group_id\n" +
        "group by students.group_id, groups.group_id\n" +
        "having count (students.group_id) <= ?;";

    @Override
    public Optional<Group> getById(Integer id) throws DaoException {
        try (
            Connection connection = ConnectionBuilder.getConnection();
            PreparedStatement statement = connection.prepareStatement(FIND_GROUP_BY_ID)
        ) {

            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int groupId = resultSet.getInt("GROUP_ID");
                    String name = resultSet.getString("GROUP_NAME");
                    return Optional.of(new Group(groupId, name));
                }
            }
        } catch (SQLException e) {
            logger.warn("Can't get group with id=" + id + " from the database", e);
            throw new DaoException("Error getting group from the database ", e);
        }

        return Optional.empty();
    }

    @Override
    public List<Group> getAll() throws DaoException {
        List<Group> groups = new ArrayList<>();
        try (
            Connection connection = ConnectionBuilder.getConnection();
            PreparedStatement statement = connection.prepareStatement(FIND_ALL_GROUPS);
            ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {
                int groupId = resultSet.getInt("GROUP_ID");
                String name = resultSet.getString("GROUP_NAME");
                groups.add(new Group(groupId, name));
            }

        } catch (SQLException e) {
            logger.warn("Can't get all groups from database", e);
            throw new DaoException("Error getting all groups from the database ", e);
        }

        return groups;
    }

    @Override
    public void insert(Group group) throws DaoException {
        try (
            Connection connection = ConnectionBuilder.getConnection();
            PreparedStatement statement = connection.prepareStatement(INSERT_GROUP)
        ) {

            statement.setString(1, group.getGroupName());
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.warn("Can't insert group " + group.getGroupName() + " to database", e);
            throw new DaoException("Error inserting group to the database ", e);
        }
    }

    @Override
    public void delete(Integer id) throws DaoException {
        try (
            Connection connection = ConnectionBuilder.getConnection();
            PreparedStatement statement = connection.prepareStatement(DELETE_GROUP_BY_ID)
        ) {

            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.warn("Can't delete group with id=" + id + " from the database", e);
            throw new DaoException("Error deleting group from the database ", e);
        }
    }

    public List<Group> getGroupsWithAMountLessOrEqual(int participantsLimit) throws DaoException {
        List<Group> groupNames = new ArrayList<>();
        try (
            Connection connection = ConnectionBuilder.getConnection();
            PreparedStatement statement = connection.prepareStatement(GET_GROUPS_AMOUNT_LESS_OR_EQUAL)
        ) {

            statement.setInt(1, participantsLimit);
            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    int id = resultSet.getInt("GROUP_ID");
                    String name = (resultSet.getString("GROUP_NAME"));
                    groupNames.add(new Group(id, name));
                }
            }
        } catch (SQLException e) {
            logger.warn("Can't get groups with amount less or equal then "
                + participantsLimit + " from the database", e);
            throw new DaoException("Error during getting groups ", e);
        }
        return groupNames;
    }

}
