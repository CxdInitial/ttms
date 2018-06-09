package me.cxd.service;

import me.cxd.bean.Examination;
import me.cxd.bean.Teacher;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public interface ExamService {
    /**
     * Find the exam whose ID is {@param id}.
     *
     * @param id: the exam's ID
     * @return found exam or {@code null} if not found
     */
    Examination find(long id);

    /**
     * Find the count of matched exams.
     * <p>
     * Whoever call this method should make sure parameters are validated， or prepare to handle exceptions may be thrown(Not only {@link NoSuchElementException},
     * but also exceptions associated with data access), this method would not do any validation job.
     *
     * @param number:      the supervisor's number
     * @param area:        the building of classroom where exam would take place
     * @param classroomNo: the number of classroom where exam would take place
     * @param beg:         the exam's between begin date and end date(inclusive)
     * @param end:         the exam's between begin date and end date(inclusive)
     * @param begNo:       the exam's begin lesson number
     * @param endNo:       the exam's end lesson number
     * @return the count of all exams match the conditions
     * @throws NoSuchElementException:when number has no matched entity
     */
    long count(Long number, String area, String classroomNo, LocalDate beg, LocalDate end, Short begNo, Short endNo);

    /**
     * Find the matched exams.
     * <p>
     * Whoever call this method should make sure parameters are validated， or prepare to handle exceptions may be thrown(Not only {@link NoSuchElementException},
     * but also exceptions associated with data access), this method would not do any validation job.
     *
     * @param begIndex:    the begin index of retrieve exams
     * @param count:       the count of retrieve exams
     * @param number:      the supervisor's number
     * @param area:        the building of classroom where exam would take place
     * @param classroomNo: the number of classroom where exam would take place
     * @param beg:         the exam's between begin date and end date(inclusive)
     * @param end:         the exam's between begin date and end date(inclusive)
     * @param begNo:       the exam's begin lesson number
     * @param endNo:       the exam's end lesson number
     * @return all exams match the conditions
     * @throws NoSuchElementException:when number has no matched entity
     */
    List<Examination> find(int begIndex, int count, Long number, String area, String classroomNo, LocalDate beg, LocalDate end, Short begNo, Short endNo) throws NoSuchElementException;

    /**
     * Add a new exam.
     *
     * @param examination: the exam to be added
     * @throws IllegalArgumentException: when add the new exam, {@link javax.persistence.PersistenceException} happened.
     */
    void add(Examination examination) throws IllegalArgumentException;

    /**
     * Remove an exam.
     *
     * @param id: the exam's ID to be removed
     * @throws NoSuchElementException: when id has no matched entity
     */
    void remove(long id) throws NoSuchElementException;

    /**
     * Use the given complete exam to update the old one.
     *
     * @param id:          the exam's ID to be modified
     * @param examination: the whole content to be updated
     * @throws NoSuchElementException:   when id or classroomId has no matched entity
     * @throws IllegalArgumentException: when the exam's classroom has already been occupied
     */
    void modify(long id, Examination examination) throws NoSuchElementException, IllegalArgumentException;

    /**
     * Add a supervisor for an exam.
     * <p>
     * A supervisor can be assign several exams who maybe take place simultaneously. Because the supervisor is a teacher, he/she can assign his/her jobs to his/her own students separately.
     *
     * @param id:        the exam's ID to be modified
     * @param teacherId: the supervisor's id
     * @throws NoSuchElementException: when id or number has no matched entity
     */
    void addSupervisor(long id, long teacherId) throws NoSuchElementException;

    /**
     * Remove a supervisor for an exam.
     *
     * @param id:        the exam's ID to be modified
     * @param teacherId: the supervisor's id
     * @throws NoSuchElementException: when id or number has no matched entity
     */
    void removeSupervisor(long id, long teacherId) throws NoSuchElementException;

    /**
     * Find supervisors who will supervise the exam with the ID {@param id}.
     *
     * @param id: the exam's ID
     * @return a list of supervisor
     */
    List<Teacher> findSupervisors(long id);
}
