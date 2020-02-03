package com.mcp.ochess.dao;

import com.mcp.ochess.model.Lobby;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Lob;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service("lobbyService")
public class LobbyService {
    private ILobbyRepository repository;

    @Autowired
    public void setRepository(ILobbyRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void newLobby(Lobby lobby) {
        if (lobby.getPassword() != null && lobby.getPassword().length() > 0) {
            lobby.setPassword(
                    new BCryptPasswordEncoder().encode(lobby.getPassword())
            );
        } else {
            lobby.setPassword(null);
        }

        lobby.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        repository.save(lobby);
    }

    @Transactional(readOnly = true)
    public Lobby getLobby(int lobbyId, String password) {
        if (password != null && password.length() == 0) {
            password = null;
        }

        Optional<Lobby> optionalResult = repository.findById(lobbyId);

        if (optionalResult.get() == null) {
            return null;
        }

        Lobby result = optionalResult.get();


        if (password != null && result.getPassword() == null || password == null && result.getPassword() != null) {
            return null;
        }

        if (password == null) {
            return result;
        }

        if (! new BCryptPasswordEncoder().matches(password, result.getPassword())) {
            return null;
        }

        return result;
    }

    @Transactional(readOnly = true)
    public List<Lobby> listLobbies(String search, int limit, int page) {
        if (search == null) {
            search = "";
        }

        return repository.findAllByNameContaining(search, PageRequest.of(page - 1, limit));
    }

    @Transactional
    public void save(Lobby lobby) {
        repository.save(lobby);
    }

    @Transactional
    public void delete(Lobby lobby) {
        repository.delete(lobby);
    }
}
