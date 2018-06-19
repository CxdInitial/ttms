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
import javax.persistence.criteria.CriteriaUpdate;
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
            throw new IllegalArgumentException("Some errors happened", e);
        }
    }

    @Override
    public void update(long id, Map<String, Object> fieldValues) throws NoSuchElementException, IllegalArgumentException {
        if (find(id) == null)
            throw new NoSuchElementException("Found no user with id: " + id);
        CriteriaBuilder builder = userDao.getEntityManager().getCriteriaBuilder();
        CriteriaUpdate<Teacher> update = builder.createCriteriaUpdate(Teacher.class);
        Root<Teacher> root = update.from(Teacher.class);
        fieldValues.forEach(update::set);
        update.where(builder.equal(root.get("id"), id));
        if (userDao.getEntityManager().createQuery(update).executeUpdate() != 1)
            throw new IllegalArgumentException("Some errors happened.");
    }

    @Override
    public void update(long id, Teacher teacher) throws NoSuchElementException, IllegalArgumentException {
        Teacher user = userDao.read(id);
        if (user == null)
            throw new NoSuchElementException("Found no user with id: " + id);
        CriteriaBuilder builder = userDao.getEntityManager().getCriteriaBuilder();
        CriteriaUpdate<Teacher> update = builder.createCriteriaUpdate(Teacher.class);
        Root<Teacher> root = update.from(Teacher.class);
        update.set("teacherNo", teacher.getTeacherNo());
        update.set("loginPassword", teacher.getLoginPassword());
        update.set("title", teacher.getTitle());
        update.set("intro", teacher.getIntro());
        update.set("phone", teacher.getPhone());
        update.set("male", teacher.getMale());
        update.set("manager", teacher.getManager());
        update.where(builder.equal(root.get("id"), id));
        if (userDao.getEntityManager().createQuery(update).executeUpdate() != 1)
            throw new IllegalArgumentException("Some errors happened.");
    }

    @Override
    public void remove(long id) throws NoSuchElementException {
        Teacher teacher;
        if ((teacher = find(id)) == null)
            throw new NoSuchElementException("Found no user with id: " + id);
        userDao.getEntityManager().remove(teacher);
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
            ids = superviseDao.getEntityManager().createNativeQuery("select teacher.id from teacher left join (select superviserecord.supervisor_id,count(examination.id) ce from examination right join superviserecord on examination.id=superviserecord.examination_id where examination.examDate>=CURDATE() group by superviserecord.supervisor_id)temp on temp.supervisor_id=teacher.id order by temp.ce asc").getResultList();
        else
            ids = superviseDao.getEntityManager().createNativeQuery("select teacher.id from teacher left join (select superviserecord.supervisor_id,count(examination.id) ce from examination right join superviserecord on examination.id=superviserecord.examination_id where examination.examDate>=CURDATE() group by superviserecord.supervisor_id)temp on temp.supervisor_id=teacher.id order by temp.ce desc").getResultList();
        return ids.stream().map(id->userDao.read(id.longValue())).collect(Collectors.toList());
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public long countUser() {
        CriteriaBuilder builder = userDao.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        query.select(builder.count(query.from(Teacher.class).get("id")));
        return userDao.getEntityManager().createQuery(query).getSingleResult();
    }
}
