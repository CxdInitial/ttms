package me.cxd.service;

import me.cxd.bean.Teacher;

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

    @Override
    default boolean isValidUser(long number, String password) {
        return true;
    }

    default boolean register(Teacher teacher) {
        return false;
    }

    default boolean update(long number, Map<String, ?> fieldValues) {
        return true;
    }

    default void remove(long number) {
    }

    default Teacher find(long number) {
        return null;
    }

    default List<Teacher> find(Order order, long begIndex, long count) {
        return List.of();
    }
}
