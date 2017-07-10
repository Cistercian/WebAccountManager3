package ru.hd.olaf.mvc.service;

import ru.hd.olaf.entities.User;

/**
 * Created by d.v.hozyashev on 18.04.2017.
 */
public interface SecurityService {
    String findLoggedUsername();

    boolean isPasswordMatches(String password);

    User findLoggedUser();

    void autologin(String username, String password);

    void demologin();
}
