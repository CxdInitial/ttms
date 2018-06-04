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

    private Long getActionUserId(ProceedingJoinPoint pjp) {
        Parameter[] parameters = ((MethodSignature) pjp.getSignature()).getMethod().getParameters();
        Parameter action = Arrays.stream(parameters).filter(parameter -> parameter.getType().equals(long.class) && parameter.getAnnotation(PathVariable.class) != null).findFirst().orElse(null);
        Long current = null;
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i] == action && pjp.getArgs()[i] instanceof Long) {
                current = (Long) pjp.getArgs()[i];
                break;
            }
        }
        return current;
    }

    @Around("@within(org.springframework.stereotype.Controller)&&@annotation(notSelf)")
    Object self(ProceedingJoinPoint pjp, NotSelf notSelf) throws Throwable {
        Long action = getActionUserId(pjp);
        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession(true);
        if (action != null) {
            Long loginId = (Long) session.getAttribute("user");
            if (loginId != null && !loginId.equals(action))
                return pjp.proceed(pjp.getArgs());
        }
        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse().setStatus(HttpStatus.UNAUTHORIZED.value());
        return defaultValue.get(((MethodSignature) pjp.getSignature()).getReturnType());
    }

    @Around("@within(org.springframework.stereotype.Controller)&&@annotation(notSelfAndAdmin)")
    Object self(ProceedingJoinPoint pjp, NotSelfAndAdmin notSelfAndAdmin) throws Throwable {
        Long action = getActionUserId(pjp);
        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession(true);
        if (action != null) {
            Long loginId = (Long) session.getAttribute("user");
            if (loginId != null && !loginId.equals(action) && userService.find(loginId).getManager())
                return pjp.proceed(pjp.getArgs());
        }
        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse().setStatus(HttpStatus.UNAUTHORIZED.value());
        return defaultValue.get(((MethodSignature) pjp.getSignature()).getReturnType());
    }

    @Around("@within(org.springframework.stereotype.Controller)&&@annotation(self)")
    Object self(ProceedingJoinPoint pjp, Self self) throws Throwable {
        Long action = getActionUserId(pjp);
        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession(true);
        if (action != null) {
            Long loginId = (Long) session.getAttribute("user");
            if (loginId != null && loginId.equals(action))
                return pjp.proceed(pjp.getArgs());
        }
        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse().setStatus(HttpStatus.UNAUTHORIZED.value());
        return defaultValue.get(((MethodSignature) pjp.getSignature()).getReturnType());
    }

    @Around("@within(org.springframework.stereotype.Controller)&&@annotation(selfOrAdmin)")
    Object selfOrAdmin(ProceedingJoinPoint pjp, SelfOrAdmin selfOrAdmin) throws Throwable {
        Long action = getActionUserId(pjp);
        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession(true);
        if (action != null) {
            Long loginId = (Long) session.getAttribute("user");
            if ((loginId != null && loginId.equals(action)) || userService.find(loginId).getManager())
                return pjp.proceed(pjp.getArgs());
        }
        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse().setStatus(HttpStatus.UNAUTHORIZED.value());
        return defaultValue.get(((MethodSignature) pjp.getSignature()).getReturnType());
    }

    @Around("@within(org.springframework.stereotype.Controller)&&(!@annotation(me.cxd.web.authentic.Self))&&(!@annotation(me.cxd.web.authentic.NotSelf))&&(!@annotation(me.cxd.web.authentic.SelfOrAdmin))&&(!@annotation(me.cxd.web.authentic.NotSelfAndAdmin))&&(@within(level)||@annotation(level))")
    Object level(ProceedingJoinPoint pjp, RequiredLevel level) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        if (level == null)
            level = pjp.getTarget().getClass().getAnnotation(RequiredLevel.class);
        boolean exec = false;
        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession(true);
        Long loginId = (Long) session.getAttribute("user");
        if (level.value() == RequiredLevel.Level.NOBODY)
            exec = true;
        else if (loginId != null) {
            Teacher teacher = userService.find(loginId);
            if (teacher != null)
                if (teacher.getManager())
                    exec = true;
                else if (level.value() == RequiredLevel.Level.TEACHER)
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
