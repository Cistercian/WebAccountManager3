package ru.hd.olaf.mvc.service;

import ru.hd.olaf.entities.Category;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by Olaf on 13.04.2017.
 */
public interface CategoryService {

    Category getById(int id);

    List<Category> getAll();
    List<Category> getAllByCurrentUser();
    List<Category> getByParentId(Category parentId);

    Map<Integer,String> getIdAndNameByCurrentUser();
    Map<Category, BigDecimal> getWithTotalSum();
    Map<Category, BigDecimal> getCategoryPrice(List<Category> categories);

    Category add(Category category);
    String delete(Integer id);
}
