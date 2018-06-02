package me.cxd.dao;

import java.util.Map;

public interface Crud<T> {
    void create(T t);

    T read(long id);

    void update(long id, T t);

    void update(long id, Map<String, ?> fields);

    void delete(long id);
}
