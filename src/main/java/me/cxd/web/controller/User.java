package me.cxd.web.controller;

import me.cxd.bean.Teacher;
import me.cxd.service.LoginValidator;
import me.cxd.service.UserService;
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
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://127.0.0.1:8010")
@Controller
@Valid
@RequiredLevel(allow = RequiredLevel.Level.ADMIN)
public class User {
    private final LoginValidator jwcLoginValidator;
    private final UserService userService;

    private static List<String> UserFieldNames() {
        return Arrays.stream(Teacher.class.getDeclaredFields()).map(Field::getName).collect(Collectors.toList());
    }

    @Autowired
    public User(@Qualifier("loginValidator") LoginValidator loginValidator, UserService userService) {
        this.jwcLoginValidator = loginValidator;
        this.userService = userService;
    }

    @RequiredLevel(allow = RequiredLevel.Level.NOBODY)
    @PostMapping("/authentication")
    @ResponseBody
    @ResponseStatus
    void login(@Validated Teacher user, BindingResult bindingResult, HttpSession session, HttpServletResponse response) {
        if (bindingResult.hasFieldErrors("number") || bindingResult.hasFieldErrors("password")) {
            response.setStatus(HttpStatus.BAD_REQUEST.value()); //校验错误
            return;
        }
        if (userService.isValidUser(user.getNumber(), user.getPassword()))
            response.setStatus(HttpStatus.CREATED.value()); //成功
        else if (jwcLoginValidator.isValidUser(user.getNumber(), user.getPassword()))
            response.setStatus(HttpStatus.NOT_FOUND.value()); //未注册
        else
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value()); //密码错误
    }

    @RequiredLevel(allow = RequiredLevel.Level.TEACHER)
    @DeleteMapping("/authentication")
    @ResponseBody
    void logout(HttpSession session, HttpServletResponse response) {
        session.removeAttribute("user");
        response.setStatus(HttpStatus.NO_CONTENT.value()); //退出成功
    }

    @PostMapping("/user")
    @ResponseBody
    void register(@Validated Teacher teacher, HttpServletResponse response) {
        if (!userService.register(teacher))
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value()); //注册时错误（学工号冲突、手机号冲突）
        else
            response.setStatus(HttpStatus.CREATED.value()); //注册成功
    }

    @Self
    @PatchMapping("/user/{number}")
    @ResponseBody
    void update(@PathVariable long number, @Validated Teacher teacher, BindingResult result, HttpSession session, HttpServletResponse response, @RequestParam Map<String, Object> map) {
        List<String> field = UserFieldNames();
        field.remove("number");
        if (!map.entrySet().stream().allMatch(pair -> field.contains(pair.getKey()) && !result.hasFieldErrors(pair.getKey())) || map.isEmpty()) {
            response.setStatus(HttpStatus.BAD_REQUEST.value()); //数据错误
            return;
        }
        if (userService.update(number, map))
            response.setStatus(HttpStatus.CREATED.value()); //更新成功
        else
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value()); //更新时错误（学工号冲突、手机号冲突）
        if (map.containsKey("password"))
            session.removeAttribute("user");
    }

    @DeleteMapping("/user/{number}")
    @ResponseBody
    void remove(@PathVariable long number, HttpServletResponse response, HttpSession session) {
        OnlineList onlineList = (OnlineList) session.getServletContext().getAttribute("user");
        onlineList.offline(number);
        userService.remove(number);
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }

    @GetMapping("/user/{number}")
    @ResponseBody
    Map<String, ?> get(@PathVariable long number, HttpServletResponse response) {
        Teacher user = userService.find(number);
        if (user != null) {
            response.setStatus(HttpStatus.OK.value()); //找到了正确的用户
            return Map.of("user", user);
        }
        throw new NoSuchElementException();
    }

    @GetMapping("/users")
    @ResponseBody
    Map<String, ?> get(@RequestParam(defaultValue = "0") @PositiveOrZero long beginIndex, @RequestParam @Positive long count, @RequestParam String orderBy) {
        UserService.Order order;
        try {
            order = UserService.Order.valueOf(orderBy.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NoSuchElementException();
        }
        List<Teacher> list = userService.find(order, beginIndex, count);
        if (list.isEmpty())
            throw new NoSuchElementException();
        return Collections.singletonMap("users", list);
    }
}
