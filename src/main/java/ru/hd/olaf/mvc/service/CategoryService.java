package ru.hd.olaf.mvc.service;

import ru.hd.olaf.entities.Category;
import ru.hd.olaf.util.json.BarEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    List<BarEntity> getCategoriesSum(Category parentId, LocalDate after, LocalDate before);

    Map<Integer,String> getIdAndNameByCurrentUser();
    Map<Category, BigDecimal> getParentsWithTotalSum(LocalDate after, LocalDate before);
    Map<Category, BigDecimal> getCategoryPrice(List<Category> categories, LocalDate after, LocalDate before);

    Category save(Category category);
    String delete(Integer id);
}
