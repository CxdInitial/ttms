package me.cxd.service;

public interface LoginValidator {
    boolean isValidUser(long number, String password);

    default boolean isValidUser(String number, String password) {
        return this.isValidUser(Long.valueOf(number), password);
    }
}
