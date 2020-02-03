package com.mcp.ochess.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DaoBase {
    protected Session session;
    private SessionFactory factory;

    @Autowired
    public void setFactory(SessionFactory factory) {
        this.factory = factory;
    }

    public void ensureSession(boolean openTransaction) {
        session = factory.openSession();
        if (openTransaction) {
            session.beginTransaction();
        }
    }

    public void close(boolean commit) {
        if (commit) {
            session.getTransaction().commit();
        }
        session.close();
    }
}
