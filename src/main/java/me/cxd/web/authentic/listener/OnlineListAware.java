package me.cxd.web.authentic.listener;

import me.cxd.web.authentic.OnlineList;

import javax.servlet.http.HttpSessionEvent;

interface OnlineListAware {
    default OnlineList getOnlineList(HttpSessionEvent event){
        Object list = event.getSession().getServletContext().getAttribute("onlineList");
        if (list instanceof OnlineList)
            return (OnlineList) list;
        return null;
    }
}
