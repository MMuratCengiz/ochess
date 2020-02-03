package com.mcp.ochess.dao;

import com.mcp.ochess.model.Player;
import com.mcp.ochess.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("userDetailsService")
public class UserPlayerService implements org.springframework.security.core.userdetails.UserDetailsService {
    private IUserRepository   userRepository;
    private IPlayerRepository playerRepository;

    @Autowired
    public void setUserRepository(IUserRepository repository) {
        this.userRepository = repository;
    }

    @Autowired
    public void setPlayerRepository(IPlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        List<User> user = userRepository.findByName(username);
        if (user.size() == 0) {
            throw new UsernameNotFoundException(String.format("User by name %s not found.", username));
        }

        return user.get(0);
    }

    @Transactional
    public void updateUser(User user) {
        userRepository.save(user);
    }

    @Transactional
    public void newUser(User user) {
        Player player = new Player();
        user.setPlayer(player);
        userRepository.save(user);
    }

    @Transactional
    public void updatePlayer(Player player) {
        playerRepository.save(player);
    }
}
