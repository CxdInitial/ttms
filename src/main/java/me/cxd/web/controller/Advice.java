package me.cxd.web.controller;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.NoSuchElementException;

@ControllerAdvice
public class Advice {
    @ExceptionHandler({BindException.class, ConstraintViolationException.class, TypeMismatchException.class, NumberFormatException.class})
    void invalidData(HttpServletResponse response, HandlerMethod method) {
        if (method.hasMethodAnnotation(GetMapping.class) || method.hasMethodAnnotation(DeleteMapping.class))
            response.setStatus(HttpStatus.NOT_FOUND.value());
        else
            response.setStatus(HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(NoSuchElementException.class)
    void notFound(HttpServletResponse response) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler({IllegalArgumentException.class,IOException.class})
    void illegalParam(HttpServletResponse response) {
        response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }
}
