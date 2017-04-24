package ru.hd.olaf.mvc.service;

import ru.hd.olaf.entities.Category;
import ru.hd.olaf.util.json.BarEntity;
import ru.hd.olaf.util.json.JsonAnswer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Created by Olaf on 13.04.2017.
 */
public interface CategoryService {

    Category getById(Integer id);

    List<Category> getAll();
    List<Category> getAllByCurrentUser();
    List<BarEntity> getBarEntityOfSubCategories(Category parent, LocalDate after, LocalDate before);

    Category save(Category category);
    JsonAnswer delete(Category category);
}
