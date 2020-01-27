package com.mcp.ochess.dao;

import com.mcp.ochess.model.Lobby;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.TypeHelper;
import org.hibernate.query.Query;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class LobbyDao {
    private Session session;
    private SessionFactory factory;

    @Autowired
    public void setFactory(SessionFactory factory) {
        this.factory = factory;
    }

    public void saveLobby(Lobby lobby) {
        ensureSession();
        if (lobby.getPassword() != null && lobby.getPassword().length() > 0) {
            lobby.setPassword(
                    new BCryptPasswordEncoder().encode(lobby.getPassword())
            );
        } else {
            lobby.setPassword(null);
        }

        lobby.setCreatedDate(new Timestamp(System.currentTimeMillis()));

        try {
            session.persist(lobby);
        } finally {
            close();
        }
    }

    public Lobby getLobby(int lobbyId, String password) {
        ensureSession();

        if (password != null && password.length() == 0) {
            password = null;
        }

        try {
            String queryString = "FROM Lobby WHERE id=:id";

            Query<Lobby> query = session
                    .createQuery(queryString, Lobby.class)
                    .setParameter("id", lobbyId);

            List<Lobby> lobby = query.getResultList();
            Lobby result = lobby.size() > 0 ? lobby.get(0) : null;

            if (result == null ||
                    password != null && result.getPassword() == null ||
                    password == null && result.getPassword() != null) {
                return null;
            }

            if (password == null && result.getPassword() == null) {
                return result;
            }

            if (! new BCryptPasswordEncoder().matches(password, result.getPassword())) {
                return null;
            }

            return result;
        } finally {
            close();
        }
    }

    public List<Lobby> listLobbies(int limit, int page, String search) {
        ensureSession();

        try {
            String query = "FROM Lobby";

            boolean hasSearch = search != null && search.length() > 0;
            if (hasSearch) {
                query += " WHERE name LIKE :like";
            }

            Query<Lobby> queryBuilder = session
                    .createQuery(query, Lobby.class)
                    .setMaxResults(limit)
                    .setFirstResult((limit * (page - 1)));

            if (hasSearch) {
                queryBuilder.setParameter("like", "%" + search + "%", StandardBasicTypes.STRING);
            }

            return queryBuilder
                    .getResultList();
        } finally {
            close();
        }
    }

    public void ensureSession() {
        session = factory.openSession();
        session.beginTransaction();
    }

    public void close() {
        session.getTransaction().commit();
        session.close();
    }

    public void updateLobby(Lobby lobby) {
        try {
            ensureSession();
            session.update(lobby);
        } finally {
            close();
        }
    }
}
