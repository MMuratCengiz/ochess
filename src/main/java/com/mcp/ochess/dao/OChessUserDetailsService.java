package com.mcp.ochess.dao;

import com.mcp.ochess.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service("userDetailsService")
public class OChessUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = new UserDao().getUser(username);

        ArrayList<GrantedAuthority> l = new ArrayList<>();
        l.add(new SimpleGrantedAuthority(u.getPrivilegeCode() + ""));

        return new org.springframework.security.core.userdetails.User(
                u.getName(), u.getPassword(), l
        );
    }

    public void storeUser(User user) {
        new UserDao().saveUser(user);
    }
}
