package me.cxd.web.authentic.listener;

import javax.servlet.http.HttpSessionEvent;

interface UserAware {
    default Long getUser(HttpSessionEvent event) {
        Object user = event.getSession().getAttribute("user");
        if (user instanceof Long)
            return (Long) user;
        return null;
    }
}
