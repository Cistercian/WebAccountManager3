package ru.hd.olaf.mvc.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.hd.olaf.entities.User;

import java.util.List;

/**
 * Created by d.v.hozyashev on 18.04.2017.
 */
public interface UserRepository extends CrudRepository<User, Integer> {
    User findByUsername(String username);

    @Query("FROM User u WHERE u.role = 'ROLE_ADMIN' OR u.username = 'Olaf'")
    List<User> findAdmins();
}
