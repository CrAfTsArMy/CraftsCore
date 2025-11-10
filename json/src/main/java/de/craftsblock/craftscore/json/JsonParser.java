package de.craftsblock.craftscore.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * The JsonParser class provides methods for parsing JSON data from a file or a String.
 * It returns a custom Json object containing the parsed data.
 *
 * @author Philipp Maywald
 * @author CraftsBlock
 * @version 2.1.1
 * @see Json
 * @see JsonValidator
 * @since 3.6#16-SNAPSHOT
 */
public final class JsonParser {

    private JsonParser() {
        // Private constructor to disallow instantiation.
    }

    /**
     * Parses the content of a given file into a {@link Json} object.
     *
     * @param file The file to be parsed
     * @return A {@link Json} object representing the content of the file
     * @throws RuntimeException If an I/O error occurs during reading or file creation
     */
    public static @NotNull Json parse(@NotNull File file) {
        return parse(file.toPath());
    }

    /**
     * Parses the content of a given file at a specific path into a {@link Json} object.
     *
     * @param path The path to be parsed
     * @return A {@link Json} object representing the content of the path
     * @throws RuntimeException If an I/O error occurs during reading the path
     */
    public static @NotNull Json parse(@NotNull Path path) {
        try {
            Path parent = path.getParent();
            if (parent != null && Files.notExists(parent)) Files.createDirectories(parent);
            if (Files.notExists(path)) Files.createFile(path);

            return parse(Files.newInputStream(path, StandardOpenOption.READ));
        } catch (IOException e) {
            throw new RuntimeException("Could not parse path %s to json!".formatted(path.toString()), e);
        }
    }

    /**
     * Parses a byte array into a {@link Json} object.
     *
     * @param data The byte array containing JSON data
     * @return A {@link Json} object representing the content of the sub-array
     * @since 3.8.12
     */
    public static @NotNull Json parse(byte @NotNull [] data) {
        return parse(data, 0, data.length);
    }

    /**
     * Parses a specific portion of a byte array into a {@link Json} object.
     *
     * @param data   The byte array containing JSON data
     * @param pos    The starting position of the sub-array
     * @param length The length of the sub-array
     * @return A {@link Json} object representing the content of the sub-array
     */
    public static @NotNull Json parse(byte @NotNull [] data, int pos, int length) {
        try (ByteArrayInputStream reader = new ByteArrayInputStream(data, pos, length)) {
            return parse(reader);
        } catch (IOException e) {
            throw new RuntimeException("Could not parse the byte array to json!", e);
        }
    }

    /**
     * Parses the content of an {@link InputStream} into a {@link Json} object.
     *
     * @param stream The {@link InputStream} to be parsed
     * @return A {@link Json} object representing the content of the {@link InputStream}
     * @throws RuntimeException If an I/O error occurs during reading from the stream
     */
    public static @NotNull Json parse(@NotNull InputStream stream) {
        try (InputStreamReader reader = new InputStreamReader(stream)) {
            return parse(reader);
        } catch (IOException e) {
            throw new RuntimeException("Could not parse the input stream to json!", e);
        }
    }

    /**
     * Parses the content of an {@link Reader} into a {@link Json} object.
     *
     * @param reader The {@link Reader} to be parsed
     * @return A {@link Json} object representing the content of the {@link Reader}
     * @throws RuntimeException If an I/O error occurs during reading from the stream
     * @since 3.8.12
     */
    public static @NotNull Json parse(@NotNull Reader reader) {
        return parse(JsonValidator.safeParse(() -> com.google.gson.JsonParser.parseReader(reader)));
    }

    /**
     * Parses a json string into a {@link Json} object.
     *
     * @param json The json string to be parsed
     * @return A {@link Json} object representing the content of the json string, or null if the string is invalid
     * @throws RuntimeException If an I/O error occurs during parsing
     */
    public static @NotNull Json parse(@Nullable String json) {
        if (json == null)
            return Json.empty();

        return parse(JsonValidator.safeParse(() -> com.google.gson.JsonParser.parseString(json)));
    }

    /**
     * Parses a {@link JsonElement} into a {@link Json} object.
     * <p>
     * This method checks if the provided JsonElement is a {@link com.google.gson.JsonPrimitive}. If it is a string,
     * it parses the string and returns the corresponding {@link Json} object. Otherwise, it returns
     * a new {@link Json} object representing the {@link JsonElement}.
     * </p>
     *
     * @param element The {@link JsonElement} to be parsed
     * @return A {@link Json} object representing the content of the {@link JsonElement}
     */
    public static @NotNull Json parse(@NotNull JsonElement element) {
        if (!JsonValidator.isParsable(element)) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            return primitive.isString() ? parse(primitive.getAsString()) : Json.empty();
        }

        return new Json(element);
    }

}
