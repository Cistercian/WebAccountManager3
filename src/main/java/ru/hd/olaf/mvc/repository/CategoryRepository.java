package ru.hd.olaf.mvc.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.entities.User;
import ru.hd.olaf.util.json.BarEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by d.v.hozyashev on 11.04.2017.
 */
public interface CategoryRepository extends CrudRepository<Category, Integer> {
    List<Category> findByParentIdAndUserId(Category parent, User user);

    List<Category> findByName(String name);

    List<Category> findByUserId(User user);

    @Query("SELECT new ru.hd.olaf.util.json.BarEntity(" +
            "CASE " +
            "WHEN (c.type = 0) THEN 'CategoryIncome' " +
            "WHEN (c.type = 1) THEN 'CategoryExpense' " +
            "END, " +
            "c.id, " +
            "SUM(a.price), " +
            "c.name) " +
            "FROM Amount a LEFT JOIN a.categoryId c " +
            "WHERE c.userId = ?1 AND c.parentId = ?2 AND " +
            "a.date BETWEEN ?3 AND ?4 " +
            "GROUP BY c.id HAVING COUNT(c.id) > 0")
    List<BarEntity> getBarEntityByUserIdAndSubCategory(User user, Category category, Date after, Date before);


    @Query("SELECT new ru.hd.olaf.util.json.BarEntity(" +
            "CASE " +
            "WHEN p IS NOT NULL THEN 'child' " +
            "WHEN (c.type = 0) THEN 'CategoryIncome' " +
            "WHEN c.type = 1 THEN 'CategoryExpense' " +
            "" +
            "END " +
            ", c.id, " +
            "CASE " +
            "WHEN (c.type = 0) THEN SUM(a.price) "+
            "ELSE SUM(-a.price) " +
            "END, " +
            "c.name) " +
            "FROM Amount a LEFT JOIN a.categoryId c LEFT JOIN c.parentId p " +
            "WHERE c.userId = ?1  AND " +
            "a.date BETWEEN ?2 AND ?3 " +
            "GROUP BY c.id " +
            " HAVING COUNT(c.id) > 0")
    List<BarEntity> getBarEntityOfParentsByUserId(User user, Date after, Date before);

    @Query("SELECT new ru.hd.olaf.util.json.BarEntity(" +
            "CASE " +
            "WHEN (c.type = 0) THEN 'CategoryIncome' " +
            "ELSE 'CategoryExpense' " +
            "END, " +
            "c.id, " +
            "AVG(a.price), " +
            "c.name) " +
            "FROM Amount a " +
            "LEFT JOIN a.categoryId c " +
            "WHERE c.userId = ?1 AND " +
            "((?2 = null AND c.parentId IS NULL) OR (?2 != null AND c.parentId = ?2)) AND " +
            "a.date BETWEEN ?3 AND ?4 " +
            "GROUP BY a.productId ") /*HAVING COUNT(c.id) > 0 */
    List<BarEntity> getCategoriesForAnalytic(User user, Category category, Date after, Date before);

    @Query("SELECT new ru.hd.olaf.util.json.BarEntity(" +
            "'Product', " +
            "p.id, " +
            "AVG(a.price), " +
            "p.name) " +
            "FROM Amount a " +
            "LEFT JOIN a.productId p " +
            "WHERE a.userId = ?1 AND " +
            "a.categoryId = ?2 AND " +
            "a.date BETWEEN ?3 AND ?4 " +
            "GROUP BY a.productId HAVING COUNT(p.id) > 0 ")
    List<BarEntity> getProductsForAnalytic(User user, Category category, Date after, Date before);
}
