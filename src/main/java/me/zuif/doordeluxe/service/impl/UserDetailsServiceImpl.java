package me.zuif.doordeluxe.service.impl;

import me.zuif.doordeluxe.model.user.Role;
import me.zuif.doordeluxe.model.user.User;
import me.zuif.doordeluxe.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) {
        User user = userRepository.findByUserNameOrEmail(usernameOrEmail, usernameOrEmail);
        if ("N/A".equals(user.getPassword())) {
            throw new BadCredentialsException("Account is linked with Google. Use Google login.");
        }
        if (user != null) {
            Set<GrantedAuthority> authorities = new HashSet<>();
            if (user.getRole() == Role.DIRECTOR) {
                authorities.add(new SimpleGrantedAuthority("ROLE_DIRECTOR"));
            } else if (user.getRole() == Role.USER) {
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            } else if (user.getRole() == Role.GUEST) {
                authorities.add(new SimpleGrantedAuthority("ROLE_GUEST"));
            } else if (user.getRole() == Role.MANAGER) {
                authorities.add(new SimpleGrantedAuthority("ROLE_MANAGER"));
            }
            logger.debug(String.format("User with name: %s and password: %s created.", user.getUserName(), user.getPassword()));
            return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(), authorities);
        } else {
            throw new UsernameNotFoundException("User " + usernameOrEmail + " not found!");
        }
    }


}