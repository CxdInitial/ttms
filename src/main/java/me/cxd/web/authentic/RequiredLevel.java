package me.cxd.web.authentic;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiredLevel {
    enum Level {
        NOBODY, TEACHER, ADMIN
    }

    @AliasFor("allow")
    Level value() default Level.TEACHER;

    @AliasFor("value")
    Level allow() default Level.TEACHER;
}
