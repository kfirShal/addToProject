package com.amazonas.frontend.utils;

import com.vaadin.flow.component.HasValue;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class POJOBinder<T> {

    private final Class<T> clazz;
    private final Map<String, FieldBinder> fieldNameToFieldBinder;
    private T object;

    public POJOBinder(Class<T> clazz) {
        this.clazz = clazz;
        fieldNameToFieldBinder = new HashMap<>();
    }

    /**
     * Bind a component to a field in the object
     * @return a {@link FieldBinder} that can be used to set a converter
     * @see Converter
     * @see FieldBinder#withConverter(Converter)
     */
    @SuppressWarnings({"unchecked"})
    public FieldBinder bind(HasValue<?,?> component, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            FieldBinder fb = new FieldBinder(field, (HasValue<?, Object>) component);
            fieldNameToFieldBinder.put(fieldName, fb);
            return fb;
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("Field " + fieldName + " not found in class " + clazz.getName());
        }
    }

    /**
     * Read the values from the object and set them in the components
     * @apiNote passing {@code null} as argument will clear the components
     */
    public void readObject(T object) {
        this.object = object;
        if(object == null){
            clear();
        }
        for (FieldBinder fieldBinder : fieldNameToFieldBinder.values()) {
            fieldBinder.read();
        }
    }

    /**
     * Clear the components
     * @apiNote equivalent to calling {@link POJOBinder#readObject(Object)} with {@code null} as argument
     */
    public void clear() {
        object = null;
        fieldNameToFieldBinder.values().forEach(FieldBinder::clear);
    }

    /**
     * Write the values from the components to the object that was passed to {@link #readObject(Object)}
     */
    public void writeObject() {
        if(object == null){
            throw new IllegalStateException("No object to write to");
        }
        fieldNameToFieldBinder.values().forEach(FieldBinder::write);
    }

    /**
     * Writes the values from the components to the objects supplied in the argument
     */
    public void writeObject(T object) {
        T temp = this.object;
        this.object = object;
        writeObject();
        this.object = temp;
    }

    public class FieldBinder {
        private final Field field;
        private final HasValue<?,Object> component;
        private Converter<Object,Object> converter;

        private FieldBinder(Field field, HasValue<?,Object> component) {
            this.field = field;
            this.component = component;
        }

        @SuppressWarnings("unchecked")
        public <T1,T2> void withConverter(Converter<T1,T2> converter) {
            this.converter = (Converter<Object,Object>)converter;
        }

        public void withIntegerConverter(){
            withConverter(new IntegerToStringConverter());
        }

        public void withDoubleConverter(){
            withConverter(new DoubleToStringConverter());
        }

        public void withBooleanConverter(){
            withConverter(new BooleanToStringConverter());
        }

        private void read() {
            try {
                Object value =  field.get(object);
                if(converter != null){
                    value = converter.to().apply(value);
                    component.setValue(converter.toType().cast(value));
                } else {
                    component.setValue(value);
                }
            } catch (IllegalAccessException ignored) {
                throw new RuntimeException(ignored);
            }
        }

        private void write() {
            Object value = component.getValue();
            try {
                if(converter != null){
                    value = converter.from().apply(value);
                    field.set(object, converter.fromType().cast(value));
                } else {
                    field.set(object, value);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        private void clear() {
            component.clear();
        }
    }

    private static class IntegerToStringConverter implements Converter<Integer,String> {

        @Override
        public Class<Integer> fromType() {
            return Integer.class;
        }

        @Override
        public Class<String> toType() {
            return String.class;
        }

        @Override
        public Function<Integer, String> to() {
            return String::valueOf;
        }

        @Override
        public Function<String, Integer> from() {
            return str -> {
                try {
                    return Integer.parseInt(str);
                } catch (Exception e) {
                    return null;
                }
            };
        }
    }

    private static class DoubleToStringConverter implements Converter<Double,String> {

        @Override
        public Class<Double> fromType() {
            return Double.class;
        }

        @Override
        public Class<String> toType() {
            return String.class;
        }

        @Override
        public Function<Double, String> to() {
            return String::valueOf;
        }

        @Override
        public Function<String,Double> from() {
            return str -> {
                try {
                    return Double.parseDouble(str);
                } catch (Exception e) {
                    return null;
                }
            };
        }
    }

    private static class BooleanToStringConverter implements Converter<Boolean,String> {
        @Override
        public Class<Boolean> fromType() {
            return Boolean.class;
        }

        @Override
        public Class<String> toType() {
            return String.class;
        }

        @Override
        public Function<Boolean, String> to() {
            return String::valueOf;
        }

        @Override
        public Function<String, Boolean> from() {
            return str -> {
                try {
                    return Boolean.parseBoolean(str);
                } catch (Exception e) {
                    return null;
                }
            };
        }
    }
}
