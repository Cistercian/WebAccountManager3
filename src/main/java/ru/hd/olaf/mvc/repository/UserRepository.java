package ru.hd.olaf.mvc.repository;

import org.springframework.data.repository.CrudRepository;
import ru.hd.olaf.entities.User;

/**
 * Created by d.v.hozyashev on 18.04.2017.
 */
public interface UserRepository extends CrudRepository<User, Integer> {
    User findByUsername(String username);
}
