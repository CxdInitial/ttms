package me.cxd.web.authentic;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class OnlineList {
    private final Set<Long> list;

    public OnlineList() {
        this.list = Collections.synchronizedSet(new HashSet<>());
    }

    public boolean isOnline(long number) {
        return list.contains(number);
    }

    public synchronized void online(long number) {
        list.add(number);
    }

    public synchronized void offline(long number) {
        list.remove(number);
    }
}
