package ru.hd.olaf.mvc.service;

import ru.hd.olaf.entities.User;

import java.util.List;

/**
 * Created by d.v.hozyashev on 18.04.2017.
 */
public interface UserService {
    List<User> getAll();

    void save(User user);

    User findByUsername(String username);

    boolean isPasswordMatches(String password, User user);
}
