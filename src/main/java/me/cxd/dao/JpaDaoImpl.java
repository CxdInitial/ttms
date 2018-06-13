package me.cxd.dao;

import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;

public class JpaDaoImpl<T> implements JpaDao<T> {
    @PersistenceContext
    private EntityManager entityManager;

    private final Class<T> clz;

    public JpaDaoImpl(Class<T> clz) {
        this.clz = clz;
    }

    @Override
    public void create(T t) {
        entityManager.persist(t);
    }

    @Override
    public T read(long id) {
        return entityManager.find(clz, id);
    }

    @Override
    public void update(long id, T t) {
        entityManager.remove(entityManager.find(clz, id));
        entityManager.persist(t);
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaUpdate<T> criteriaUpdate = builder.createCriteriaUpdate(clz);
        criteriaUpdate.from(clz);
        String idField = Arrays.stream(clz.getDeclaredFields()).filter(field -> field.getAnnotation(Id.class) != null).map(Field::getName).findFirst().get();
        char[] chars = idField.toCharArray();
        chars[0] -= 32;
        String getter = "get" + String.valueOf(chars);
        try {
            criteriaUpdate.where(builder.equal(criteriaUpdate.getRoot().get(idField), clz.getMethod(getter).invoke(t)));
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new PersistenceException("No getter found or getter has bad format.", e);
        }
        criteriaUpdate.set(idField, id);
        entityManager.createQuery(criteriaUpdate).executeUpdate();
    }

    @Override
    public void update(long id, Map<String, String> fields) {
        assert 1 == entityManager.createQuery(String.format("update %s set %s where id=%d", clz.getSimpleName(), fields.entrySet().stream().map(field -> field.getKey() + "=" + String.valueOf(field.getValue())).reduce((a, b) -> a + "," + b).get(), id)).executeUpdate();
    }

    @Override
    public void delete(long id) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaDelete<T> delete = builder.createCriteriaDelete(clz);
        Root<T> root = delete.from(clz);
        delete.where(builder.equal(root.get("id"), id));
        assert 1 == entityManager.createQuery(delete).executeUpdate();
        entityManager.flush();
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
