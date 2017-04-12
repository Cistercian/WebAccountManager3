package ru.hd.olaf.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import ru.hd.olaf.entities.CategoriesEntity;

import java.util.List;

/**
 * Created by d.v.hozyashev on 11.04.2017.
 */
public interface ContactRepository extends CrudRepository<CategoriesEntity, Integer>{
    List<CategoriesEntity> findByParentId(Integer parentId);
    List<CategoriesEntity> findByName(String name);
}
