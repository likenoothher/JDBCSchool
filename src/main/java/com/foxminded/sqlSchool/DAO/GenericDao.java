package com.foxminded.sqlSchool.DAO;

import java.util.List;
import java.util.Optional;

public interface GenericDao<T> {
    Optional<T> getById(int id);

    List<T> getAll();

    void insert(T t);

    void delete(T t);

    void update(T t);

}
