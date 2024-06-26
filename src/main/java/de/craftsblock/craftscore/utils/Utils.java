package de.craftsblock.craftscore.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * The Utils class provides various utility methods for common operations and reflections.
 * It offers functionalities like reading all bytes from an InputStream, finding methods with specific annotations,
 * and checking for the existence of certain annotations or methods in a class hierarchy.
 *
 * @author CraftsBlock
 * @version 1.5
 * @since 3.0-SNAPSHOT
 */
public final class Utils {

    /**
     * Reads all bytes from the given InputStream and returns them as a byte array.
     *
     * @param inputStream The InputStream from which bytes will be read.
     * @return The byte array containing all the bytes read from the InputStream.
     * @throws IOException If an I/O error occurs during the reading process.
     */
    public static byte[] readAllBytes(InputStream inputStream) throws IOException {
        final int bufLen = 4 * 0x400; // 4KB
        byte[] buf = new byte[bufLen];
        int readLen;
        IOException exception = null;

        try {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                while ((readLen = inputStream.read(buf, 0, bufLen)) != -1)
                    outputStream.write(buf, 0, readLen);

                return outputStream.toByteArray();
            }
        } catch (IOException e) {
            exception = e;
            throw e;
        } finally {
            if (exception == null) inputStream.close();
            else try {
                inputStream.close();
            } catch (IOException e) {
                exception.addSuppressed(e);
            }
        }
    }

    /**
     * Finds and returns a list of all methods in the given class and its superclasses that are annotated with the specified annotation.
     *
     * @param type       The class to search for annotated methods.
     * @param annotation The annotation class to be searched for.
     * @return A list of methods annotated with the specified annotation.
     */
    @NotNull
    public static List<Method> getMethodsByAnnotation(final Class<?> type, final Class<? extends Annotation> annotation) {
        final List<Method> methods = new ArrayList<>();
        Class<?> clazz = type;
        while (clazz != Object.class) {
            for (final Method method : clazz.getDeclaredMethods())
                if (method.isAnnotationPresent(annotation)) {
                    methods.add(method);
                }
            clazz = clazz.getSuperclass();
        }
        return methods;
    }

    /**
     * Finds and returns the Method object representing the specified method in the given class or its superclasses.
     *
     * @param type           The class to search for the method.
     * @param name           The name of the method.
     * @param parameterTypes The parameter types of the method (null if none).
     * @return The Method object representing the specified method, or null if not found.
     */
    @Nullable
    public static Method getMethod(final Class<?> type, String name, @Nullable Class<?>... parameterTypes) {
        Class<?> clazz = type;
        while (clazz != Object.class) {
            try {
                return clazz.getDeclaredMethod(name, parameterTypes);
            } catch (NoSuchMethodException ignored) {
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    /**
     * Checks whether the given class or its superclasses have the specified annotation.
     *
     * @param type       The class to check for the annotation.
     * @param annotation The annotation class to be checked for.
     * @return True if the annotation is present in the class hierarchy, false otherwise.
     */
    public static boolean checkForAnnotation(final Class<?> type, final Class<? extends Annotation> annotation) {
        Class<?> clazz = type;
        while (clazz != Object.class) {
            if (clazz.isAnnotationPresent(annotation)) return true;
            clazz = clazz.getSuperclass();
        }
        return false;
    }

    /**
     * Checks whether the given class or its superclasses contain the specified method with the provided name and parameter types.
     *
     * @param type           The class to check for the method.
     * @param name           The name of the method.
     * @param parameterTypes The parameter types of the method (null if none).
     * @return True if the method exists in the class hierarchy, false otherwise.
     */
    public static boolean checkForMethod(final Class<?> type, String name, @Nullable Class<?>... parameterTypes) {
        Class<?> clazz = type;
        while (clazz != Object.class) {
            if (getMethod(type, name, parameterTypes) != null) return true;
            clazz = clazz.getSuperclass();
        }
        return false;
    }

}
