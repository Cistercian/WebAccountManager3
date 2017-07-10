package ru.hd.olaf.mvc.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ru.hd.olaf.entities.User;
import ru.hd.olaf.mvc.repository.UserRepository;
import ru.hd.olaf.mvc.service.SecurityService;
import ru.hd.olaf.mvc.service.UserService;
import ru.hd.olaf.util.LogUtil;

/**
 * Created by d.v.hozyashev on 18.04.2017.
 */
@Service
public class SecurityServiceImpl implements SecurityService {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(SecurityServiceImpl.class);

    /**
     * Функция возвращает имя текущего пользователя
     *
     * @return username
     */
    public String findLoggedUsername() {
        logger.debug(LogUtil.getMethodName());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        //TODO: AuthException
        if (null == auth) {
            throw new RuntimeException("NotFoundException: current auth");
        }

        Object principal = auth.getPrincipal();
        String username;

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        logger.debug(String.format("Username: %s", username));
        return username;
    }

    /**
     * Функция возвращает текущего пользователя
     *
     * @return Сущность БД User
     */
    public User findLoggedUser() {
        logger.debug(LogUtil.getMethodName());

        String userName = findLoggedUsername();
        //if (null == userName || "anonymousUser".equalsIgnoreCase(userName)) userName = "demoUser";

        //TODO: UserNotFoundException?
        return userRepository.findByUsername(userName);
    }

    /**
     * Функция проверки пароля
     *
     * @param password переданный для проверки пароль
     * @return true при совпадении
     */
    public boolean isPasswordMatches(String password) {
        logger.debug(LogUtil.getMethodName());

        return userService.isPasswordMatches(password, findLoggedUser());
    }

    /**
     * Функция автологина пользователя после успешной регистрации (для новых пользоватей).
     *
     * @param username Имя пользователя
     * @param password Пароль
     */
    public void autologin(String username, String password) {
        logger.debug(LogUtil.getMethodName());

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());

        authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        if (usernamePasswordAuthenticationToken.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            logger.debug(String.format("autologin succesfull: %s", username));
        }
    }

    /**
     * Функция автологина для демо доступа к данным
     */
    public void demologin() {
        logger.debug(LogUtil.getMethodName());

        autologin("Demo", "1");
    }
}
