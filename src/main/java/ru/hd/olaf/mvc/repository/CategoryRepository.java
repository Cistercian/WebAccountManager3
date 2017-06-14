package ru.hd.olaf.mvc.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.entities.User;
import ru.hd.olaf.util.json.AnalyticEntity;
import ru.hd.olaf.util.json.BarEntity;

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
            "AND a.type != 3" +
            "GROUP BY c.id HAVING COUNT(c.id) > 0")
    List<BarEntity> getBarEntityByUserIdAndSubCategory(User user, Category category, Date after, Date before);


    @Query("SELECT new ru.hd.olaf.util.json.BarEntity(" +
            "CASE " +
            "WHEN p IS NOT NULL THEN 'CategoryChild' " +
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
            "AND a.type != 3" +
            "GROUP BY c.id " +
            " HAVING COUNT(c.id) > 0")
    List<BarEntity> getBarEntityOfParentsByUserId(User user, Date after, Date before);

    @Query("SELECT new ru.hd.olaf.util.json.AnalyticEntity(" +
            "CASE " +
            "WHEN p IS NOT NULL THEN 'CategoryChild' " +
            "WHEN c.type = 0 THEN 'CategoryIncome' " +
            "WHEN c.type = 1 THEN 'CategoryExpense' " +
            "END " +
            ", c.id, " +
            "SUM(a.price), " +
            "c.name," +
            "MAX(a.date)," +
            "MIN(a.date)) " +
            "FROM Amount a " +
            "LEFT JOIN a.categoryId c LEFT JOIN c.parentId p " +
            "WHERE c.userId = ?1  AND " +
            "((?2 = null AND p IS NULL) OR (a.categoryId = ?2)) AND " +
            "a.date BETWEEN ?3 AND ?4 AND " +
            "((?5 = true AND (a.type = 0 OR a.type = 1)) OR " +
            "(?6 = true AND a.type != 3) OR " +
            "(?7 = true AND (a.type = 1 OR a.type = 2))) " +
            "GROUP BY c.id " +
            " HAVING COUNT(c.id) > 0")
    List<AnalyticEntity> getAnalyticDataByCategory(User user,
                                                   Category category,
                                                   Date after,
                                                   Date before,
                                                   boolean isNeedAvgSum,
                                                   boolean isNeedRealSum,
                                                   boolean isNeedOneTimeSum);

    @Query("SELECT new ru.hd.olaf.util.json.AnalyticEntity(" +
            "CASE " +
            "WHEN c.parentId IS NOT NULL THEN 'CategoryChild' " +
            "WHEN (c.type = 0) THEN 'CategoryIncome' " +
            "WHEN c.type = 1 THEN 'CategoryExpense' " +
            "END " +
            ", c.id, " +
            "SUM(a.price), " +
            "c.name," +
            "MAX(a.date)," +
            "MIN(a.date)) " +
            "FROM Amount a " +
            "LEFT JOIN a.regularId r " +
            "LEFT JOIN a.categoryId c " +
            "WHERE c.userId = ?1 AND " +
            "(?2 = null OR c.parentId = ?2) AND " +
            "a.type = 3 AND " +
            "r.date BETWEEN ?3 AND ?4 AND " +
            "r.id IS NULL  " +
            "GROUP BY c.id " +
            "HAVING COUNT(c.id) > 0")
    List<AnalyticEntity> getAnalyticDataOfRegularByCategory(User user,
                                                           Category category,
                                                           Date after,
                                                           Date before);

    @Query("SELECT new ru.hd.olaf.util.json.AnalyticEntity(" +
            "'Product' " +
            ", p.id, " +
            "SUM(a.price), " +
            "p.name," +
            "MAX(a.date)," +
            "MIN(a.date)) " +
            "FROM Amount a LEFT JOIN a.productId p " +
            "WHERE a.userId = ?1  AND " +
            "a.categoryId = ?2 AND " +
            "a.date BETWEEN ?3 AND ?4 AND " +
            "((?5 = true AND (a.type = 0 OR a.type = 1)) OR " +
            "(?6 = true AND a.type != 3) OR " +
            "(?7 = true AND (a.type = 1 OR a.type = 2))) " +
            "GROUP BY p.id " +
            " HAVING COUNT(p.id) > 0")
    List<AnalyticEntity> getAnalyticDataByProduct(User user,
                                                  Category category,
                                                  Date after,
                                                  Date before,
                                                  boolean isNeedAvgSum,
                                                  boolean isNeedRealSum,
                                                  boolean isNeedOneTimeSum);

    @Query("SELECT new ru.hd.olaf.util.json.AnalyticEntity(" +
            "'Product', " +
            "p.id, " +
            "SUM(a.price), " +
            "p.name," +
            "MAX(a.date)," +
            "MIN(a.date)) " +
            "FROM Amount a " +
            "LEFT JOIN a.regularId r " +
            "LEFT JOIN a.productId p " +
            "WHERE a.userId = ?1  AND " +
            "a.categoryId = ?2 AND " +
            "a.type = 3 AND " +
            "r.date BETWEEN ?3 AND ?4 AND " +
            "r.id IS NULL  " +
            "GROUP BY p.id " +
            "HAVING COUNT(p.id) > 0")
    List<AnalyticEntity> getAnalyticDataOfRegularByProduct( User user,
                                                            Category category,
                                                            Date after,
                                                            Date before );
}
