package ru.hd.olaf.mvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.hd.olaf.entities.Amount;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.entities.Product;
import ru.hd.olaf.entities.User;

import java.util.Date;
import java.util.List;

/**
 * Created by Olaf on 13.04.2017.
 */
public interface AmountRepository extends CrudRepository<Amount, Integer> {
    List<Amount> findByCategoryIdAndUserId(Category category, User user);
    List<Amount> findByCategoryIdAndProductIdAndUserId(Category category, Product product, User user);

    List<Amount> findByUserId(User user);

    @Query("Select a from Amount a where a.userId = ?1 and a.amountsDate between ?2 and ?3")
    List<Amount> findByUserIdAndAmountsDateBetween(User userId, Date after, Date before);

    @Query("Select a from Amount a where a.productId = ?1 and a.userId = ?2 and a.amountsDate between ?3 and ?4")
    List<Amount> findByProductIdAndUserIdAndAmountsDateBetween(Product product, User user, Date after, Date before);
}
