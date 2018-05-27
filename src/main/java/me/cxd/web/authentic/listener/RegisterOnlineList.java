package me.cxd.web.authentic.listener;

import me.cxd.web.authentic.OnlineList;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
@Component
public class RegisterOnlineList implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        sce.getServletContext().setAttribute("onlineList", new OnlineList());
    }
}
