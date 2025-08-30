package com.example.userservice.dao;

import com.example.userservice.entity.User;
import com.example.userservice.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {
    private static final Logger log = LoggerFactory.getLogger(UserDaoImpl.class);

    @Override
    public User create(User user) {
        return doInTransaction(session -> {
            session.persist(user);
            return user;
        });
    }

    @Override
    public Optional<User> findById(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(User.class, id));
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from User", User.class).getResultList();
        }
    }

    @Override
    public User update(User user) {
        return doInTransaction(session -> {
            session.merge(user);
            return user;
        });
    }

    @Override
    public boolean deleteById(long id) {
        return doInTransaction(session -> {
            User u = session.get(User.class, id);
            if (u == null) return false;
            session.remove(u);
            return true;
        });
    }

    private <T> T doInTransaction(HibernateWork<T> work) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            T result = work.apply(session);
            tx.commit();
            return result;
        } catch (ConstraintViolationException e) {
            rollbackQuietly(tx);
            log.warn("Constraint violation: {}", e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            rollbackQuietly(tx);
            log.error("Transaction failed", e);
            throw e;
        }
    }

    private void rollbackQuietly(Transaction tx) {
        if (tx != null && tx.getStatus().canRollback()) {
            try { tx.rollback(); } catch (Exception ignored) { }
        }
    }

    @FunctionalInterface
    private interface HibernateWork<T> {
        T apply(Session session);
    }
}

