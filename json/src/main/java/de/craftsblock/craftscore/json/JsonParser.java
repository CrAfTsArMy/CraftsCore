package de.craftsblock.craftscore.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import de.craftsblock.craftscore.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

/**
 * The JsonParser class provides methods for parsing JSON data from a file or a String.
 * It returns a custom Json object containing the parsed data.
 *
 * @author Philipp Maywald
 * @author CraftsBlock
 * @version 2.0.2
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
    public static Json parse(File file) {
        return parse(file.toPath());
    }

    /**
     * Parses the content of a given file at a specific path into a {@link Json} object.
     *
     * @param path The path to be parsed
     * @return A {@link Json} object representing the content of the path
     * @throws RuntimeException If an I/O error occurs during reading the path
     */
    public static Json parse(Path path) {
        try {
            Path parent = path.getParent();
            if (Files.notExists(parent)) Files.createDirectories(parent);
            if (Files.notExists(path)) Files.createFile(path);

            return parse(Files.newInputStream(path, StandardOpenOption.READ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Parses the content of an InputStream into a {@link Json} object.
     * <p>
     * This method reads all bytes from the provided InputStream and converts them into a json string.
     * </p>
     *
     * @param stream The InputStream to be parsed
     * @return A {@link Json} object representing the content of the InputStream
     * @throws RuntimeException If an I/O error occurs during reading from the stream
     */
    public static Json parse(InputStream stream) {
        try (ByteArrayOutputStream data = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[2048];

            int read;
            while ((read = stream.read(buffer)) != -1)
                data.write(buffer, 0, read);

            Arrays.fill(buffer, (byte) 0);
            return parse(data.toByteArray(), 0, data.size());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Parses a specific portion of a byte array into a {@link Json} object.
     * <p>
     * This method extracts a sub-array from the provided byte array starting at a specified position
     * and with a specified length. It then converts this sub-array into a JSON string and returns
     * the corresponding {@link Json} object. The method ensures that the sub-array contains only valid UTF-8 bytes.
     * </p>
     *
     * @param data   The byte array containing JSON data
     * @param pos    The starting position of the sub-array
     * @param length The length of the sub-array
     * @return A {@link Json} object representing the content of the sub-array
     * @throws IllegalArgumentException If the sub-array contains non-positive bytes
     */
    public static Json parse(byte[] data, int pos, int length) {
        assert pos >= 0;
        assert length <= data.length;
        byte[] specific = Arrays.copyOfRange(data, pos, length);

        if (!Utils.isEncodingValid(specific, StandardCharsets.UTF_8))
            throw new IllegalArgumentException("The sub-array is not encoded as a utf8 string!");

        return parse(new String(specific, StandardCharsets.UTF_8));
    }

    /**
     * Parses a json string into a {@link Json} object.
     *
     * @param json The json string to be parsed
     * @return A {@link Json} object representing the content of the json string, or null if the string is invalid
     * @throws RuntimeException If an I/O error occurs during parsing
     */
    public static Json parse(String json) {
        try {
            if (json != null && JsonValidator.isValid(json))
                return new Json(com.google.gson.JsonParser.parseString(json));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Json.empty();
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
    public static Json parse(JsonElement element) {
        if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            return primitive.isString() ? parse(primitive.getAsString()) : null;
        }

        return new Json(element);
    }

}
