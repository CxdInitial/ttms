package me.cxd.service;

import me.cxd.bean.Classroom;
import me.cxd.bean.Examination;
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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class ExamServiceImpl implements ExamService {
    private final JpaDao<Examination> examDao;
    private final JpaDao<SuperviseRecord> superviseDao;
    private final JpaDao<Classroom> classroomDao;
    private final JpaDao<Teacher> userDao;

    @Autowired
    public ExamServiceImpl(JpaDao<Examination> examDao, JpaDao<SuperviseRecord> superviseDao, JpaDao<Classroom> classroomDao, JpaDao<Teacher> userDao) {
        this.examDao = examDao;
        this.superviseDao = superviseDao;
        this.classroomDao = classroomDao;
        this.userDao = userDao;
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Examination find(long id) {
        return examDao.read(id);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public long count() {
        return examDao.getEntityManager().createQuery("select count(e.id) from Examination e", Long.class).getSingleResult();
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public long count(Long number, Long classroomId, LocalDate beg, LocalDate end, Short begNo, Short endNo) throws NoSuchElementException {
        EntityManager entityManager = examDao.getEntityManager();
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        if (number == null) {
            Root<Examination> root = query.from(Examination.class);
            query.select(builder.count(root.get("id")));
            if (classroomId != null) {
                if (classroomDao.read(classroomId)==null)
                    throw new NoSuchElementException("Found no classroom with the ID:" + classroomId);
                query.where(builder.equal(root.get("classroom").get("id"), classroomId));
            }
            query.where(builder.between(root.get("date"), Optional.of(beg).orElse(LocalDate.MIN), Optional.of(end).orElse(LocalDate.MAX)));
            query.where(builder.ge(root.get("begin"), Optional.of(begNo).orElse((short) 1)));
            query.where(builder.le(root.get("end"), Optional.of(endNo).orElse((short) 11)));
            return entityManager.createQuery(query).getSingleResult();
        }
        Root<SuperviseRecord> root = query.from(SuperviseRecord.class);
        query.select(builder.count(root.get("examination")));
        query.distinct(true);
        if (classroomId != null) {
            if (classroomDao.read(classroomId)==null)
                throw new NoSuchElementException("Found no classroom with the ID:" + classroomId);
            query.where(builder.equal(root.get("examination").get("classroom").get("id"), classroomId));
        }
        query.where(builder.between(root.get("examination").get("date"), Optional.of(beg).orElse(LocalDate.MIN), Optional.of(end).orElse(LocalDate.MAX)));
        query.where(builder.ge(root.get("examination").get("begin"), Optional.of(begNo).orElse((short) 1)));
        query.where(builder.le(root.get("examination").get("end"), Optional.of(endNo).orElse((short) 11)));
        return entityManager.createQuery(query).getSingleResult();
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<Examination> find(long begIndex, long count, Long number, Long classroomId, LocalDate beg, LocalDate end, Short begNo, Short endNo) throws NoSuchElementException {
        EntityManager entityManager = examDao.getEntityManager();
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Examination> query = builder.createQuery(Examination.class);
        if (number == null) {
            Root<Examination> root = query.from(Examination.class);
            query.select(root);
            if (classroomId != null) {
                if (classroomDao.read(classroomId)==null)
                    throw new NoSuchElementException("Found no classroom with the ID:" + classroomId);
                query.where(builder.equal(root.get("classroom").get("id"), classroomId));
            }
            query.where(builder.between(root.get("date"), Optional.of(beg).orElse(LocalDate.MIN), Optional.of(end).orElse(LocalDate.MAX)));
            query.where(builder.ge(root.get("begin"), Optional.of(begNo).orElse((short) 1)));
            query.where(builder.le(root.get("end"), Optional.of(endNo).orElse((short) 11)));
            return entityManager.createQuery(query).getResultList();
        }
        Root<SuperviseRecord> root = query.from(SuperviseRecord.class);
        query.select(root.get("examination"));
        query.distinct(true);
        if (classroomId != null) {
            if (classroomDao.read(classroomId)==null)
                throw new NoSuchElementException("Found no classroom with the ID:" + classroomId);
            query.where(builder.equal(root.get("examination").get("classroom").get("id"), classroomId));
        }
        query.where(builder.between(root.get("examination").get("date"), Optional.of(beg).orElse(LocalDate.MIN), Optional.of(end).orElse(LocalDate.MAX)));
        query.where(builder.ge(root.get("examination").get("begin"), Optional.of(begNo).orElse((short) 1)));
        query.where(builder.le(root.get("examination").get("end"), Optional.of(endNo).orElse((short) 11)));
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public long add(Examination examination, long classroomId) throws NoSuchElementException, IllegalArgumentException {
        Classroom classroom = classroomDao.read(classroomId);
        if (classroom == null)
            throw new NoSuchElementException("Found no classroom with the ID:" + classroomId);
        try {
            examDao.create(examination);
            examination.setClassroom(classroom);
        } catch (PersistenceException e) {
            throw new IllegalArgumentException("The exam to be added should not has ID.", e);
        }
        return examination.getId();
    }

    @Override
    public void modify(long id, Examination examination, long classroomId) throws NoSuchElementException, IllegalArgumentException {
        examDao.delete(id);
        long old = add(examination, classroomId);
        examDao.getEntityManager().createQuery("update Examination e set e.id = :n where e.id = :old").setParameter("n", id).setParameter("old", old).executeUpdate();
    }

    @Override
    public void addSupervisor(long id, long number) throws NoSuchElementException {
        Examination exam = find(id);
        if (exam == null)
            throw new NoSuchElementException("Found no exam with the ID: " + id);
        Teacher teacher = userDao.read(number);
        if (teacher == null)
            throw new NoSuchElementException("Found no user with number: " + number);
        SuperviseRecord record = new SuperviseRecord();
        record.setExamination(exam);
        record.setSupervisor(teacher);
        superviseDao.create(record);
    }

    @Override
    public void removeSupervisor(long id, long number) throws NoSuchElementException {
        if (find(id) == null)
            throw new NoSuchElementException("Found no exam with the ID: " + id);
        if (userDao.read(number) == null)
            throw new NoSuchElementException("Found no user with number: " + number);
        if (superviseDao.getEntityManager().createQuery("delete from SuperviseRecord s where s.examination.id=:id and s.supervisor.id=:number").executeUpdate() != 1)
            throw new NoSuchElementException("Found no supervise record with the given exam ID and supervisor number.");
    }

    @Override
    public void remove(long id) throws NoSuchElementException {
        if (find(id) == null)
            throw new NoSuchElementException("Found no exam with the ID: " + id);
        examDao.delete(id);
    }
}
