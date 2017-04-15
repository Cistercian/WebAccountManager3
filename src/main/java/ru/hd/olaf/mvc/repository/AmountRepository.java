package ru.hd.olaf.mvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hd.olaf.entities.Amount;
import ru.hd.olaf.entities.Category;

import java.util.List;

/**
 * Created by Olaf on 13.04.2017.
 */
public interface AmountRepository extends JpaRepository<Amount, Integer> {
    List<Amount> findByCategoryId(Category categoryId);
}
