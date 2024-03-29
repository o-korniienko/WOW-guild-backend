package com.wowguild.service.entity;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public interface EntityService<T> {

    void save(T entity);

    List<T> getAll();

    List<T> saveAll(List<T> entities);

    List<T> findAll();

    void delete(T entity);

    List<T> sort(List<T> entities, Comparator<T> comparator1, Comparator<T> comparator2);

}
