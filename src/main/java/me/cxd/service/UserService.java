package me.cxd.service;

import me.cxd.bean.Teacher;
import org.hibernate.mapping.Backref;

import java.util.List;
import java.util.Map;

public interface UserService extends LoginValidator {
    enum Order {
        NUMBER("number"), TITLE("title"), BUSYNESS("busyness");

        private final String val;

        Order(String val) {
            this.val = val;
        }

        public String value() {
            return val;
        }
    }

    default boolean isValidUser(long number, String password){
        Teacher user = find(number);
        return user != null && user.getPassword().equals(password);
    }

    boolean register(Teacher teacher);

    boolean update(long number, Map<String, ?> fieldValues);

    void remove(long number);

    Teacher find(long number);

    List<Teacher> find(Order order, long begIndex, long count);

    default List<Teacher> find(Order order, long count) {
        return find(order, 0, count);
    }
}
