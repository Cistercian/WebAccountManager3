package ru.hd.olaf.mvc.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.entities.User;
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
                "CASE WHEN (c.type = 0) THEN " +
                "'CategoryIncome' " +
                "WHEN (c.type = 1) THEN " +
                "'CategoryExpense' END " +
                ", c.id, SUM(a.price), c.name) " +
            "FROM Amount a LEFT JOIN a.categoryId c " +
            "WHERE a.categoryId = c AND c.userId = ?1 AND c.parentId = ?2 AND " +
            "a.date BETWEEN ?3 AND ?4 " +
            "GROUP BY c.id HAVING COUNT(c.id) > 0")
    List<BarEntity> getBarEntityByUserIdAndSubCategory(User user, Category category, Date before, Date after);

    @Query("SELECT new ru.hd.olaf.util.json.BarEntity(" +
            "CASE WHEN (c.type = 0) THEN " +
            "'CategoryIncome' " +
            "WHEN (c.type = 1) THEN " +
            "'CategoryExpense' END " +
            ", c.id, SUM(a.price), c.name) " +
            "FROM Amount a LEFT JOIN a.categoryId c " +
            "WHERE a.categoryId = c AND c.userId = ?1 AND c.parentId IS NULL AND " +
            "a.date BETWEEN ?2 AND ?3 " +
            "GROUP BY c.id HAVING COUNT(c.id) > 0")
    List<BarEntity> getBarEntityOfParentsByUserId(User user, Date before, Date after);
}
