package de.craftsblock.craftscore.json;

import de.craftsblock.craftscore.utils.Validator;

import java.io.*;

/**
 * The JsonParser class provides methods for parsing JSON data from a file or a String.
 * It returns a custom Json object containing the parsed data.
 *
 * @author CraftsBlock
 * @version 1.0
 * @see Json
 * @see Validator
 * @since 3.6#16-SNAPSHOT
 */
public final class JsonParser {

    /**
     * Parses JSON data from a file and returns a Json object containing the parsed data.
     *
     * @param f The file containing the JSON data.
     * @return A Json object containing the parsed JSON data, or an empty Json object if parsing fails or the file is empty.
     */
    public static Json parse(File f) {
        try {
            // Create the parent directories if they do not exist
            if (!f.getParentFile().exists()) f.getParentFile().mkdirs();
            // Create the file if it does not exist
            f.createNewFile();
            // Read the JSON data from the file
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            String json = bufferedReader.readLine();
            // Validate the JSON data and parse it into a Json object
            if (json != null && Validator.isJsonValid(json))
                return new Json(com.google.gson.JsonParser.parseString(json).getAsJsonObject());
            else return new Json(com.google.gson.JsonParser.parseString("{}").getAsJsonObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Parses JSON data from a String and returns a Json object containing the parsed data.
     *
     * @param json The JSON data as a String.
     * @return A Json object containing the parsed JSON data, or null if parsing fails.
     */
    public static Json parse(String json) {
        try {
            // Validate the JSON data and parse it into a Json object
            if (json != null && Validator.isJsonValid(json))
                return new Json(com.google.gson.JsonParser.parseString(json).getAsJsonObject());
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Return null if parsing fails or an exception occurs
        return null;
    }

}
