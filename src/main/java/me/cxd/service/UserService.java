package me.cxd.service;

import me.cxd.bean.Teacher;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public interface UserService extends LoginValidator {
    /**
     * Order used by {@link UserService#find(Order, int, int, boolean)} to order the user list.
     */
    enum Order {
        NUMBER("teacherNo"), TITLE("title"), BUSYNESS("busyness");

        private final String val;

        Order(String val) {
            this.val = val;
        }

        public String value() {
            return val;
        }
    }

    default boolean isValidUser(long no, String password) {
        Teacher user = findByNo(no);
        return user != null && user.getLoginPassword().equals(password);
    }

    /**
     * @param teacher: new teacher to be registered
     * @throws IllegalArgumentException: user's no or phone has already been registered
     */
    void register(Teacher teacher) throws IllegalArgumentException;

    /**
     * Update the teacher whose id is {@param id} with the given content {@param fieldValues}.
     * <p>
     * Whoever call this method should make sure {@param fieldValues} is validated， or prepare to handle exceptions may be thrown
     * (Not only {@link NoSuchElementException} {@link IllegalArgumentException}, but also exceptions associated with data access),
     * this method would not do any validation job.
     *
     * @param id:          teacher's id
     * @param fieldValues: pairs(field name and field value) to be updated
     * @throws NoSuchElementException:   when id has no matched teacher
     * @throws IllegalArgumentException: when the new phone or no has already been used
     */
    void update(long id, Map<String, Object> fieldValues) throws NoSuchElementException, IllegalArgumentException;

    void update(long id, Teacher teacher) throws NoSuchElementException, IllegalArgumentException;

    /**
     * Remove the teacher whose no is {@param no}.
     *
     * @param id: the id of the teacher to be removed
     * @throws NoSuchElementException: the id has no matcher teacher
     */
    void remove(long id) throws NoSuchElementException;

    /**
     * Find the teacher whose id is {@param id}.
     *
     * @param id: the id of the teacher to be found
     * @return found teacher
     */
    Teacher find(long id);

    /**
     * Find the teacher whose phone number is {@param phoneNumber}.
     *
     * @param phoneNumber: the phone number of the teacher to be found
     * @return found teacher
     */
    Teacher findByPhone(String phoneNumber);

    /**
     * Find the teacher whose teacher number is {@param teacherNo}.
     *
     * @param teacherNo: the teacher number of the teacher to be found
     * @return found teacher
     */
    Teacher findByNo(long teacherNo);

    /**
     * Find first {@param count} teachers from index {@param begIndex} to the end by order {@param order}.
     *
     * @param order:    sorting order
     * @param begIndex: begin index
     * @param count:    retrieve count
     * @return list of teacher
     */
    List<Teacher> find(Order order, int begIndex, int count, boolean asc);

    /**
     * @return the count of all users.
     */
    long countUser();
}
