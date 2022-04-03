package de.craftsarmy.craftscore.utils;

import java.lang.reflect.Method;

public final class Touch<E extends Touch.TouchAble> {

    private final Class<?> instance;

    public Touch(Class<?> instance) {
        this.instance = instance;
    }

    public E touch(Class<? extends E> clazz) throws Exception {
        return clazz.getDeclaredConstructor(Class.class).newInstance(instance);
    }

    public static abstract class TouchAble {

        public TouchAble(Class<?> from) {
            try {
                boolean callback = false;
                Method[] methods = from.getMethods();
                for (Method method : methods)
                    if (method.getName().equals("callback")) {
                        callback = true;
                        break;
                    }
                if (callback)
                    from.getMethod("callback").invoke(from.getDeclaredConstructor(Class.class).newInstance(this.getClass()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
