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
    public void update(long id, Map<String, ?> fieldValues) throws NoSuchElementException, IllegalArgumentException {
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
        List<List> lists;
        if (asc)
            lists = superviseDao.getEntityManager().createQuery("select s.supervisor,count(s.examination) as c from SuperviseRecord s where s.examination.examDate > :today group by s.supervisor order by c asc").setParameter("today", LocalDate.now()).setFirstResult(begIndex).setMaxResults(count).getResultList();
        else
            lists = superviseDao.getEntityManager().createQuery("select s.supervisor,count(s.examination) as c from SuperviseRecord s where s.examination.examDate > :today group by s.supervisor order by c desc").setParameter("today", LocalDate.now()).setFirstResult(begIndex).setMaxResults(count).getResultList();
        return lists.stream().map(l -> ((Teacher) l.get(0))).collect(Collectors.toList());
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public long countUser() {
        return userDao.getEntityManager().createQuery("select count(t.id) from Teacher t", Long.class).getSingleResult();
    }
}
