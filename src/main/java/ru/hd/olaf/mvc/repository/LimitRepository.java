package ru.hd.olaf.mvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

//    @Query("SELECT 'e' AS type, l.id AS id, l.entityName AS name, SUM(a.price) AS sum " +
//            "FROM Limit l LEFT JOIN Product p " +
//            "LEFT JOIN Amount a " +
//            "WHERE p.id = :productId AND a.productId = :p AND a.userId = l.userId AND " +
//            "l.userId = ?1 AND l.type = 'category' AND l.period = ?2 AND " +
//            "a.date BETWEEN ?3 and ?4 ")
//    List<BarEntity> findSumAmountByProductId(User user, Byte period, Date after, Date before);
}
