package ru.hd.olaf.mvc.service;

import org.springframework.cglib.core.Local;
import ru.hd.olaf.entities.Amount;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.entities.Product;
import ru.hd.olaf.entities.User;
import ru.hd.olaf.util.json.BarEntity;
import ru.hd.olaf.util.json.JsonAnswer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Created by Olaf on 13.04.2017.
 */
public interface AmountService {

    Amount getById(Integer id);

    List<Amount> getAll();
    List<Amount> getByProduct(Product product);
    List<Amount> getByCategoryAndProduct(Category category, Product product);
    List<Amount> getByCategory(Category category);

    List<BarEntity> getBarEntitiesByCategory(Category category,
                                             LocalDate after,
                                             LocalDate before);

    Amount save(Amount amount);
    JsonAnswer delete(Amount amount);

    BigDecimal getSumByCategoryAndProduct(Category category, Product product, LocalDate after, LocalDate before);
}
