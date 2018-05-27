package me.cxd.util;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DefaultValue {
    private final Map<Class, Object> defaultValues;

    DefaultValue() {
        defaultValues = Map.of(int.class, 0, long.class, 0L, short.class, (short) 0, float.class, (float) 0, double.class, (double) 0, byte.class, (byte) 0, char.class, (char) 0, boolean.class, false);
    }

    public Object get(Class clz){
        return defaultValues.get(clz);
    }
}

