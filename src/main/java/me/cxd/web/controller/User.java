package me.cxd.web.controller;

import me.cxd.bean.Teacher;
import me.cxd.service.LoginValidator;
import me.cxd.service.UserService;
import me.cxd.util.FieldList;
import me.cxd.web.authentic.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.*;

@RestController
@Validated
@RequiredLevel(RequiredLevel.Level.ADMIN)
public class User {
    private final LoginValidator jwcLoginValidator;
    private final UserService userService;

    @Autowired
    public User(@Qualifier("additionalLoginValidator") LoginValidator loginValidator, UserService userService) {
        this.jwcLoginValidator = loginValidator;
        this.userService = userService;
    }

    @RequiredLevel(RequiredLevel.Level.NOBODY)
    @PostMapping("/authentication")
    Map<String, Long> login(@RequestParam @Min(value = 1000000000L) long teacherNo, @RequestParam @Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,}$") String loginPassword, HttpSession session, HttpServletResponse response) {
        if (userService.isValidUser(teacherNo, loginPassword))
            response.setStatus(HttpStatus.CREATED.value()); //成功
        else if (jwcLoginValidator.isValidUser(teacherNo, loginPassword)) {
            response.setStatus(HttpStatus.NOT_FOUND.value()); //未注册
            if (userService.findByNo(teacherNo) != null)
                response.setStatus(HttpStatus.CREATED.value()); //成功
        } else
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value()); //密码错误
        if (response.getStatus() == HttpStatus.CREATED.value()) {
            long id = userService.findByNo(teacherNo).getId();
            session.setAttribute("user", id);
            return Map.of("id", id);
        }
        return null;
    }

    @RequiredLevel(RequiredLevel.Level.TEACHER)
    @DeleteMapping("/authentication")
    void logout(HttpSession session, HttpServletResponse response) {
        session.removeAttribute("user");
        response.setStatus(HttpStatus.NO_CONTENT.value()); //退出成功
    }

    @PostMapping("/user")
    void register(@Validated Teacher teacher, HttpServletResponse response) {
        if (teacher.getId() != 0)
            throw new ConstraintViolationException(null);
        userService.register(teacher);
        response.setStatus(HttpStatus.CREATED.value()); //注册成功
    }

    @Self
    @PatchMapping("/user/{id}")
    void update(@PathVariable long id
            , @RequestParam(required = false) @Pattern(regexp = "^[\\u2E80-\\u9FFF]{2,5}$") String teacherName
            , @RequestParam(required = false) @Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,}$") String loginPassword
            , @RequestParam(required = false) Boolean male
            , @RequestParam(required = false) @Pattern(regexp = "^((13[0-9])|(147,145)|(15(0,1,2,3,5,6,7,8,9))|(166)|(17[6-8])|(18[0-9])|(19[8-9]))[0-9]{8}$") String phone
            , @RequestParam(required = false) String intro
            , HttpSession session, HttpServletResponse response) {
        Map<String, Object> fields = new HashMap<>(5);
        if (teacherName != null)
            fields.put("teacherName", teacherName);
        if (loginPassword != null) {
            fields.put("loginPassword", loginPassword);
            session.removeAttribute("user");
        }
        if (male != null)
            fields.put("male", male);
        if (phone != null)
            fields.put("phone", phone);
        if (intro != null) {
            if (intro.trim().isEmpty())
                throw new ConstraintViolationException(null);
            fields.put("intro", intro);
        }
        if (fields.isEmpty())
            throw new ConstraintViolationException(null);
        userService.update(id, fields);
        response.setStatus(HttpStatus.CREATED.value()); //更新成功
    }

    @RequiredLevel(RequiredLevel.Level.ADMIN)
    @PutMapping("/user/{id}")
    void update(@PathVariable long id, @Validated Teacher teacher, HttpSession session, HttpServletResponse response) {
        userService.update(id, teacher);
        session.removeAttribute("user");
        response.setStatus(HttpStatus.CREATED.value());
    }

    @DeleteMapping("/user/{id}")
    @NotSelfAndAdmin
    void remove(@PathVariable long id, HttpServletResponse response, HttpSession session) {
        userService.remove(id);
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }

    @GetMapping("/user/{id}")
    @RequiredLevel(RequiredLevel.Level.TEACHER)
    Map<String, Teacher> get(@PathVariable long id, HttpServletResponse response) {
        Teacher user = userService.find(id);
        user.setSuperviseRecords(null);
        user.setTasks(null);
        user.setRecords(null);
        user.setLoginPassword(null);
        user.setUpdateTime(null);
        user.setInsertTime(null);
        if (user != null) {
            response.setStatus(HttpStatus.OK.value()); //找到了正确的用户
            return Map.of("user", user);
        }
        throw new NoSuchElementException();
    }

    @GetMapping("/user")
    Map<String, List<Teacher>> get(@RequestParam(defaultValue = "0") @Min(0) int begIndex, @RequestParam(defaultValue = "50") @Min(1) int count, @RequestParam(defaultValue = "teacherNo") String orderBy, @RequestParam(defaultValue = "true") boolean asc, HttpServletResponse response) {
        UserService.Order order = Arrays.stream(UserService.Order.values()).filter(i -> i.value().equals(orderBy)).findFirst().orElse(null);
        if (order == null || begIndex < 0 || count <= 0)
            throw new NoSuchElementException();
        List<Teacher> list = userService.find(order, begIndex, count, asc);
        if (list.isEmpty())
            throw new NoSuchElementException();
        list.forEach(user -> {
            user.setSuperviseRecords(null);
            user.setTasks(null);
            user.setRecords(null);
            user.setLoginPassword(null);
        });
        return Collections.singletonMap("users", list);
    }

    @GetMapping("/count/user")
    Map<String, Long> count(HttpServletResponse response) {
        response.setStatus(HttpStatus.OK.value());
        return Map.of("count", userService.countUser());
    }
}
