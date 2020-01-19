package com.mcp.ochess.dao;

import com.mcp.ochess.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao {
    private Session session;
    private SessionFactory factory;

    @Autowired
    public void setFactory(SessionFactory factory) {
        this.factory = factory;
    }

    public void saveUser(User user) {
        ensureSession();
        try {
            session.persist(user);
        } finally {
            close();
        }
    }

    public User getUser(String userName) {
        ensureSession();

        try {
            return session
                    .createQuery("FROM User " +
                            "WHERE name=:name", User.class)
                    .setParameter("name", userName)
                    .getSingleResult();
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
