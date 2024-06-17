package com.amazonas.frontend.utils;

import com.vaadin.flow.component.HasValue;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class POJOBinder<T> {

    private final Class<T> clazz;
    private final Map<String, Field> fields;
    private final Map<String, HasValue<?,Object>> components;
    private T object;

    public POJOBinder(Class<T> clazz) {
        this.clazz = clazz;
        fields = new HashMap<>();
        components = new HashMap<>();
    }

    public void bind(HasValue<?,?> component, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            fields.put(fieldName, field);
            components.put(fieldName, (HasValue<?, Object>) component);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("Field " + fieldName + " not found in class " + clazz.getName());
        }
    }

    public void readObject(T object) {
        this.object = object;
        if(object == null){
            return;
        }
        fields.forEach((fieldName, field) -> {
            try {
                components.get(fieldName).setValue(field.get(object));
            } catch (IllegalAccessException ignored) {}
        });
    }

    /**
     * Write the values from the components to the object
     */
    public T writeAndGetObject() {
        fields.forEach((fieldName, field) -> {
            HasValue<?,Object> component = components.get(fieldName);
            Object value = component.getValue();
            try {
                field.set(object, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        return object;
    }

    public T getObject() {
        return object;
    }
}
