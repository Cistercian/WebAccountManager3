package ru.hd.olaf.services;

import ru.hd.olaf.entities.CategoriesEntity;

import java.util.List;

/**
 * Created by d.v.hozyashev on 11.04.2017.
 */
public interface CategoriesService {

    List<CategoriesEntity> findAll();
    List<CategoriesEntity> findByParentId(Integer parentId);
    List<CategoriesEntity> findByName(String name);
}
