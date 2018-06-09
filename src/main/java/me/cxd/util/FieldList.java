package me.cxd.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class FieldList<T> {
    private final Class<T> clz;

    public FieldList() {
        clz = ((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
    }

    public Set<Field> getFields() {
        return Arrays.stream(clz.getDeclaredFields()).collect(Collectors.toSet());
    }

    private Map<String, Method> get(String s) {
        Function<Method, String> getField = method -> {
            String methodName = method.getName();
            if (methodName.startsWith(s)) {
                char[] fieldName = methodName.substring(3).toCharArray();
                if (fieldName.length != 0) {
                    fieldName[0] += 32;
                    return String.valueOf(fieldName);
                }
            }
            return "";
        };
        Set<Field> fields = getFields();
        Map<String, Field> map = fields.stream().collect(Collectors.toMap(Field::getName, Function.identity()));
        Set<String> fieldNames = fields.stream().map(Field::getName).collect(Collectors.toSet());
        return Arrays.stream(clz.getMethods()).filter(method -> fieldNames.contains(getField.apply(method))).collect(Collectors.toMap(getField, Function.identity()));
    }

    public Map<String, Method> getGetters() {
        return get("get");
    }

    public Map<String, Method> getSetters() {
        return get("set");
    }
}
