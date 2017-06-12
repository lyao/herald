package org.church.volyn.data.database;

import java.util.List;

public interface Dao<T> {
    long save(T type);
    void update(T entity);
    T get(String id);
    T get(long id);
    List<T> getAll();
    List<T> getNonEmpty();
}


