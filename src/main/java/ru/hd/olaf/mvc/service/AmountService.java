package ru.hd.olaf.mvc.service;

import ru.hd.olaf.entities.Amount;
import ru.hd.olaf.entities.Category;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by Olaf on 13.04.2017.
 */
public interface AmountService {

    Amount add(Amount amount);
    List<Amount> getAll();
    List<Amount> getAllByCategoryId(Category categoryId);
    Amount getById(int id);
    String delete(Integer id);
}
