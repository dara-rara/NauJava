package ru.glebova.NauJava.repository;

public interface CrudRepository<T, ID> {
    void create(T entity);

    T read(ID id);

    void update(T entity);

    void delete(ID id);
}