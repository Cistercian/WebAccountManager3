package ru.hd.olaf.mvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hd.olaf.entities.Product;
import ru.hd.olaf.entities.User;

import java.util.List;

/**
 * Created by d.v.hozyashev on 21.04.2017.
 */
public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findByNameIgnoreCaseContaining(String name);

    List<Product> findByName(String name);

    List<Product> findByUserId(User user);

}
