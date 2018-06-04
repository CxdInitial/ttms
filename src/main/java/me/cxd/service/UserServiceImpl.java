package me.cxd.service;

import me.cxd.bean.SuperviseRecord;
import me.cxd.bean.Teacher;
import me.cxd.dao.JpaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(noRollbackFor = NoSuchElementException.class)
public class UserServiceImpl implements UserService {
    private final JpaDao<Teacher> userDao;
    private final JpaDao<SuperviseRecord> superviseDao;

    @Autowired
    public UserServiceImpl(JpaDao<Teacher> userDao, JpaDao<SuperviseRecord> superviseDao) {
        this.userDao = userDao;
        this.superviseDao = superviseDao;
    }

    @Override
    public void register(Teacher teacher) throws IllegalArgumentException {
        try {
            userDao.create(teacher);
        } catch (PersistenceException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Some errors happened", e);
        }
    }

    @Override
    public void update(long id, Map<String, String> fieldValues) throws NoSuchElementException, IllegalArgumentException {
        if (find(id) == null)
            throw new NoSuchElementException("Found no user with id: " + id);
        try {
            userDao.update(id, fieldValues);
        } catch (PersistenceException e) {
            throw new IllegalArgumentException("Some errors happened.", e);
        }
    }

    @Override
    public void remove(long id) throws NoSuchElementException {
        if (find(id) == null)
            throw new NoSuchElementException("Found no user with id: " + id);
        userDao.delete(id);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Teacher find(long id) {
        return userDao.read(id);
    }

    @Override
    public Teacher findByPhone(String phoneNumber) {
        List<Teacher> list = userDao.getEntityManager().createQuery("from Teacher where phone=:phone", Teacher.class).setParameter("phone", phoneNumber).getResultList();
        if (list.isEmpty())
            return null;
        return list.get(0);
    }

    @Override
    public Teacher findByNo(long teacherNo) {
        List<Teacher> list = userDao.getEntityManager().createQuery("from Teacher where teacherNo=:no", Teacher.class).setParameter("no", teacherNo).getResultList();
        if (list.isEmpty())
            return null;
        return list.get(0);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<Teacher> find(Order order, int begIndex, int count, boolean asc) {
        if (order != Order.BUSYNESS) {
            EntityManager entityManager = userDao.getEntityManager();
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Teacher> query = builder.createQuery(Teacher.class);
            Root<Teacher> root = query.from(Teacher.class);
            query.select(root);
            if (asc)
                query.orderBy(builder.asc(root.get(order.value())));
            else
                query.orderBy(builder.desc(root.get(order.value())));
            return entityManager.createQuery(query).setFirstResult(begIndex).setMaxResults(count).getResultList();
        }
        List<BigInteger> ids;
        if (asc)
            ids = superviseDao.getEntityManager().createNativeQuery("select userId from (select t.id as userId,count(s.id) as c from teacher as t left join superviserecord as s on t.id=s.supervisor_id group by t.id order by c asc) temp").getResultList();
        else
            ids = superviseDao.getEntityManager().createNativeQuery("select userId from (select t.id as userId,count(s.id) as c from teacher as t left join superviserecord as s on t.id=s.supervisor_id group by t.id order by c desc) temp").getResultList();
        return ids.stream().map(BigInteger::longValue).map(userDao::read).collect(Collectors.toList());
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public long countUser() {
        return userDao.getEntityManager().createQuery("select count(t.id) from Teacher t", Long.class).getSingleResult();
    }
}
