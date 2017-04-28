package ru.hd.olaf.mvc.service;

import ru.hd.olaf.entities.Category;
import ru.hd.olaf.exception.AuthException;
import ru.hd.olaf.exception.CrudException;
import ru.hd.olaf.util.json.BarEntity;
import ru.hd.olaf.util.json.JsonResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Created by Olaf on 13.04.2017.
 */
public interface CategoryService {

    JsonResponse getById(Integer id);

    List<Category> getAll();
    List<Category> getAllByCurrentUser();
    List<BarEntity> getBarEntityOfSubCategories(Category parent, LocalDate after, LocalDate before);


    Category save(Category category) throws CrudException;
    JsonResponse delete(Category category) throws CrudException;
}
