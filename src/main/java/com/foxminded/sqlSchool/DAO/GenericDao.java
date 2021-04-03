package com.foxminded.sqlSchool.DAO;

import java.util.Optional;

public interface GenericDao<T> {
    Optional<T> getById(int id);

    void create(T t);

    void delete(T t);

    void update(T t);
}
