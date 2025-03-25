package ru.glebova.NauJava.adapter.custom;

public interface CrudRepository<T, ID> {
    void create(T entity);

    T read(ID id);

    void update(T entity);

    void delete(ID id);
}