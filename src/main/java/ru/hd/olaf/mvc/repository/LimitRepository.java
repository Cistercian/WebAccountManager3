package ru.hd.olaf.mvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.entities.Limit;
import ru.hd.olaf.entities.Product;
import ru.hd.olaf.entities.User;
import ru.hd.olaf.util.json.BarEntity;

import java.util.Date;
import java.util.List;

/**
 * Created by Olaf on 08.05.2017.
 */
public interface LimitRepository extends JpaRepository<Limit, Integer> {
    List<Limit> findByUserId(User user);

    Limit findByUserIdAndPeriodAndCategoryId(User user, Byte period, Category categoryId);
    Limit findByUserIdAndPeriodAndProductId(User user, Byte period, Product productId);

    @Query("SELECT new ru.hd.olaf.util.json.BarEntity(l.type, p.id, SUM(a.price), l.entityName, l.sum) " +
            "FROM Limit l " +
            "LEFT JOIN l.productId p " +
            "LEFT JOIN p.amounts a " +
            "WHERE  a.productId = p AND " +
            "l.type = 'product' AND " +
            "l.userId = ?1 AND l.period = ?2 and a.date BETWEEN ?3 AND ?4 " +
            "GROUP BY l.id " +
            "HAVING l.sum > 0 ")
    List<BarEntity> findLimitAndSumAmountsByProduct(User user, Byte period, Date after, Date before);

    @Query("SELECT new ru.hd.olaf.util.json.BarEntity(l.type, c.id, SUM(a.price), l.entityName, l.sum) " +
            "FROM Limit l " +
            "LEFT JOIN l.categoryId c " +
            "LEFT JOIN c.amounts a " +
            "WHERE  a.categoryId = c AND " +
            "l.categoryId = c AND " +
            "l.type = 'category' AND " +
            "l.userId = ?1 AND l.period = ?2 and a.date BETWEEN ?3 AND ?4 " +
            "GROUP BY l.id " +
            "HAVING l.sum > 0 ")
    List<BarEntity> findLimitAndSumAmountsByCategory(User user, Byte period, Date after, Date before);
}
