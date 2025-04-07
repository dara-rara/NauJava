package ru.glebova.NauJava.adapter.repository.custom;

public interface CrudRepository<T, ID> {
    void create(T entity);

    T read(ID id);

    void update(T entity);

    void delete(ID id);
}