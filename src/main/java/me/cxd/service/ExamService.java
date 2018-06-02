package me.cxd.service;

import me.cxd.bean.Examination;

import java.time.LocalDate;
import java.util.List;
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
     * @return count of all exams
     */
    default long count() {
        return count(null, null, null, null, null, null);
    }

    /**
     * Find the count of matched exams.
     * <p>
     * Whoever call this method should make sure parameters are validated， or prepare to handle exceptions may be thrown(Not only {@link NoSuchElementException},
     * but also exceptions associated with data access), this method would not do any validation job.
     *
     * @param number:      the supervisor's number
     * @param classroomId: the ID of classroom where exam would take place
     * @param beg,end:     the exam's between begin date and end date(inclusive)
     * @param begNo:       the exam's begin lesson number
     * @param endNo:       the exam's end lesson number
     * @return the count of all exams match the conditions
     * @throws NoSuchElementException:when number or classroomId has no matched entity
     */
    long count(Long number, Long classroomId, LocalDate beg, LocalDate end, Short begNo, Short endNo) throws NoSuchElementException;

    /**
     * Find the matched exams.
     * <p>
     * Whoever call this method should make sure parameters are validated， or prepare to handle exceptions may be thrown(Not only {@link NoSuchElementException},
     * but also exceptions associated with data access), this method would not do any validation job.
     *
     * @param begIndex:    the begin index of retrieve exams
     * @param count:       the count of retrieve exams
     * @param number:      the supervisor's number
     * @param classroomId: the ID of classroom where exam would take place
     * @param beg:         the exam's between begin date and end date(inclusive)
     * @param end:         the exam's between begin date and end date(inclusive)
     * @param begNo:       the exam's begin lesson number
     * @param endNo:       the exam's end lesson number
     * @return all exams match the conditions
     * @throws NoSuchElementException:when number or classroomId has no matched entity
     */
    abstract List<Examination> find(long begIndex, long count, Long number, Long classroomId, LocalDate beg, LocalDate end, Short begNo, Short endNo) throws NoSuchElementException;

    /**
     * Add a new exam.
     *
     * @param examination: the exam to be added
     * @param classroomId: the ID of classroom where exam would take place
     * @return the ID of newly added exam
     * @throws NoSuchElementException:   when classroomId has no matched entity
     * @throws IllegalArgumentException: when the exam's classroom has already been occupied
     */
    long add(Examination examination, long classroomId) throws NoSuchElementException, IllegalArgumentException;

    /**
     * Use the given complete exam to update the old one.
     *
     * @param id:          the exam's ID to be modified
     * @param examination: the whole content to be updated
     * @param classroomId: the ID of classroom where exam would take place
     * @throws NoSuchElementException:   when id or classroomId has no matched entity
     * @throws IllegalArgumentException: when the exam's classroom has already been occupied
     */
    void modify(long id, Examination examination, long classroomId) throws NoSuchElementException, IllegalArgumentException;

    /**
     * Add a supervisor for an exam.
     * <p>
     * A supervisor can be assign several exams who maybe take place simultaneously. Because the supervisor is a teacher, he/she can assign his/her job to his/her own students.
     *
     * @param id:     the exam's ID to be modified
     * @param number: the supervisor's number
     * @throws NoSuchElementException: when id or number has no matched entity
     */
    void addSupervisor(long id, long number) throws NoSuchElementException;

    /**
     * Remove a supervisor for an exam.
     *
     * @param id:     the exam's ID to be modified
     * @param number: the supervisor's number
     * @throws NoSuchElementException: when id or number has no matched entity
     */
    void removeSupervisor(long id, long number) throws NoSuchElementException;

    /**
     * Remove an exam.
     *
     * @param id: the exam's ID to be removed
     * @throws NoSuchElementException: when id has no matched entity
     */
    void remove(long id) throws NoSuchElementException;
}
