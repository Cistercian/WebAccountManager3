package ru.hd.olaf.mvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import ru.hd.olaf.entities.Amount;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.entities.Product;
import ru.hd.olaf.entities.User;

import java.util.List;

/**
 * Created by Olaf on 13.04.2017.
 */
public interface AmountRepository extends CrudRepository<Amount, Integer> {
    List<Amount> findByCategoryIdAndUserId(Category category, User user);
    List<Amount> findByCategoryIdAndProductIdAndUserId(Category category, Product product, User user);
    List<Amount> findByProductIdAndUserId(Product product, User user);
    List<Amount> findByUserId(User user);
}
