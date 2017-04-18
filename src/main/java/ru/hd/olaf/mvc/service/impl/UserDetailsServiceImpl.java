package ru.hd.olaf.mvc.service.impl;

import com.google.common.collect.Lists;
import org.codehaus.jackson.map.ser.std.StdArraySerializers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.hd.olaf.entities.User;
import ru.hd.olaf.mvc.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by d.v.hozyashev on 18.04.2017.
 *
 * To implement login/authentication with Spring Security, we need to implement
 org.springframework.security.core.userdetails.UserDetailsService interface
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
        User user = userRepository.findByUsername(username);

        List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        grantedAuthorities.add(new SimpleGrantedAuthority(user.getRole()));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), grantedAuthorities);
    }

}
