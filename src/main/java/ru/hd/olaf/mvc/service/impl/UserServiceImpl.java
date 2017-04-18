package ru.hd.olaf.mvc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.hd.olaf.entities.User;
import ru.hd.olaf.mvc.repository.UserRepository;
import ru.hd.olaf.mvc.service.UserService;

/**
 * Created by d.v.hozyashev on 18.04.2017.
 *
 * Provide service for registering account
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public void save(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
