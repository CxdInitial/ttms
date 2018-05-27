package me.cxd.web.authentic.listener;

import me.cxd.web.authentic.OnlineList;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.Optional;

@WebListener
public class ChangeOnlineList implements HttpSessionAttributeListener, HttpSessionListener {
    private OnlineList getOnlineList(HttpSessionEvent event) {
        Object list = event.getSession().getServletContext().getAttribute("onlineList");
        if (list instanceof OnlineList)
            return (OnlineList) list;
        return null;
    }

    private Long getUser(HttpSessionEvent event) {
        Object user = event.getSession().getAttribute("user");
        if (user instanceof Long)
            return (Long) user;
        return null;
    }

    private void online(HttpSessionEvent event) {
        Long user = getUser(event);
        OnlineList list = getOnlineList(event);
        if (list != null && user != null)
            list.online(user);
    }

    private void offline(HttpSessionEvent event) {
        Long user = getUser(event);
        OnlineList list = getOnlineList(event);
        if (list != null && user != null)
            list.offline(user);
    }

    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        online(event);
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent event) {
        if (event.getName().equals("user") && event.getValue() instanceof Long)
            Optional.of(getOnlineList(event)).ifPresent(list -> list.offline((Long) event.getValue()));
        online(event);
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent event) {
        offline(event);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        offline(event);
    }
}
