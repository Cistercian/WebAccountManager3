package ru.hd.olaf.mvc.service;

import ru.hd.olaf.entities.Amount;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.entities.Product;
import ru.hd.olaf.entities.User;
import ru.hd.olaf.exception.AuthException;
import ru.hd.olaf.exception.CrudException;
import ru.hd.olaf.util.json.BarEntity;
import ru.hd.olaf.util.json.CalendarEntity;
import ru.hd.olaf.util.json.JsonResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by Olaf on 13.04.2017.
 */
public interface AmountService {

    Amount getOne(Integer id) throws AuthException, IllegalArgumentException;

    JsonResponse getById(Integer id);

    List<Amount> getAll();

    List<Amount> getByProductAndDate(User user, Product product, LocalDate after, LocalDate before);

    List<Amount> getByProductAndCategoryAndDate(User user,
                                                Product product,
                                                Category category,
                                                LocalDate after,
                                                LocalDate before);

    List<Amount> getByCategoryAndProduct(Category category, Product product);

    List<Amount> getByCategory(Category category);

    List<Amount> getByDate(User user, LocalDate after, LocalDate before);

    List<Amount> getByMatchingName(User user, String query, LocalDate after, LocalDate before);

    List<Amount> getAllRegular(User user);

    List<BarEntity> getBarEntitiesByCategory(User user,
                                             Category category,
                                             LocalDate after,
                                             LocalDate before);

    List<Amount> getByType(User user, Category category, LocalDate after, LocalDate before, Integer type);

    List<Amount> getAmountsForBindingByType(User user,
                                            Category category,
                                            LocalDate after,
                                            LocalDate before,
                                            Byte type);

    List<Amount> getAvgOfPrevMonths(User user,
                                    Product product,
                                    Category category);

    Amount save(Amount amount) throws CrudException;

    JsonResponse delete(Amount amount) throws CrudException;

    BigDecimal getSumByCategoryAndProduct(Category category, Product product, LocalDate after, LocalDate before);

    BigDecimal getSumByCategoryType(Byte type, User user, LocalDate after, LocalDate before);

    BigDecimal getCompareAvgPrice(User user, String query, LocalDate after, LocalDate before);

    List<CalendarEntity> getCalendarEntities(User user,
                                             LocalDate after,
                                             LocalDate before,
                                             Category category,
                                             Product product);
}
