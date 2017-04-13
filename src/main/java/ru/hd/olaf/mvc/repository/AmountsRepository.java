package ru.hd.olaf.mvc.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.hd.olaf.entities.Amounts;

/**
 * Created by Olaf on 13.04.2017.
 */
public interface AmountsRepository extends CrudRepository<Amounts, Integer> {

}
