package de.craftsblock.craftscore.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility class providing common functionality for byte and reflection operations.
 *
 * @author Philipp Maywald
 * @author CraftsBlock
 * @version 2.0.1
 * @since 3.0-SNAPSHOT
 */
public final class Utils {

    /**
     * Reads all bytes from an InputStream and returns them as a byte array.
     *
     * @param inputStream The InputStream to read from.
     * @return A byte array containing the read data.
     * @throws IOException If an I/O error occurs while reading or closing the stream.
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
     * Retrieves all methods from a given class that are annotated with the specified annotation.
     *
     * @param type       The class to search for annotated methods.
     * @param annotation The annotation to look for.
     * @return A list of methods annotated with the specified annotation.
     */
    @NotNull
    public static List<Method> getMethodsByAnnotation(final Class<?> type, final Class<? extends Annotation> annotation) {
        final List<Method> methods = new ArrayList<>();
        getMethodsByAnnotation(methods, type, annotation);
        return methods;
    }

    /**
     * Recursively retrieves all methods with the specified annotation in the class hierarchy and adds them to the provided list.
     *
     * @param methods    The list to store the methods found.
     * @param type       The class to search for annotated methods.
     * @param annotation The annotation to look for.
     */
    private static void getMethodsByAnnotation(final List<Method> methods, final Class<?> type, final Class<? extends Annotation> annotation) {
        if (type == null || type.equals(Object.class)) return;

        for (final Class<?> iface : type.getInterfaces())
            getMethodsByAnnotation(methods, iface, annotation);
        getMethodsByAnnotation(methods, type.getSuperclass(), annotation);

        for (final Method method : type.getDeclaredMethods()) {
            Method overritten = getOverrittenMethod(method);
            if (overritten != null) methods.remove(overritten);

            if (method.isAnnotationPresent(annotation)) {
                methods.add(method);
            }
        }
    }

    /**
     * Checks if the class contains any methods annotated with the specified annotation.
     *
     * @param type       The class to search for annotated methods.
     * @param annotation The annotation to look for.
     * @return true if a method with the specified annotation is found, false otherwise.
     */
    public static boolean checkForMethodsWithAnnotation(final Class<?> type, final Class<? extends Annotation> annotation) {
        if (type == null || type.equals(Object.class)) return false;

        for (final Method method : type.getDeclaredMethods())
            if (method.isAnnotationPresent(annotation)) return true;

        for (final Class<?> iface : type.getInterfaces())
            if (checkForMethodsWithAnnotation(iface, annotation)) return true;

        return checkForMethodsWithAnnotation(type.getSuperclass(), annotation);
    }

    /**
     * Retrieves a method with the specified name and parameter types from the given class.
     *
     * @param type           The class to search for the method.
     * @param name           The name of the method.
     * @param parameterTypes The parameter types of the method (optional).
     * @return The method if found, or null if not found.
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
     * Checks if a method with the specified name and parameter types exists in the class.
     *
     * @param type           The class to search for the method.
     * @param name           The name of the method.
     * @param parameterTypes The parameter types of the method (optional).
     * @return true if the method exists, false otherwise.
     */
    public static boolean checkForMethod(final Class<?> type, String name, @Nullable Class<?>... parameterTypes) {
        Class<?> clazz = type;
        while (clazz != Object.class) {
            if (getMethod(type, name, parameterTypes) != null) return true;
            clazz = clazz.getSuperclass();
        }
        return false;
    }

    /**
     * Checks if a byte array can be decoded using the specified charset.
     *
     * @param data    The byte array to validate.
     * @param charset The charset to use for decoding.
     * @return true if the data can be decoded with the given charset, false otherwise.
     */
    public static boolean isEncodingValid(byte @NotNull [] data, @NotNull Charset charset) {
        CharsetDecoder decoder = charset.newDecoder();
        ByteBuffer buf = ByteBuffer.wrap(data);

        try {
            decoder.decode(buf);
        } catch (CharacterCodingException e) {
            return false;
        }
        return true;
    }

    /**
     * Retrieves the overridden method for the given method, if it exists in a superclass or interface.
     * Inspired by <a href="https://stackoverflow.com/a/15206147">Marek Potociar on stackoverflow</a>.
     *
     * @param method The method for which to find the overridden method.
     * @return The overridden method, or null if not found.
     */
    public static Method getOverrittenMethod(final Method method) {
        Class<?> declaringClass = method.getDeclaringClass();
        if (declaringClass.equals(Object.class)) return null;

        try {
            Class<?> superclass = declaringClass.getSuperclass();
            if (superclass == null) throw new NoSuchMethodException();
            return superclass.getMethod(method.getName(), method.getParameterTypes());
        } catch (NoSuchMethodException e) {
            for (Class<?> iface : declaringClass.getInterfaces())
                try {
                    return iface.getMethod(method.getName(), method.getParameterTypes());
                } catch (NoSuchMethodException ignored) {
                }

            return null;
        }
    }

    /**
     * Retrieves the oldest n files from a specified folder.
     *
     * @param folder the {@link Path} of the folder to search within.
     * @param amount the maximum number of oldest files to retrieve.
     * @return a list of the {@link Path} objects representing the oldest files, sorted
     * in ascending order by modification date.
     * @throws RuntimeException if an {@link IOException} occurs while accessing the folder.
     */
    public static List<Path> getOldestNFiles(Path folder, int amount) {
        try (Stream<Path> paths = Files.walk(folder)) {
            return paths.filter(Files::isRegularFile)
                    .sorted(Comparator.comparingLong(p -> p.toFile().lastModified()))
                    .limit(amount)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
