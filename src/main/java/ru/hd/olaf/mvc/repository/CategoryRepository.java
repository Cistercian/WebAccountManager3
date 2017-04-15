package ru.hd.olaf.mvc.repository;

import org.springframework.data.repository.CrudRepository;
import ru.hd.olaf.entities.Category;

import java.util.List;

/**
 * Created by d.v.hozyashev on 11.04.2017.
 */
public interface CategoryRepository extends CrudRepository<Category, Integer>{
    List<Category> findByParentId(Integer parentId);
    List<Category> findByName(String name);
}
