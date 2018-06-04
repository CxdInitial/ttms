package me.cxd.service;

import me.cxd.bean.Examination;
import me.cxd.bean.SuperviseRecord;
import me.cxd.bean.Teacher;
import me.cxd.dao.JpaDao;
import me.cxd.web.controller.Exam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(noRollbackFor = NoSuchElementException.class)
public class ExamServiceImpl implements ExamService {
    private final JpaDao<Examination> examDao;
    private final JpaDao<SuperviseRecord> superviseDao;
    private final JpaDao<Teacher> userDao;

    @Autowired
    public ExamServiceImpl(JpaDao<Examination> examDao, JpaDao<SuperviseRecord> superviseDao, JpaDao<Teacher> userDao) {
        this.examDao = examDao;
        this.superviseDao = superviseDao;
        this.userDao = userDao;
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Examination find(long id) {
        return examDao.read(id);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public long count(Long teacherId, String area, String classroomNo, LocalDate beg, LocalDate end, Short begNo, Short endNo) throws NoSuchElementException {
        EntityManager entityManager = examDao.getEntityManager();
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        if (teacherId == null) {
            Root<Examination> root = query.from(Examination.class);
            List<Predicate> predicates = new ArrayList<>(6);
            if (area != null)
                predicates.add(builder.equal(root.get("area"), area));
            if (classroomNo != null)
                predicates.add(builder.equal(root.get("classroomNo"), classroomNo));
            if (beg != null)
                predicates.add(builder.greaterThanOrEqualTo(root.get("examDate"), beg));
            if (end != null)
                predicates.add(builder.lessThanOrEqualTo(root.get("examDate"), end));
            if (begNo != null)
                predicates.add(builder.ge(root.get("beginNo"), begNo == null ? 1 : begNo));
            if (endNo != null)
                predicates.add(builder.le(root.get("endNo"), endNo == null ? 12 : endNo));
            query.select(builder.count(root.get("id"))).where(predicates.toArray(new Predicate[0]));
            return entityManager.createQuery(query).getSingleResult();
        }
        Root<SuperviseRecord> root = query.from(SuperviseRecord.class);
        List<Predicate> predicates = new ArrayList<>(7);
        predicates.add(builder.equal(root.get("supervisor").get("id"), teacherId));
        if (area != null)
            predicates.add(builder.equal(root.get("examination").get("area"), area));
        if (classroomNo != null)
            predicates.add(builder.equal(root.get("examination").get("classroomNo"), classroomNo));
        if (beg != null)
            predicates.add(builder.greaterThanOrEqualTo(root.get("examDate"), beg));
        if (end != null)
            predicates.add(builder.lessThanOrEqualTo(root.get("examDate"), end));
        if (begNo != null)
            predicates.add(builder.ge(root.get("examination").get("beginNo"), begNo == null ? 1 : begNo));
        if (endNo != null)
            predicates.add(builder.le(root.get("examination").get("endNo"), endNo == null ? 12 : endNo));
        query.select(builder.count(root.get("examination").get("id"))).distinct(true).where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(query).getSingleResult();
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<Examination> find(int begIndex, int count, Long teacherId, String area, String classroomNo, LocalDate beg, LocalDate end, Short begNo, Short endNo) throws NoSuchElementException {
        EntityManager entityManager = examDao.getEntityManager();
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Examination> query = builder.createQuery(Examination.class);
        if (teacherId == null) {
            Root<Examination> root = query.from(Examination.class);
            List<Predicate> predicates = new ArrayList<>(6);
            if (area != null)
                predicates.add(builder.equal(root.get("area"), area));
            if (classroomNo != null)
                predicates.add(builder.equal(root.get("classroomNo"), classroomNo));
            if (beg != null)
                predicates.add(builder.greaterThanOrEqualTo(root.get("examDate"), beg));
            if (end != null)
                predicates.add(builder.lessThanOrEqualTo(root.get("examDate"), end));
            if (begNo != null)
                predicates.add(builder.ge(root.get("beginNo"), begNo == null ? 1 : begNo));
            if (endNo != null)
                predicates.add(builder.le(root.get("endNo"), endNo == null ? 12 : endNo));
            query.select(root).where(predicates.toArray(new Predicate[0]));
            return entityManager.createQuery(query).setFirstResult(begIndex).setMaxResults(count).getResultList();
        }
        Root<SuperviseRecord> root = query.from(SuperviseRecord.class);
        List<Predicate> predicates = new ArrayList<>(7);
        predicates.add(builder.equal(root.get("supervisor").get("id"), teacherId));
        if (area != null)
            predicates.add(builder.equal(root.get("examination").get("area"), area));
        if (classroomNo != null)
            predicates.add(builder.equal(root.get("examination").get("classroomNo"), classroomNo));
        if (beg != null)
            predicates.add(builder.greaterThanOrEqualTo(root.get("examDate"), beg));
        if (end != null)
            predicates.add(builder.lessThanOrEqualTo(root.get("examDate"), end));
        if (begNo != null)
            predicates.add(builder.ge(root.get("examination").get("beginNo"), begNo == null ? 1 : begNo));
        if (endNo != null)
            predicates.add(builder.le(root.get("examination").get("endNo"), endNo == null ? 12 : endNo));
        query.select(root.get("examination")).distinct(true).where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(query).setFirstResult(begIndex).setMaxResults(count).getResultList();
    }

    @Override
    public void add(Examination examination) throws NoSuchElementException, IllegalArgumentException {
        CriteriaBuilder builder = examDao.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<Examination> root = query.from(Examination.class);
        query.select(builder.count(root.get("id")));
        query.where(builder.equal(root.get("examDate"), examination.getExamDate())
                , builder.equal(root.get("area"), examination.getArea())
                , builder.equal(root.get("classroomNo"), examination.getClassroomNo())
                , builder.between(root.get("beginNo"), examination.getBeginNo(), examination.getEndNo()));
        if (examDao.getEntityManager().createQuery(query).getSingleResult() != 0)
            throw new IllegalArgumentException("There is conflict in examination arrange.");
        try {
            examDao.create(examination);
        } catch (PersistenceException e) {
            throw new IllegalArgumentException("The exam to be added should not has ID.", e);
        }
    }

    @Override
    public void modify(long id, Examination examination) throws NoSuchElementException, IllegalArgumentException {
        CriteriaBuilder builder = examDao.getEntityManager().getCriteriaBuilder();
        CriteriaUpdate<Examination> update = builder.createCriteriaUpdate(Examination.class);
        Root<Examination> root = update.from(Examination.class);
        update.where(builder.equal(root.get("id"), id));
        update.set("examDate", examination.getExamDate());
        update.set("beginNo", examination.getBeginNo());
        update.set("endNo", examination.getEndNo());
        update.set("course", examination.getCourse());
        update.set("area", examination.getArea());
        update.set("classroomNo", examination.getClassroomNo());
        assert 1 == examDao.getEntityManager().createQuery(update).executeUpdate();
    }

    @Override
    public void addSupervisor(long id, long teacherId) throws NoSuchElementException {
        Examination exam = find(id);
        if (exam == null)
            throw new NoSuchElementException("Found no exam with the ID: " + id);
        Teacher teacher = userDao.read(teacherId);
        if (teacher == null)
            throw new NoSuchElementException("Found no user with the ID: " + teacherId);
        SuperviseRecord record = new SuperviseRecord();
        record.setExamination(exam);
        record.setSupervisor(teacher);
        superviseDao.create(record);
    }

    @Override
    public void removeSupervisor(long id, long teacherId) throws NoSuchElementException {
        if (find(id) == null)
            throw new NoSuchElementException("Found no exam with the ID: " + id);
        if (userDao.read(teacherId) == null)
            throw new NoSuchElementException("Found no user with the ID: " + teacherId);
        if (superviseDao.getEntityManager().createQuery("delete from SuperviseRecord s where s.examination.id=:id and s.supervisor.id=:number").executeUpdate() != 1)
            throw new NoSuchElementException("Found no supervise record with the given exam ID and supervisor ID.");
    }

    @Override
    public List<Teacher> findSupervisors(long id) {
        Examination examination = find(id);
        if (examination == null)
            throw new NoSuchElementException("Found no exam with the ID: " + id);
        return examination.getSuperviseRecords().stream().map(SuperviseRecord::getSupervisor).collect(Collectors.toList());
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<Map<String, String>> findClassrooms(int beginIndex, int count) {
        CriteriaBuilder builder = examDao.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Tuple> query = builder.createQuery(Tuple.class);
        query.distinct(true);
        Root<Examination> root = query.from(Examination.class);
        query.multiselect(root.<String>get("area"), root.<String>get("classroomNo"));
        return userDao.getEntityManager().createQuery(query).setFirstResult(beginIndex).setMaxResults(count).getResultList().stream().map(tuple -> Map.of("area", String.valueOf(tuple.get(0)), "classroomNo", String.valueOf(tuple.get(1)))).collect(Collectors.toList());
    }

    @Override
    public void remove(long id) throws NoSuchElementException {
        if (find(id) == null)
            throw new NoSuchElementException("Found no exam with the ID: " + id);
        examDao.delete(id);
    }
}
