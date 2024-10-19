package de.craftsblock.craftscore.utils;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * The Validator class provides utility methods to validate JSON strings.
 * It checks if a given JSON string is valid and well-formed.
 *
 * @author Philipp Maywald
 * @author CraftsBlock
 * @version 1.0
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
        return isJsonValid(new StringReader(json));
    }

    /**
     * Validates whether the JSON data read from the provided reader is valid and well-formed.
     *
     * @param reader The reader from which the JSON data is read.
     * @return True if the JSON data is valid, false otherwise.
     * @throws IOException If an I/O error occurs while validating the JSON data.
     */
    public static boolean isJsonValid(final Reader reader) throws IOException {
        return isJsonValid(new JsonReader(reader));
    }

    /**
     * Validates whether the JSON data read from the provided JsonReader is valid and well-formed.
     *
     * @param jsonReader The JsonReader instance to read the JSON data.
     * @return True if the JSON data is valid, false otherwise.
     * @throws IOException If an I/O error occurs while validating the JSON data.
     */
    public static boolean isJsonValid(final JsonReader jsonReader) throws IOException {
        try {
            JsonToken token;
            loop:
            // Loop through the JSON data using the JsonReader to validate its structure.
            while ((token = jsonReader.peek()) != JsonToken.END_DOCUMENT && token != null) {
                switch (token) {
                    case BEGIN_ARRAY:
                        jsonReader.beginArray();
                        break;
                    case END_ARRAY:
                        jsonReader.endArray();
                        break;
                    case BEGIN_OBJECT:
                        jsonReader.beginObject();
                        break;
                    case END_OBJECT:
                        jsonReader.endObject();
                        break;
                    case NAME:
                        jsonReader.nextName();
                        break;
                    case STRING:
                    case NUMBER:
                    case BOOLEAN:
                    case NULL:
                        jsonReader.skipValue();
                        break;
                    case END_DOCUMENT:
                        break loop;
                    default:
                        throw new AssertionError(token);
                }
            }
            return true; // If the loop completes without any exceptions, the JSON data is valid.
        } catch (final MalformedJsonException ignored) {
            return false; // If a MalformedJsonException is caught, it indicates that the JSON data is not valid.
        }
    }

}
