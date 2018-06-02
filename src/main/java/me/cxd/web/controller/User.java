package me.cxd.web.controller;

import me.cxd.bean.Teacher;
import me.cxd.service.LoginValidator;
import me.cxd.service.UserService;
import me.cxd.util.FieldList;
import me.cxd.web.authentic.NotSelf;
import me.cxd.web.authentic.RequiredLevel;
import me.cxd.web.authentic.Self;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.Null;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://127.0.0.1:8010")
@Controller
@Valid
@RequiredLevel(RequiredLevel.Level.ADMIN)
public class User {
    private final LoginValidator jwcLoginValidator;
    private final UserService userService;
    private final FieldList<Teacher> userFieldNameList;

    @Autowired
    public User(@Qualifier("additionalLoginValidator") LoginValidator loginValidator, UserService userService, FieldList<Teacher> userFieldNameList) {
        this.jwcLoginValidator = loginValidator;
        this.userService = userService;
        this.userFieldNameList = userFieldNameList;
    }

    @RequiredLevel(RequiredLevel.Level.NOBODY)
    @PostMapping("/authentication")
    void login(@Validated Teacher user, BindingResult bindingResult, HttpSession session, HttpServletResponse response) {
        if (bindingResult.hasFieldErrors("teacherNo") || bindingResult.hasFieldErrors("loginPassword")) {
            response.setStatus(HttpStatus.BAD_REQUEST.value()); //校验错误
            return;
        }
        if (userService.isValidUser(user.getTeacherNo(), user.getLoginPassword()))
            response.setStatus(HttpStatus.CREATED.value()); //成功
        else if (jwcLoginValidator.isValidUser(user.getTeacherNo(), user.getLoginPassword())) {
            response.setStatus(HttpStatus.NOT_FOUND.value()); //未注册
            if (userService.findByNo(user.getTeacherNo()) != null)
                response.setStatus(HttpStatus.CREATED.value()); //成功
        } else
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value()); //密码错误
        if (response.getStatus() == HttpStatus.CREATED.value())
            session.setAttribute("user", userService.findByNo(user.getTeacherNo()).getId());
    }

    @RequiredLevel(RequiredLevel.Level.TEACHER)
    @DeleteMapping("/authentication")
    void logout(HttpSession session, HttpServletResponse response) {
        session.removeAttribute("user");
        response.setStatus(HttpStatus.NO_CONTENT.value()); //退出成功
    }

    @PostMapping("/user")
    void register(@Validated Teacher teacher, HttpServletResponse response) {
        userService.register(teacher);
        response.setStatus(HttpStatus.CREATED.value()); //注册成功
    }

    @Self
    @RequiredLevel(RequiredLevel.Level.TEACHER)
    @PatchMapping("/user/{id}")
    void update(@PathVariable long id, @Validated Teacher teacher, BindingResult result, HttpSession session, HttpServletResponse response, @RequestParam Map<String, Object> map) {
        Set<String> fields = userFieldNameList.getFields().stream().filter(f -> f.getAnnotation(Null.class) == null).map(Field::getName).collect(Collectors.toSet());
        if (!map.entrySet().stream().allMatch(pair -> fields.contains(pair.getKey()) && !result.hasFieldErrors(pair.getKey())) || map.isEmpty()) {
            response.setStatus(HttpStatus.BAD_REQUEST.value()); //数据错误
            return;
        }
        if (map.containsKey("loginPassword") || map.containsKey("teacherNo"))
            session.removeAttribute("user");
        userService.update(id, map);
        response.setStatus(HttpStatus.CREATED.value()); //更新成功
    }

    @DeleteMapping("/user/{id}")
    @NotSelf
    void remove(@PathVariable long id, HttpServletResponse response, HttpSession session) {
        userService.remove(id);
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }

    @GetMapping("/user/{id}")
    @RequiredLevel(RequiredLevel.Level.TEACHER)
    @ResponseBody
    Map<String, ?> get(@PathVariable long id, HttpServletResponse response) {
        Teacher user = userService.find(id);
        user.setSuperviseRecords(null);
        user.setTasks(null);
        user.setUpdateTime(null);
        user.setInsertTime(null);
        if (user != null) {
            response.setStatus(HttpStatus.OK.value()); //找到了正确的用户
            return Map.of("user", user);
        }
        throw new NoSuchElementException();
    }

    @GetMapping("/user")
    @ResponseBody
    Map<String, ?> get(@RequestParam(defaultValue = "0") int beginIndex, @RequestParam(defaultValue = "50") int count, @RequestParam(defaultValue = "teacherNo") String orderBy, @RequestParam(defaultValue = "true") boolean asc, HttpServletResponse response) {
        UserService.Order order;
        order = Arrays.stream(UserService.Order.values()).filter(i -> i.value().equals(orderBy)).findFirst().get();
        if (beginIndex < 0 || count <= 0)
            throw new NoSuchElementException();
        List<Teacher> list = userService.find(order, beginIndex, count, asc);
        if (list.isEmpty())
            throw new NoSuchElementException();
        response.setStatus(HttpStatus.OK.value());
        list.forEach(user -> {
            user.setSuperviseRecords(null);
            user.setTasks(null);
            user.setUpdateTime(null);
            user.setInsertTime(null);
        });
        return Collections.singletonMap("users", list);
    }

    @GetMapping("/count/user")
    @ResponseBody
    Map<String, ?> count(HttpServletResponse response) {
        response.setStatus(HttpStatus.OK.value());
        return Map.of("count", userService.countUser());
    }
}
