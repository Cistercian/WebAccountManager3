package ru.hd.olaf.mvc.service.impl;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hd.olaf.entities.Categories;
import ru.hd.olaf.mvc.repository.CategoriesRepository;
import ru.hd.olaf.mvc.service.CategoriesService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Olaf on 13.04.2017.
 */
@Service
public class CategoriesServiceImpl implements CategoriesService {

    @Autowired
    CategoriesRepository categoriesRepository;

    public List<Categories> getAll() {
        return Lists.newArrayList(categoriesRepository.findAll());
    }

    public Categories getById(int id) {
        return categoriesRepository.findOne(id);
    }

}
