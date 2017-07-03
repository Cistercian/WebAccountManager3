package ru.hd.olaf.mvc.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.hd.olaf.entities.Amount;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.entities.Product;
import ru.hd.olaf.entities.User;
import ru.hd.olaf.util.json.BarEntity;
import ru.hd.olaf.util.json.CalendarEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by Olaf on 13.04.2017.
 */
public interface AmountRepository extends CrudRepository<Amount, Integer> {
    List<Amount> findByCategoryIdAndUserId(Category category, User user);

    List<Amount> findByCategoryIdAndProductIdAndUserId(Category category, Product product, User user);

    List<Amount> findByUserId(User user);

    List<Amount> findByRegularId(Amount regularId);

    @Query("Select a from Amount a where a.userId = ?1 and a.date between ?2 and ?3 and a.type != 3")
    List<Amount> findByUserIdAndDateBetween(User userId, Date after, Date before);

    @Query("Select a from Amount a where a.userId = ?1 and a.type = 3")
    List<Amount> getAllRegular(User userId);

    @Query("Select a from Amount a where a.productId = ?1 and a.userId = ?2 and a.date between ?3 and ?4 and a.type != 3")
    List<Amount> findByProductIdAndUserIdAndDateBetween(Product product, User user, Date after, Date before);

    @Query("Select a from Amount a where a.productId = ?1 and a.categoryId = ?2 and a.userId = ?3 and a.date between ?4 and ?5 and a.type != 3")
    List<Amount> findByProductIdAndCategoryIdAndUserIdAndDateBetween(Product product, Category category, User user, Date after, Date before);

    @Query("SELECT new ru.hd.olaf.util.json.BarEntity(" +
            "'Product', " +
            "p.id, " +
            "SUM(a.price), " +
            "p.name) " +
            "FROM Amount a LEFT JOIN a.productId p WHERE " +
            "a.userId = ?1 AND a.categoryId = ?2 AND " +
            "a.date BETWEEN ?3 AND ?4 " +
            "AND a.type != 3" +
            "GROUP BY p.id HAVING COUNT(p.id) > 0")
    List<BarEntity> getBarEntityByUserIdAndCategoryIdAndDateGroupByProductId(User user,
                                                                             Category category,
                                                                             Date after,
                                                                             Date before);

    @Query("SELECT COALESCE(SUM(a.price), 0) AS price FROM Amount a " +
            "JOIN a.categoryId c " +
            "WHERE a.categoryId = c AND " +
            "c.type = ?1 AND " +
            "a.userId = ?2 AND " +
            "a.date BETWEEN ?3 AND ?4 " +
            "AND a.type != 3")
    BigDecimal getSumByTypeAndUserIdAndDate(Byte type, User userId, Date after, Date before);

    @Query("SELECT new ru.hd.olaf.util.json.CalendarEntity(" +
            "SUM( CASE WHEN c.type = 0 THEN a.price ELSE -a.price END ) ," +
            "a.date )" +
            "FROM Amount a " +
            "JOIN a.categoryId c " +
            "WHERE a.userId = ?1 AND " +
            "a.date BETWEEN ?2 AND ?3 AND " +
            "a.type != 3 AND " +
            "((?4 != NULL AND c = ?4) OR (?4 = NULL)) AND " +
            "((?5 != NULL AND a.productId = ?5) OR (?5 = NULL)) " +
            "GROUP BY a.date")
    List<CalendarEntity> getCalendarData(User userId, Date after, Date before, Category category, Product product);

    @Query("Select a from Amount a " +
            "LEFT JOIN a.productId p " +
            "LEFT JOIN a.categoryId c " +
            "WHERE a.userId = ?1 and a.date between ?3 and ?4 and " +
            "(UPPER(a.name) LIKE ?2 OR " +
            "UPPER(p.name) LIKE ?2 OR " +
            "UPPER(c.name) LIKE ?2) " +
            "AND a.type != 3")
    List<Amount> getByUserIdAndMatchingNameAndDateBetween(User userId, String query, Date after, Date before);

    @Query("Select AVG(a.price) from Amount a " +
            "LEFT JOIN a.productId p " +
            "LEFT JOIN a.categoryId c " +
            "WHERE a.userId = ?1 and a.date between ?3 and ?4 and " +
            "(UPPER(a.name) LIKE ?2 OR " +
            "UPPER(p.name) LIKE ?2 OR " +
            "UPPER(c.name) LIKE ?2) " +
            "AND a.type != 3")
    BigDecimal getAvgPriceByUserIdAndMatchingNameAndDateBetween(User userId, String query, Date after, Date before);


    @Query("Select a FROM Amount a " +
            "LEFT JOIN a.categoryId c " +
            "WHERE a.userId = ?1 AND " +
            "(?2 = NULL OR (?2 != NULL AND c = ?2)) AND " +
            "((?5 != 3 AND a.date BETWEEN ?3 AND ?4) OR (?5 = 3))AND " +
            "((?5 != 1 AND ?5 != 2 AND ?5 != NULL AND a.type = ?5) OR" +
            "((?5 = 1 OR ?5 = 2) AND (a.type = 1 OR a.type = 2)) OR " +
            "(?5 = NULL))")
    List<Amount> getByType(User user, Category category, Date after, Date before, Integer type);

    @Query("Select a FROM Amount a " +
            "LEFT JOIN a.categoryId c WHERE " +
            "a.userId = ?1 AND " +
            "c = ?2 AND " +
            "a.date BETWEEN ?3 AND ?4 AND " +
            "a.type != ?5")
    List<Amount> getAmountsForBinding(User user, Category category, Date after, Date before, Byte type);
}
