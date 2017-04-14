package ru.hd.olaf.mvc.service;

import ru.hd.olaf.entities.Categories;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Olaf on 13.04.2017.
 */
public interface CategoriesService {

    List<Categories> getAll();
    Categories getById(int id);

}
