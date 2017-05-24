package ru.hd.olaf.mvc.service.impl;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.hd.olaf.entities.User;
import ru.hd.olaf.mvc.repository.UserRepository;
import ru.hd.olaf.mvc.service.UserService;
import ru.hd.olaf.util.LogUtil;

import java.util.List;

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

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    /**
     * Функция возвращает список всех пользователей
     * @return List
     */
    public List<User> getAll() {
        logger.debug(LogUtil.getMethodName());

        return Lists.newArrayList(userRepository.findAll());
    }

    public void save(User user) {
        logger.debug(LogUtil.getMethodName());

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public boolean isPasswordMatches(String password, User user) {
        return bCryptPasswordEncoder.matches(password, user.getPassword());
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
