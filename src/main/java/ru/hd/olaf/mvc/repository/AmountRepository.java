package ru.hd.olaf.mvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
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

    @Query("Select a from Amount a where a.userId = ?1 and a.date between ?2 and ?3")
    List<Amount> findByUserIdAndDateBetween(User userId, Date after, Date before);

    @Query("Select a from Amount a where a.productId = ?1 and a.userId = ?2 and a.date between ?3 and ?4")
    List<Amount> findByProductIdAndUserIdAndDateBetween(Product product, User user, Date after, Date before);

    @Query("Select a from Amount a where a.productId = ?1 and a.categoryId = ?2 and a.userId = ?3 and a.date between ?4 and ?5")
    List<Amount> findByProductIdAndCategoryIdAndUserIdAndDateBetween(Product product, Category category, User user, Date after, Date before);

    @Query("SELECT new ru.hd.olaf.util.json.BarEntity(" +
                "'Product', " +
                "p.id, " +
                "SUM(a.price), " +
                "p.name) " +
            "FROM Amount a LEFT JOIN a.productId p WHERE " +
            "a.userId = ?1 AND a.categoryId = ?2 AND " +
            "a.date BETWEEN ?3 AND ?4 " +
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
            "a.date BETWEEN ?3 AND ?4")
    BigDecimal getSumByTypeAndUserIdAndDate(Byte type, User userId, Date after, Date before);

}
