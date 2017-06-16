package ru.hd.olaf.mvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.hd.olaf.entities.Category;
import ru.hd.olaf.entities.Product;
import ru.hd.olaf.entities.User;

import java.util.List;

/**
 * Created by d.v.hozyashev on 21.04.2017.
 */
public interface ProductRepository extends CrudRepository<Product, Integer> {

    List<Product> findByUserIdAndNameIgnoreCaseContaining(User user, String name);

    List<Product> findByNameAndUserId(String name, User user);

    @Query("SELECT p FROM Amount a " +
            "LEFT JOIN a.productId p " +
            "WHERE a.userId = ?1 AND " +
            "a.categoryId = ?2 " +
            "GROUP BY p.id " +
            "ORDER BY p.name")
    List<Product> getAllByCategory(User user, Category category);

    List<Product> findByUserId(User user);

}
