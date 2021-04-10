package com.foxminded.sqlschool.schoolrepository;

import java.util.List;
import java.util.Optional;

public interface GenericDao<T, K> {
    Optional<T> getById(K id) throws DaoException;

    List<T> getAll() throws DaoException;

    void insert(T t) throws DaoException;

    void delete(K id) throws DaoException;

}
