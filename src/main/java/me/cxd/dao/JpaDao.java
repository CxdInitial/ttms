package me.cxd.dao;

import javax.persistence.EntityManager;

public interface JpaDao<T> extends Crud<T> {
    EntityManager getEntityManager();
}
