package me.cxd.web.authentic.listener;

import me.cxd.web.authentic.OnlineList;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class RegisterOnlineList implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        sce.getServletContext().setAttribute("onlineList",new OnlineList());
    }
}
