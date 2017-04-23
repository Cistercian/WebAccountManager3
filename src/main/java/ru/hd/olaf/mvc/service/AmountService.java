package ru.hd.olaf.mvc.service;

import org.springframework.cglib.core.Local;
import ru.hd.olaf.entities.Amount;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.entities.Product;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Created by Olaf on 13.04.2017.
 */
public interface AmountService {

    Amount getById(int id);
    List<Amount> getAll();
    List<Amount> getByCategory(Category categoryId);
    List<Amount> getByProduct(Product product);
    List<Amount> getByCategoryAndProduct(Category category, Product product);

    Amount add(Amount amount);
    String delete(Integer id);

    BigDecimal getSumByCategoryAndProduct(Category category, Product product, LocalDate after, LocalDate before);
}
