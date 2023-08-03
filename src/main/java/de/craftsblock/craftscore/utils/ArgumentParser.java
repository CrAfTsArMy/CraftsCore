package de.craftsblock.craftscore.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * The ArgumentParser class parses command-line arguments and provides methods to retrieve their values as different data types.
 * It allows easy access to command-line arguments and their values, making it convenient to handle command-line inputs.
 *
 * @author CraftsBlock
 * @version 1.0
 * @since 3.7.22-SNAPSHOT
 */
public class ArgumentParser {

    private final Map<String, String> arguments = new HashMap<>();

    /**
     * Constructs an ArgumentParser instance and parses the provided command-line arguments.
     *
     * @param args The command-line arguments to be parsed.
     */
    public ArgumentParser(String[] args) {
        parseArgs(args);
    }

    /**
     * Parses the provided command-line arguments and stores the argument-value pairs in a map.
     *
     * @param args The command-line arguments to be parsed.
     */
    private void parseArgs(String[] args) {
        String key = null;
        for (String arg : args) {
            if (arg.matches("--?[\\w-]+")) {
                key = arg.replaceFirst("--?", "");
                arguments.put(key, "");
            } else if (key != null) {
                arguments.put(key, arg);
                key = null;
            }
        }
    }

    /**
     * Retrieves the value of the argument as a string.
     *
     * @param key The key of the argument to retrieve.
     * @return The value of the argument as a string, or null if the argument does not exist.
     */
    public String getAsString(String key) {
        return arguments.get(key);
    }

    /**
     * Retrieves the value of the argument as a boolean.
     *
     * @param key The key of the argument to retrieve.
     * @return The value of the argument as a boolean, or false if the argument does not exist or is not a valid boolean.
     */
    public boolean getAsBoolean(String key) {
        return Boolean.parseBoolean(getAsString(key));
    }

    /**
     * Retrieves the value of the argument as an integer.
     *
     * @param key The key of the argument to retrieve.
     * @return The value of the argument as an integer, or 0 if the argument does not exist or is not a valid integer.
     */
    public int getAsInt(String key) {
        return Integer.parseInt(getAsString(key));
    }

    /**
     * Retrieves the value of the argument as a long.
     *
     * @param key The key of the argument to retrieve.
     * @return The value of the argument as a long, or 0L if the argument does not exist or is not a valid long.
     */
    public long getAsLong(String key) {
        return Long.parseLong(getAsString(key));
    }

    /**
     * Retrieves the value of the argument as a double.
     *
     * @param key The key of the argument to retrieve.
     * @return The value of the argument as a double, or 0.0 if the argument does not exist or is not a valid double.
     */
    public double getAsDouble(String key) {
        return Double.parseDouble(getAsString(key));
    }

    /**
     * Retrieves the value of the argument as a short.
     *
     * @param key The key of the argument to retrieve.
     * @return The value of the argument as a short, or 0 if the argument does not exist or is not a valid short.
     */
    public short getAsShort(String key) {
        return Short.parseShort(getAsString(key));
    }

    /**
     * Retrieves the value of the argument as a float.
     *
     * @param key The key of the argument to retrieve.
     * @return The value of the argument as a float, or 0.0f if the argument does not exist or is not a valid float.
     */
    public float getAsFloat(String key) {
        return Float.parseFloat(getAsString(key));
    }

    /**
     * Retrieves the value of the argument as a byte.
     *
     * @param key The key of the argument to retrieve.
     * @return The value of the argument as a byte, or 0 if the argument does not exist or is not a valid byte.
     */
    public byte getAsByte(String key) {
        return Byte.parseByte(getAsString(key));
    }

    /**
     * Checks if the specified argument is present in the command-line arguments.
     *
     * @param key The key of the argument to check.
     * @return true if the argument is present, false otherwise.
     */
    public boolean isPresent(String key) {
        return arguments.containsKey(key);
    }

}
