package de.craftsblock.craftscore.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.Reader;

/**
 * The Validator class provides utility methods to validate JSON strings.
 * It checks if a given JSON string is valid and well-formed.
 *
 * @author Philipp Maywald
 * @author CraftsBlock
 * @version 2.0.0
 * @see Json
 * @see de.craftsblock.craftscore.json.JsonParser
 * @since 2.4-SNAPSHOT
 */
public final class JsonValidator {

    private JsonValidator() {
        // Private constructor to disallow instantiation.
    }

    /**
     * Validates whether the provided {@link JsonElement} is parseable by {@link Json}.
     *
     * @param element The {@link JsonElement} to check.
     * @return {@code true} if the json is parseable, {@code false} otherwise.
     */
    public static boolean isParsable(JsonElement element) {
        return element != null && !element.isJsonPrimitive() && !element.isJsonNull();
    }

    /**
     * Validates whether the provided JSON string is valid and well-formed.
     *
     * @param json The JSON string to be validated.
     * @return {@code true} if the json string is valid, {@code false} otherwise.
     */
    public static boolean isValid(final String json) {
        try {
            JsonElement element = JsonParser.parseString(json);
            return isParsable(element);
        } catch (JsonSyntaxException e) {
            return false;
        }
    }

    /**
     * Validates whether the JSON data read from the provided reader is valid and well-formed.
     *
     * @param reader The reader from which the JSON data is read.
     * @return {@code true} if the json reader is valid, {@code false} otherwise.
     */
    public static boolean isValid(final Reader reader) {
        try {
            JsonElement element = JsonParser.parseReader(reader);
            return isParsable(element);
        } catch (JsonSyntaxException e) {
            return false;
        }
    }

}
