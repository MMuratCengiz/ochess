package com.mcp.ochess.dao;

import com.mcp.ochess.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service("userDetailsService")
public class OChessUserDetailsService implements UserDetailsService {
    private UserDao repository;

    @Autowired
    public void setRepository(UserDao repository) {
        this.repository = repository;
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.getUser(username);
    }

    public void storeUser(User user) {
        repository.saveUser(user);
    }
}
