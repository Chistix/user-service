package com.example.userservice.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HibernateUtil {

    private static final Logger log = LoggerFactory.getLogger(HibernateUtil.class);
    private static volatile SessionFactory sessionFactory = buildSessionFactory();

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    // Метод для тестов - позволяет заменить SessionFactory
    public static void setSessionFactory(SessionFactory testSessionFactory) {
        if (sessionFactory != null && sessionFactory != testSessionFactory) {
            try {
                sessionFactory.close();
            } catch (Exception e) {
                log.warn("Error closing previous SessionFactory", e);
            }
        }
        sessionFactory = testSessionFactory;
    }

    // Метод для сброса к продакшн SessionFactory
    public static void resetSessionFactory() {
        setSessionFactory(buildSessionFactory());
    }

    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            log.error("Initial SessionFactory creation failed", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    private HibernateUtil() {}
}