package ua.softserveinc.tc.service;

import java.util.List;

public interface BaseService<T> {
    List<T> findAll();

    void create(T entity);

    T findById(Object id);

    void delete(T entity);

    void deleteAll();

    T update(T entity);
}
