package de.craftsblock.craftscore.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.Reader;

/**
 * The Validator class provides utility methods to validate JSON strings.
 * It checks if a given JSON string is valid and well-formed.
 *
 * @author Philipp Maywald
 * @author CraftsBlock
 * @version 2.0.0
 * @see de.craftsblock.craftscore.json.Json
 * @see de.craftsblock.craftscore.json.JsonParser
 * @since 2.4-SNAPSHOT
 */
public final class Validator {

    /**
     * Validates whether the provided JSON string is valid and well-formed.
     *
     * @param json The JSON string to be validated.
     * @return True if the JSON string is valid, false otherwise.
     * @throws IOException If an I/O error occurs while validating the JSON string.
     */
    public static boolean isJsonValid(final String json) throws IOException {
        try {
            JsonElement element = JsonParser.parseString(json);
            return element != null && !element.isJsonPrimitive() && !element.isJsonNull();
        } catch (JsonSyntaxException e) {
            return false;
        }
    }

    /**
     * Validates whether the JSON data read from the provided reader is valid and well-formed.
     *
     * @param reader The reader from which the JSON data is read.
     * @return True if the JSON data is valid, false otherwise.
     * @throws IOException If an I/O error occurs while validating the JSON data.
     */
    public static boolean isJsonValid(final Reader reader) throws IOException {
        StringBuilder read  = new StringBuilder();
        char[] buffer = new char[1024];
        int length;

        while ((length = reader.read(buffer)) != -1)
            read.append(buffer, 0, length);

        return isJsonValid(read.toString());
    }

}
