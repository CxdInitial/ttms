package me.cxd.web.controller;

import me.cxd.bean.Teacher;
import me.cxd.service.LoginValidator;
import me.cxd.service.UserService;
import me.cxd.util.FieldList;
import me.cxd.web.authentic.NotSelf;
import me.cxd.web.authentic.OnlineList;
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
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
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

    private void logout(HttpSession session) {
        session.removeAttribute("user");
    }

    @Autowired
    public User(@Qualifier("additionalLoginValidator") LoginValidator loginValidator, UserService userService, FieldList<Teacher> userFieldNameList) {
        this.jwcLoginValidator = loginValidator;
        this.userService = userService;
        this.userFieldNameList = userFieldNameList;
    }

    @RequiredLevel(RequiredLevel.Level.NOBODY)
    @PostMapping("/authentication")
    void login(@Validated Teacher user, BindingResult bindingResult, HttpSession session, HttpServletResponse response) {
        if (bindingResult.hasFieldErrors("number") || bindingResult.hasFieldErrors("password")) {
            response.setStatus(HttpStatus.BAD_REQUEST.value()); //校验错误
            return;
        }
        if (userService.isValidUser(user.getNumber(), user.getPassword()))
            response.setStatus(HttpStatus.CREATED.value()); //成功
        else if (jwcLoginValidator.isValidUser(user.getNumber(), user.getPassword())) {
            response.setStatus(HttpStatus.NOT_FOUND.value()); //未注册
            if (userService.find(user.getNumber()) != null)
                response.setStatus(HttpStatus.CREATED.value()); //成功
        } else
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value()); //密码错误
    }

    @RequiredLevel(RequiredLevel.Level.TEACHER)
    @DeleteMapping("/authentication")
    void logout(HttpSession session, HttpServletResponse response) {
        logout(session);
        response.setStatus(HttpStatus.NO_CONTENT.value()); //退出成功
    }

    @PostMapping("/user")
    void register(@Validated Teacher teacher, HttpServletResponse response) {
        if (!userService.register(teacher))
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value()); //注册时错误（学工号冲突、手机号冲突）
        else
            response.setStatus(HttpStatus.CREATED.value()); //注册成功
    }

    @Self
    @RequiredLevel(RequiredLevel.Level.TEACHER)
    @PatchMapping("/user/{number}")
    void update(@PathVariable long number, @Validated Teacher teacher, BindingResult result, HttpSession session, HttpServletResponse response, @RequestParam Map<String, Object> map) {
        Set<String> field = userFieldNameList.getFields().stream().filter(f -> f.getAnnotation(Null.class) == null && !f.getName().equals("number")).map(Field::getName).collect(Collectors.toSet());
        if (!map.entrySet().stream().allMatch(pair -> field.contains(pair.getKey()) && !result.hasFieldErrors(pair.getKey())) || map.isEmpty()) {
            response.setStatus(HttpStatus.BAD_REQUEST.value()); //数据错误
            return;
        }
        if (userService.update(number, map))
            response.setStatus(HttpStatus.CREATED.value()); //更新成功
        else
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value()); //更新时错误（学工号冲突、手机号冲突）
        if (map.containsKey("password"))
            logout(session);
    }

    @DeleteMapping("/user/{number}")
    @NotSelf
    void remove(@PathVariable long number, HttpServletResponse response, HttpSession session) {
        OnlineList onlineList = (OnlineList) session.getServletContext().getAttribute("onlineList");
        onlineList.offline(number);
        userService.remove(number);
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }

    @GetMapping("/user/{number}")
    @RequiredLevel(RequiredLevel.Level.TEACHER)
    @ResponseBody
    Map<String, ?> get(@PathVariable long number, HttpServletResponse response) {
        Teacher user = userService.find(number);
        if (user != null) {
            response.setStatus(HttpStatus.OK.value()); //找到了正确的用户
            return Map.of("user", user);
        }
        throw new NoSuchElementException();
    }

    @GetMapping("/user")
    @ResponseBody
    Map<String, ?> get(@RequestParam(defaultValue = "0") long beginIndex, @RequestParam(defaultValue = "50") long count, @RequestParam(defaultValue = "number") String orderBy, HttpServletResponse response) {
        UserService.Order order;
        try {
            order = UserService.Order.valueOf(orderBy.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NoSuchElementException();
        }
        if (beginIndex < 0 || count <= 0)
            throw new NoSuchElementException();
        List<Teacher> list = userService.find(order, beginIndex, count);
        if (list.isEmpty())
            throw new NoSuchElementException();
        response.setStatus(HttpStatus.OK.value());
        return Collections.singletonMap("users", list);
    }
}
