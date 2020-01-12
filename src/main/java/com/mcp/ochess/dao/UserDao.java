package com.mcp.ochess.dao;

import com.mcp.ochess.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class UserDao {
//    @Autowired
    private SessionFactory factory;
    private Session session;

    public void setFactory(SessionFactory factory) {
        this.factory = factory;
    }

    public void saveUser(User user) {
        ensureSession();
        session.persist(user);
        close();
    }

    public User getUser(String userName) {
        ensureSession();

        try {
            return session
                    .createQuery("FROM Users WHERE name=?", User.class)
                    .setParameter(0, userName)
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
        session.close();
        session.getTransaction().commit();;
    }
}
