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
import ru.hd.olaf.util.LogUtil;

/**
 * Created by d.v.hozyashev on 18.04.2017.
 *
 * We create SecurityService to provide current loggedin user and auto login user after resgistering an account.
 */
@Service
public class SecurityServiceImpl implements SecurityService {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(SecurityServiceImpl.class);

    public String findLoggedUsername() {
        logger.debug(LogUtil.getMethodName());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        //TODO: Exception
        if (null == auth) {
            throw new RuntimeException("NotFoundException: current auth");
        }

        Object principal = auth.getPrincipal();
        String username = "";

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        return username;
    }

    public User findLoggedUser() {
        logger.debug(LogUtil.getMethodName());

        String userName = findLoggedUsername();
        //if (null == userName || "anonymousUser".equalsIgnoreCase(userName)) userName = "demoUser";

        //TODO: UserNotFoundException?
        return userRepository.findByUsername(userName);
    }

    public void autologin(String username, String password) {
        logger.debug(LogUtil.getMethodName());

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());

        authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        if (usernamePasswordAuthenticationToken.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            System.out.println("autologin succesfull!" + username);
        }
    }
}
