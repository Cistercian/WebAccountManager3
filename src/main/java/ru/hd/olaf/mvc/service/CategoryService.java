package ru.hd.olaf.mvc.service;

import ru.hd.olaf.entities.Category;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by Olaf on 13.04.2017.
 */
public interface CategoryService {

    List<Category> getAll();
    Category getById(int id);
    Map<Integer,String> getIdAndName();
    Category add(Category category);
    Map<Category, BigDecimal> getAllWithTotalSum();
}
