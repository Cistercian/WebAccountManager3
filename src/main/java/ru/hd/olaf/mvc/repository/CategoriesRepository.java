package ru.hd.olaf.mvc.repository;

import org.springframework.data.repository.CrudRepository;
import ru.hd.olaf.entities.Categories;

import java.util.List;

/**
 * Created by d.v.hozyashev on 11.04.2017.
 */
public interface CategoriesRepository extends CrudRepository<Categories, Integer>{
    List<Categories> findByParentId(Integer parentId);
    List<Categories> findByName(String name);
}
