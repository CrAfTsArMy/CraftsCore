package de.craftsblock.craftscore.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.jetbrains.annotations.NotNull;

import java.io.Reader;
import java.util.function.Supplier;

/**
 * The Validator class provides utility methods to validate JSON strings.
 * It checks if a given JSON string is valid and well-formed.
 *
 * @author Philipp Maywald
 * @author CraftsBlock
 * @version 2.1.0
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
     * @since 3.8.12
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
            return isParsable(safeParse(() -> JsonParser.parseString(json)));
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
            return isParsable(safeParse(() -> JsonParser.parseReader(reader)));
        } catch (JsonSyntaxException e) {
            return false;
        }
    }

    /**
     * Safely parses the given parser while transforming a {@link JsonSyntaxException}
     * to {@link JsonNull#INSTANCE}.
     *
     * @param parser The parser to perform.
     * @return The parsed {@link JsonElement} or {@link JsonNull#INSTANCE}
     * @since 3.8.12
     */
    static @NotNull JsonElement safeParse(Supplier<JsonElement> parser) {
        try {
            return parser.get();
        } catch (JsonSyntaxException e) {
            return JsonNull.INSTANCE;
        }
    }

}
