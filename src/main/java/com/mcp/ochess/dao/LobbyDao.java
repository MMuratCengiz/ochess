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
        if (lobby.getPassword() != null) {
            lobby.setPassword(
                    new BCryptPasswordEncoder().encode(lobby.getPassword())
            );
        }
        lobby.setCreatedDate(new Timestamp(System.currentTimeMillis()));

        try {
            session.persist(lobby);
        } finally {
            close();
        }
    }

    public Lobby getLobby(int lobbyId) {
        ensureSession();

        try {
            return session
                    .createQuery("FROM Lobby " +
                            "WHERE id=:id", Lobby.class)
                    .setParameter("id", lobbyId)
                    .getSingleResult();
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
}
