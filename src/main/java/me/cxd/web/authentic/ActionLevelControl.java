package me.cxd.web.authentic;

import me.cxd.bean.Teacher;
import me.cxd.service.UserService;
import me.cxd.util.DefaultValue;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.lang.reflect.Parameter;
import java.util.Arrays;

@Aspect
@Component
public class ActionLevelControl {
    private final UserService userService;
    private final DefaultValue defaultValue;

    @Autowired
    public ActionLevelControl(UserService userService, DefaultValue defaultValue) {
        this.userService = userService;
        this.defaultValue = defaultValue;
    }

    @Autowired
    private long getLoginUserNo(OnlineList list, Object user) {
        if (user instanceof Long) {
            if (list.isOnline((Long) user))
                return (long) user;
        }
        return -1;
    }

    @Around("@within(org.springframework.stereotype.Controller)&&@annotation(self)")
    Object self(ProceedingJoinPoint pjp, Self self) throws Throwable {
        Parameter[] parameters = ((MethodSignature) pjp.getSignature()).getMethod().getParameters();
        Parameter action = Arrays.stream(parameters).filter(parameter -> parameter.getType().equals(long.class) && parameter.getAnnotation(PathVariable.class) != null && parameter.getAnnotation(PathVariable.class).value().equalsIgnoreCase("number")).findFirst().orElse(null);
        long current = -1;
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i] == action && pjp.getArgs()[i] instanceof Long) {
                current = (Long) pjp.getArgs()[i];
                break;
            }
        }
        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession(true);
        if (current != -1 && session.getServletContext().getAttribute("onlineList") instanceof OnlineList && getLoginUserNo((OnlineList) session.getServletContext().getAttribute("onlineList"), session.getAttribute("user")) == current)
            return pjp.proceed(pjp.getArgs());
        else {
            ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse().setStatus(HttpStatus.UNAUTHORIZED.value());
            return defaultValue.get(((MethodSignature) pjp.getSignature()).getReturnType());
        }
    }

    @Around("@within(org.springframework.stereotype.Controller)&&@annotation(level)||@within(level)")
    Object level(ProceedingJoinPoint pjp, RequiredLevel level) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        if (level == null)
            level = methodSignature.getMethod().getAnnotation(RequiredLevel.class);

        boolean exec = false;
        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession(true);
        Object list = session.getServletContext().getAttribute("onlineList");
        long loginNo = -1;
        if (list instanceof OnlineList)
            loginNo = getLoginUserNo((OnlineList) list, session.getAttribute("user"));
        if (level.allow() == RequiredLevel.Level.NOBODY)
            exec = true;
        else if (loginNo != -1) {
            Teacher teacher = userService.find(loginNo);
            if (teacher != null)
                if (teacher.getAdmin())
                    exec = true;
                else if (level.allow() == RequiredLevel.Level.TEACHER)
                    exec = true;
        }
        if (exec)
            return pjp.proceed(pjp.getArgs());
        else {
            ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse().setStatus(HttpStatus.UNAUTHORIZED.value());
            return defaultValue.get(methodSignature.getReturnType());
        }
    }
}
