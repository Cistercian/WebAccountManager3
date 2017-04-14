package ru.hd.olaf.mvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hd.olaf.entities.Amounts;

/**
 * Created by Olaf on 13.04.2017.
 */
public interface AmountsRepository extends JpaRepository<Amounts, Integer> {

}
