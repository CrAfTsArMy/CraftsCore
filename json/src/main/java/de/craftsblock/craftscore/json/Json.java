package de.craftsblock.craftscore.json;

import com.google.gson.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The Json class represents a json object and provides methods to work with json data.
 *
 * @author Philipp Maywald
 * @author CraftsBlock
 * @version 2.2.1
 * @see JsonParser
 * @since 3.6#16-SNAPSHOT
 */
public final class Json {

    private static final Gson GSON = newGsonBuilder().create();
    private static final Gson PRETTY_GSON = newGsonBuilder().setPrettyPrinting().create();

    /**
     * Creates a new {@link GsonBuilder} with some preset values.
     *
     * @return The new {@link GsonBuilder}.
     */
    public static @NotNull GsonBuilder newGsonBuilder() {
        return new GsonBuilder().serializeNulls().disableHtmlEscaping();
    }

    private JsonElement object;

    /**
     * Constructs a new Json object with the given JsonElement.
     *
     * @param object The underlying JsonElement to work with.
     */
    public Json(@NotNull JsonElement object) {
        this.object = object;
    }

    /**
     * Checks if the json data contains a value at the specified path.
     *
     * @param path The path to the value in the json data.
     * @return true if the value exists at the path, false otherwise.
     */
    public boolean contains(@NotNull String path) {
        String[] args = path.split("(?<!\\\\)\\.");
        JsonElement destination = getParentOfPath(path, false);

        if (destination == null || destination.isJsonNull()) return false;

        String arg = args[args.length - 1].replace("\\.", ".");
        if (destination.isJsonObject()) return destination.getAsJsonObject().has(arg);

        if (destination.isJsonArray())
            try {
                JsonArray array = destination.getAsJsonArray();
                int index = argumentToIndex(arg, array, false);
                return array.size() > index;
            } catch (IllegalStateException e) {
                return false;
            }

        return true;
    }

    /**
     * Removes a value from the json data at the specified path.
     *
     * @param path The path to the value to remove from the json data.
     * @return The Json object itself after removing the value.
     */
    public @NotNull Json remove(@NotNull String path) {
        String[] args = path.split("(?<!\\\\)\\.");
        JsonElement target = getParentOfPath(path, false);

        if (target == null || target.isJsonNull()) return this;

        String lastArg = args[args.length - 1].replace("\\.", ".");
        if (target.isJsonArray()) {
            JsonArray array = target.getAsJsonArray();
            int index = argumentToIndex(lastArg, array, false);

            if (index >= array.size()) return this;
            array.remove(index);
        } else if (target.isJsonObject())
            target.getAsJsonObject().remove(lastArg);
        return this;
    }

    /**
     * Deserializes the json data at the specified path to an object of the specified class.
     *
     * @param path     The path to the json data in the json object.
     * @param classOfT The class to deserialize the json data into.
     * @param <T>      The type of the deserialized object.
     * @return The deserialized object of the specified class, or null if the path does not exist.
     */
    public <T> @Nullable T deserialize(@NotNull String path, @NotNull Class<T> classOfT) {
        if (contains(path)) return GSON.fromJson(get(path), classOfT);
        return null;
    }

    /**
     * Serializes the provided data and sets it at the specified path in the json data.
     *
     * @param path The path to set the serialized data in the json data.
     * @param data The data to be serialized and set at the path.
     * @param <T>  The type of the object that should be serialized.
     * @return The Json object itself after setting the data at the path.
     */
    public <T> @NotNull Json serialize(@NotNull String path, @NotNull T data) {
        Json json = JsonParser.parse(GSON.toJson(data));
        set(path, json.getObject());
        return this;
    }

    /**
     * Navigates to the parent element of the given json path.
     * If the path does not exist and {@code initIfMissing} is {@code true},
     * it will initialize any missing objects or arrays along the way.
     *
     * @param path          The json path string using dot notation, with optional escaped dots and array indicators
     * @param initIfMissing If {@code true}, missing elements along the path will be initialized
     * @return The {@link JsonElement} representing the parent of the final path segment, or
     * {@code null} if it doesn't exist and {@code initIfMissing} is {@code false}
     * @since 3.8.9
     */
    private @Nullable JsonElement getParentOfPath(@NotNull String path, boolean initIfMissing) {
        String[] segments = path.split("(?<!\\\\)\\.");
        JsonElement current = getObject();
        StringBuilder processedPath = new StringBuilder();

        for (int i = 0; i < segments.length - 1; i++) {
            String key = segments[i].replace("\\.", ".");
            boolean isArray = key.startsWith("$");

            if (current == null || current.isJsonNull())
                if (initIfMissing)
                    current = initPath(processedPath.toString(), isArray);
                else return JsonNull.INSTANCE;

            String nextSegment = segments[i + 1];
            boolean nextIsArray = nextSegment.startsWith("$");
            String processedKey;

            if (isArray) {
                JsonArray array = current.getAsJsonArray();
                int index = argumentToIndex(key, array, initIfMissing);
                current = initIfMissing
                        ? getOrCreate(array, index, nextIsArray)
                        : array.get(index);
                processedKey = "$" + index;
            } else {
                JsonObject object = current.getAsJsonObject();
                current = initIfMissing
                        ? getOrCreate(object, key, nextIsArray)
                        : object.get(key);
                processedKey = key;
            }

            processedPath.append(processedKey);
            if (i < segments.length - 2)
                processedPath.append(".");
        }

        return current;
    }

    /**
     * Retrieves the JsonElement at the specified path in the json data.
     *
     * @param path The path to the JsonElement in the json data.
     * @return The JsonElement at the given path, or null if the path does not exist.
     */
    public @NotNull JsonElement get(@NotNull String path) {
        String[] args = path.split("(?<!\\\\)\\.");
        if (path.isBlank()) return getObject();

        JsonElement destination = getParentOfPath(path, false);
        if (destination == null || destination.isJsonNull()) return JsonNull.INSTANCE;

        String arg = args[args.length - 1].replace("\\.", ".");

        JsonElement element;
        if (destination.isJsonObject())
            element = destination.getAsJsonObject().get(arg);
        else if (destination.isJsonArray())
            element = destination.getAsJsonArray()
                    .get(argumentToIndex(arg, destination.getAsJsonArray(), false));
        else element = destination;

        return element != null && !element.isJsonNull() ? element : JsonNull.INSTANCE;
    }

    /**
     * Retrieves the JsonElement at the specified path in the json data.
     * If no data is associated with the path, a fallback data value is returned.
     *
     * @param path     The path to the JsonElement in the json data.
     * @param fallback The fallback data to return if no data is associated with the path
     * @return The JsonElement at the given path, or null if the path does not exist.
     */
    @Contract(value = "_, !null -> !null", pure = true)
    public @Nullable JsonElement getOrDefault(@NotNull String path, @Nullable JsonElement fallback) {
        JsonElement element = get(path);
        return !element.isJsonNull() ? element : fallback;
    }

    /**
     * Retrieves the JsonElement at the specified path in the json data and returns it as a new instance of {@link Json}.
     *
     * @param path The path to the data in the json data.
     * @return The {@link Json} at the given path, or null if the path does not exist.
     */
    public @Nullable Json getJson(@NotNull String path) {
        return getJson(path, null);
    }

    /**
     * Retrieves the JsonElement at the specified path in the json data and returns it as a new instance of {@link Json}.
     * If no data is associated with the path, a fallback data value is returned.
     *
     * @param path     The path to the data in the json data.
     * @param fallback The fallback data to return if no data is associated with the path
     * @return The {@link Json} at the given path, or the fallback value if the path does not exist.
     */
    @Contract(value = "_, !null -> !null", pure = true)
    public @Nullable Json getJson(@NotNull String path, @Nullable Json fallback) {
        JsonElement element = get(path);
        return !element.isJsonNull() ? JsonParser.parse(element) : fallback;
    }

    /**
     * Retrieves the String value at the specified path in the json data.
     *
     * @param path The path to the String value in the json data.
     * @return The String value at the given path, or an empty string if the path does not exist or the value is not a string.
     */
    public @Nullable Character getCharacter(@NotNull String path) {
        return getCharacter(path, null);
    }

    /**
     * Retrieves the {@link String} at the specified path in the json data.
     * If no data is associated with the path, a fallback data value is returned.
     *
     * @param path   The path to the {@link String} in the json data.
     * @param orElse The fallback data to return if no data is associated with the path.
     * @return The {@link String} at the given path, or the {@code orElse} value if the path does not exist.
     */
    @SuppressWarnings("deprecation")
    @Contract(value = "_, !null -> !null", pure = true)
    public @Nullable Character getCharacter(@NotNull String path, @Nullable Character orElse) {
        try {
            JsonElement element = get(path);
            return element.isJsonPrimitive() ? Character.valueOf(element.getAsCharacter()) : orElse;
        } catch (UnsupportedOperationException e) {
            return orElse;
        }
    }

    /**
     * Retrieves the String value at the specified path in the json data.
     *
     * @param path The path to the String value in the json data.
     * @return The String value at the given path, or an empty string if the path does not exist or the value is not a string.
     */
    public @NotNull String getString(@NotNull String path) {
        return getString(path, "");
    }

    /**
     * Retrieves the {@link String} at the specified path in the json data.
     * If no data is associated with the path, a fallback data value is returned.
     *
     * @param path   The path to the {@link String} in the json data.
     * @param orElse The fallback data to return if no data is associated with the path.
     * @return The {@link String} at the given path, or the {@code orElse} value if the path does not exist.
     */
    @Contract(value = "_, !null -> !null", pure = true)
    public @Nullable String getString(@NotNull String path, @Nullable String orElse) {
        JsonElement element = get(path);
        return element != null && element.isJsonPrimitive() ? element.getAsString() : orElse;
    }

    /**
     * Retrieves the boolean value at the specified path in the json data.
     *
     * @param path The path to the boolean value in the json data.
     * @return The boolean value at the given path, or false if the path does not exist or the value is not a boolean.
     */
    public boolean getBoolean(@NotNull String path) {
        return getBoolean(path, false);
    }

    /**
     * Retrieves the {@link Boolean} at the specified path in the json data.
     * If no data is associated with the path, a fallback data value is returned.
     *
     * @param path   The path to the {@link Boolean} in the json data.
     * @param orElse The fallback data to return if no data is associated with the path.
     * @return The {@link Boolean} at the given path, or the {@code orElse} value if the path does not exist.
     */
    public boolean getBoolean(@NotNull String path, boolean orElse) {
        JsonElement element = get(path);
        return element.isJsonPrimitive() ? element.getAsBoolean() : orElse;
    }

    /**
     * Retrieves the byte value at the specified path in the json data.
     *
     * @param path The path to the byte value in the json data.
     * @return The byte value at the given path, or -1 if the path does not exist or the value is not a byte.
     */
    public byte getByte(@NotNull String path) {
        return getByte(path, (byte) -1);
    }

    /**
     * Retrieves the {@link Byte} at the specified path in the json data.
     * If no data is associated with the path, a fallback data value is returned.
     *
     * @param path   The path to the {@link Byte} in the json data.
     * @param orElse The fallback data to return if no data is associated with the path.
     * @return The {@link Byte} at the given path, or the {@code orElse} value if the path does not exist.
     */
    public byte getByte(@NotNull String path, byte orElse) {
        JsonElement element = get(path);
        return element.isJsonPrimitive() ? element.getAsByte() : orElse;
    }

    /**
     * Retrieves the integer value at the specified path in the json data.
     *
     * @param path The path to the integer value in the json data.
     * @return The integer value at the given path, or -1 if the path does not exist or the value is not an integer.
     */
    public int getInt(@NotNull String path) {
        JsonElement element = get(path);
        if (element.isJsonPrimitive()) return element.getAsInt();
        return -1;
    }

    /**
     * Retrieves the {@link Integer} at the specified path in the json data.
     * If no data is associated with the path, a fallback data value is returned.
     *
     * @param path   The path to the {@link Integer} in the json data.
     * @param orElse The fallback data to return if no data is associated with the path.
     * @return The {@link Integer} at the given path, or the {@code orElse} value if the path does not exist.
     */
    public int getInt(@NotNull String path, int orElse) {
        JsonElement element = get(path);
        return element.isJsonPrimitive() ? element.getAsInt() : orElse;
    }

    /**
     * Retrieves the long value at the specified path in the json data.
     *
     * @param path The path to the long value in the json data.
     * @return The long value at the given path, or -1 if the path does not exist or the value is not a long.
     */
    public long getLong(@NotNull String path) {
        return getLong(path, -1);
    }

    /**
     * Retrieves the {@link Long} at the specified path in the json data.
     * If no data is associated with the path, a fallback data value is returned.
     *
     * @param path   The path to the {@link Long} in the json data.
     * @param orElse The fallback data to return if no data is associated with the path.
     * @return The {@link Long} at the given path, or the {@code orElse} value if the path does not exist.
     */
    public long getLong(@NotNull String path, long orElse) {
        JsonElement element = get(path);
        return element.isJsonPrimitive() ? element.getAsLong() : orElse;
    }

    /**
     * Retrieves the double value at the specified path in the json data.
     *
     * @param path The path to the double value in the json data.
     * @return The double value at the given path, or -1 if the path does not exist or the value is not a double.
     */
    public double getDouble(@NotNull String path) {
        return getDouble(path, -1);
    }

    /**
     * Retrieves the {@link Double} at the specified path in the json data.
     * If no data is associated with the path, a fallback data value is returned.
     *
     * @param path   The path to the {@link Double} in the json data.
     * @param orElse The fallback data to return if no data is associated with the path.
     * @return The {@link Double} at the given path, or the {@code orElse} value if the path does not exist.
     */
    public double getDouble(@NotNull String path, double orElse) {
        JsonElement element = get(path);
        return element.isJsonPrimitive() ? element.getAsDouble() : orElse;
    }

    /**
     * Retrieves the float value at the specified path in the json data.
     *
     * @param path The path to the float value in the json data.
     * @return The float value at the given path, or -1 if the path does not exist or the value is not a float.
     */
    public float getFloat(@NotNull String path) {
        return getFloat(path, -1);
    }

    /**
     * Retrieves the {@link Float} at the specified path in the json data.
     * If no data is associated with the path, a fallback data value is returned.
     *
     * @param path   The path to the {@link Float} in the json data.
     * @param orElse The fallback data to return if no data is associated with the path.
     * @return The {@link Float} at the given path, or the {@code orElse} value if the path does not exist.
     */
    public float getFloat(@NotNull String path, float orElse) {
        JsonElement element = get(path);
        return element.isJsonPrimitive() ? element.getAsFloat() : orElse;
    }

    /**
     * Retrieves a list of {@link JsonElement} at the specified path in the json data.
     *
     * @param path The path to the list in the json data.
     * @return A Collection of {@link JsonElement} at the given path, or an empty list if the path does not exist.
     */
    public Collection<JsonElement> getList(@NotNull String path) {
        JsonElement element = get(path);
        if (element.isJsonArray())
            return element.getAsJsonArray().asList();
        return List.of();
    }

    /**
     * Retrieves a {@link Stream stream} of {@link JsonPrimitive json primitives} at the
     * specified path in the json data.
     *
     * @param path The path to the list of json primitives.
     * @return A {@link Stream stream} of {@link JsonPrimitive json primitives}.
     */
    private @NotNull Stream<JsonPrimitive> getJsonPrimitiveStream(@NotNull String path) {
        return getList(path).stream()
                .filter(JsonElement::isJsonPrimitive)
                .map(JsonElement::getAsJsonPrimitive);
    }

    /**
     * Retrieves a list of {@link Json} at the specified path in the json data.
     *
     * @param path The path to the list of strings in the json data.
     * @return A Collection of {@link Json} at the given path, or an empty list if the path does not exist.
     */
    public @NotNull @Unmodifiable Collection<Json> getJsonList(@NotNull String path) {
        return getList(path).stream().map(JsonParser::parse).toList();
    }

    /**
     * Retrieves a list of strings at the specified path in the json data.
     *
     * @param path The path to the list of strings in the json data.
     * @return A Collection of strings at the given path, or an empty list if the path does not exist or the value is not a json array of strings.
     */
    public @NotNull @Unmodifiable Collection<String> getStringList(@NotNull String path) {
        return getJsonPrimitiveStream(path).map(JsonElement::getAsString).toList();
    }

    /**
     * Retrieves a list of numbers at the specified path in the json data.
     *
     * @param path The path to the list of numbers in the json data.
     * @param type The type of number that is expected.
     * @param <T>  The representation of the type of number that is expected.
     * @return A Collection of numbers at the given path, or an empty list if the path does not exist or the value is not a json array of numbers.
     */
    @SuppressWarnings("unchecked")
    public <T extends Number> @NotNull @Unmodifiable Collection<T> getNumberList(@NotNull String path, @NotNull Class<T> type) {
        return getJsonPrimitiveStream(path)
                .map(JsonElement::getAsNumber)
                .map(number -> {
                    try {
                        return (T) number.getClass().getDeclaredMethod(type + "Value").invoke(number);
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }).toList();
    }

    /**
     * Retrieves a list of bytes at the specified path in the json data.
     *
     * @param path The path to the list of bytes in the json data.
     * @return A Collection of bytes at the given path, or an empty list if the path does not exist or the value is not a json array of bytes.
     */
    public @NotNull @Unmodifiable Collection<Byte> getByteList(@NotNull String path) {
        return getIntList(path).stream().map(Integer::byteValue).toList();
    }

    /**
     * Retrieves a list of integers at the specified path in the json data.
     *
     * @param path The path to the list of integers in the json data.
     * @return A Collection of integers at the given path, or an empty list if the path does not exist or the value is not a json array of integers.
     */
    public @NotNull @Unmodifiable Collection<Integer> getIntList(@NotNull String path) {
        return getNumberList(path, int.class);
    }

    /**
     * Retrieves a list of longs at the specified path in the json data.
     *
     * @param path The path to the list of longs in the json data.
     * @return A Collection of longs at the given path, or an empty list if the path does not exist or the value is not a json array of longs.
     */
    public @NotNull @Unmodifiable Collection<Long> getLongList(@NotNull String path) {
        return getNumberList(path, long.class);
    }

    /**
     * Retrieves a list of doubles at the specified path in the json data.
     *
     * @param path The path to the list of doubles in the json data.
     * @return A Collection of doubles at the given path, or an empty list if the path does not exist or the value is not a json array of doubles.
     */
    public @NotNull @Unmodifiable Collection<Double> getDoubleList(@NotNull String path) {
        return getNumberList(path, double.class);
    }

    /**
     * Retrieves a list of floats at the specified path in the json data.
     *
     * @param path The path to the list of floats in the json data.
     * @return A Collection of floats at the given path, or an empty list if the path does not exist or the value is not a json array of floats.
     */
    public @NotNull @Unmodifiable Collection<Float> getFloatList(@NotNull String path) {
        return getNumberList(path, float.class);
    }

    /**
     * Retrieves a list of booleans at the specified path in the json data.
     *
     * @param path The path to the list of booleans in the json data.
     * @return A Collection of booleans at the given path, or an empty list if the path does not exist or the value is not a json array of booleans.
     */
    public @NotNull @Unmodifiable Collection<Boolean> getBooleanList(@NotNull String path) {
        return getJsonPrimitiveStream(path).map(JsonElement::getAsBoolean).toList();
    }

    /**
     * Sets the value at the specified path in the json data.
     * If the path does not exist, it will be created.
     * If the given data is of a supported type (String, Integer, Long, Boolean, Double, Collection or Object[]),
     * the appropriate setter method will be called. Otherwise, the data will be converted to a string and set as a value.
     *
     * @param path The path where the value should be set in the json data.
     * @param data The data to be set at the given path.
     * @return The Json object with the updated value at the specified path.
     */
    public @NotNull Json set(@NotNull String path, @Nullable Object data) {
        synchronized (this) {
            if (data == null) set(path, JsonNull.INSTANCE);
            else if (data instanceof JsonElement jsonElement) setJson(path, jsonElement);
            else if (data instanceof Json json) setJson(path, json.getObject());
            else if (data instanceof String string) setJson(path, new JsonPrimitive(string));
            else if (data instanceof Number number) setJson(path, new JsonPrimitive(number));
            else if (data instanceof Boolean bool) setJson(path, new JsonPrimitive(bool));
            else if (data instanceof Character character) setJson(path, new JsonPrimitive(character));

            else if (data instanceof Collection<?> collection) setList(path, collection);
            else if (data instanceof Object[] array) setList(path, Arrays.asList(array));

            else setJson(path, new JsonPrimitive(data.toString()));
            return this;
        }
    }

    /**
     * Moves a json object if it exists to a desired output path.
     *
     * @param source The path from witch is copied from and afterward deleted.
     * @param target The desired output path (where the object is moved to).
     * @return The updated json object.
     * @since 3.8.4-SNAPSHOT
     */
    public @NotNull Json moveTo(@NotNull String source, @NotNull String target) {
        copyTo(source, target);
        return remove(source);
    }

    /**
     * Copys a json object if it exists to a desired output path.
     *
     * @param source The path from witch is copied from.
     * @param target The desired output path (where the object is copied to).
     * @return The updated json object.
     * @since 3.8.4-SNAPSHOT
     */
    public @NotNull Json copyTo(@NotNull String source, @NotNull String target) {
        if (!contains(source)) return this;
        return set(target, get(source));
    }

    /**
     * Sets a json element at the specified path in the json data.
     *
     * @param path The path where the string value should be set in the json data.
     * @param data The string data to be set at the given path.
     */
    private void setJson(@NotNull String path, @NotNull JsonElement data) {
        String[] args = path.split("(?<!\\\\)\\.");
        JsonElement destination = getParentOfPath(path, true);
        if (destination == null) return;

        // Handling for the last element in the path
        JsonElement last;
        String lastArg = args[args.length - 1].replace("\\.", ".");
        if (lastArg.startsWith("$")) {
            JsonArray array = destination.isJsonNull() ? new JsonArray() : destination.getAsJsonArray();
            int index = argumentToIndex(lastArg, array, true);
            if (index == array.size()) array.add(data);
            else array.set(index, data);
            last = array;
        } else {
            JsonObject obj = destination.isJsonNull() ? new JsonObject() : destination.getAsJsonObject();
            obj.add(lastArg, data);
            last = obj;
        }

        if (this.getObject().equals(JsonNull.INSTANCE))
            this.object = last;
    }

    /**
     * Converts an argument to its index representation while respecting the allowance of new indexes.
     *
     * @param arg      The argument as its string representation.
     * @param array    The json array for which the index is applied.
     * @param allowNew {@code true} if new creation of new indexes is supported, {@code false} otherwise.
     * @return The converted index.
     */
    private int argumentToIndex(@NotNull String arg, @NotNull JsonArray array, boolean allowNew) {
        try {
            if (arg.equalsIgnoreCase("$last")) return array.size() - 1;

            if (arg.equalsIgnoreCase("$new"))
                if (allowNew) return array.size();
                else throw new IllegalStateException("The $new argument is not allowed on this action!");

            return Integer.parseInt(arg.replace("$", ""));
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Not a valid array index! (" + arg + ")", e);
        }
    }

    /**
     * Initializes a json path by setting up a new json element based on the specified path.
     *
     * @param path    the path within the json structure to initialize, as a string
     * @param isArray {@code true} if the path should be initialized as a {@link JsonArray}, {@code false} for {@link JsonObject}
     * @return the newly created {@link JsonElement} (either {@link JsonArray} or {@link JsonObject}) at the specified path
     */
    private @NotNull JsonElement initPath(@NotNull String path, boolean isArray) {
        JsonElement element = isArray ? new JsonArray() : new JsonObject();
        if (getObject().isJsonNull()) this.object = element;
        else setJson(path, element);
        return element;
    }

    /**
     * Retrieves an existing property from a json object or creates a new one if it doesn't exist.
     *
     * @param obj      the {@link JsonObject} in which to retrieve or create the property
     * @param property the name of the property to retrieve or create
     * @param isArray  {@code true} if the property should be initialized as a {@link JsonArray}, {@code false} for {@link JsonObject}
     * @return the existing or newly created {@link JsonElement} (either {@link JsonArray} or {@link JsonObject})
     */
    private @NotNull JsonElement getOrCreate(@NotNull JsonObject obj, @NotNull String property, boolean isArray) {
        if (!obj.has(property)) {
            JsonElement newObject = isArray ? new JsonArray() : new JsonObject();
            obj.add(property, newObject);
            return newObject;
        }

        return obj.get(property);
    }

    /**
     * Retrieves or creates an element within a json array at the specified index.
     *
     * @param array   the {@link JsonArray} in which to retrieve or create the element
     * @param index   the index at which to retrieve or create the element
     * @param isArray {@code true} if the element should be initialized as a {@link JsonArray}, {@code false} for {@link JsonObject}
     * @return the existing or newly created {@link JsonElement} (either {@link JsonArray} or {@link JsonObject}) at the specified index
     */
    private @NotNull JsonElement getOrCreate(@NotNull JsonArray array, int index, boolean isArray) {
        JsonElement element = index >= array.size() ? JsonNull.INSTANCE : array.get(index);

        if (element.isJsonNull()) {
            element = isArray ? new JsonArray() : new JsonObject();

            if (index == array.size()) array.add(element);
            else array.set(index, element);
        }

        return element;
    }

    /**
     * Sets a collection of values at the specified path in the json data.
     * If the collection is empty, an empty json array will be set at the path.
     * For each element in the collection, the appropriate setter method will be called based on its type.
     * If an element in the collection is not of a supported type (String, Integer, Long, Boolean, Double),
     * it will be converted to a string and added to the json array.
     *
     * @param path       The path where the collection should be set in the json data.
     * @param collection The collection of values to be set at the given path.
     */
    @SuppressWarnings("unchecked")
    private void setList(@NotNull String path, @Nullable Collection<?> collection) {
        if (collection == null || collection.isEmpty()) {
            setEmptyList(path);
            return;
        }

        for (Object o : collection) {
            if (o instanceof JsonElement) setJsonList(path, (Collection<JsonElement>) collection);
            else if (o instanceof Json) setJsonList(path, ((Collection<Json>) collection).stream().map(Json::getObject).toList());
            else if (o instanceof String) setPrimitiveList(path, ((Collection<String>) collection).stream().map(JsonPrimitive::new).toList());
            else if (o instanceof Number) setPrimitiveList(path, ((Collection<Number>) collection).stream().map(JsonPrimitive::new).toList());
            else if (o instanceof Boolean) setPrimitiveList(path, ((Collection<Boolean>) collection).stream().map(JsonPrimitive::new).toList());
            else if (o instanceof Character) setPrimitiveList(path, ((Collection<Character>) collection).stream().map(JsonPrimitive::new).toList());
            else setPrimitiveList(path, collection.stream().map(Object::toString).map(JsonPrimitive::new).toList());

            break;
        }
    }

    /**
     * Sets an empty json array at the specified path in the json data.
     *
     * @param path The path where the empty json array should be set.
     */
    private void setEmptyList(@NotNull String path) {
        setJson(path, new JsonArray());
    }

    /**
     * Sets a collection of {@link JsonElement} at the specified path in the json data.
     *
     * @param path       The path where the {@link JsonElement} collection should be set in the json data.
     * @param collection The collection of {@link JsonElement} to be set at the given path.
     */
    private void setJsonList(@NotNull String path, @NotNull Collection<JsonElement> collection) {
        JsonArray array = new JsonArray();
        for (JsonElement item : collection) array.add(item);
        setJson(path, array);
    }

    /**
     * Sets a collection of {@link JsonPrimitive} at the specified path in the json data.
     *
     * @param path       The path where the {@link JsonPrimitive} collection should be set in the json data.
     * @param collection The collection of {@link JsonPrimitive} to be set at the given path.
     * @since 3.8.11
     */
    private void setPrimitiveList(@NotNull String path, @NotNull Collection<JsonPrimitive> collection) {
        JsonArray array = new JsonArray();
        collection.forEach(array::add);
        setJson(path, array);
    }

    /**
     * Returns a set of all keys stored in the stored json object.
     *
     * @return The set containing all the keys.
     */
    public @NotNull Set<String> keySet() {
        return keySet("");
    }

    /**
     * Returns a set of all keys stored in the json data under the specific path.
     *
     * @param path The path for which the keys should be loaded.
     * @return The set containing all the keys.
     */
    public @NotNull Set<String> keySet(@NotNull String path) {
        JsonElement element = get(path);
        if (element.isJsonNull()) throw new NullPointerException("No such json element under " + path);
        if (!element.isJsonObject()) throw new IllegalStateException("Can not retrieve the keys of " + element.getClass().getSimpleName());
        return element.getAsJsonObject().keySet();
    }

    /**
     * Returns a set of all values stored in the stored json object.
     *
     * @return The set containing all the values.
     */
    public @NotNull Set<JsonElement> values() {
        return values("");
    }

    /**
     * Returns a set of all values stored in the json data under the specific path.
     *
     * @param path The path for which the values should be loaded.
     * @return The set containing all the values.
     */
    public @NotNull Set<JsonElement> values(@NotNull String path) {
        JsonElement element = get(path);
        if (element.isJsonNull()) throw new NullPointerException("No such json element under " + path);

        if (element.isJsonObject())
            return element.getAsJsonObject().entrySet().parallelStream().map(Map.Entry::getValue).collect(Collectors.toSet());
        if (element.isJsonArray())
            return new HashSet<>(element.getAsJsonArray().asList());

        throw new IllegalStateException("Can not retrieve the values of " + element.getClass().getSimpleName());
    }

    /**
     * Retrieves the size of the root json object or array.
     *
     * @return the size of the root json object or array
     * @throws NullPointerException  if the root json element does not exist
     * @throws IllegalStateException if the root element is neither a json object nor a json array
     */
    public int size() {
        return size("");
    }

    /**
     * Retrieves the size of the json object or array at the specified path.
     *
     * @param path the path within the json structure to the target element
     * @return the size of the json object or array located at the specified path
     * @throws NullPointerException  if there is no json element at the specified path
     * @throws IllegalStateException if the target element is neither a json object nor a json array
     */
    public int size(@NotNull String path) {
        JsonElement element = get(path);
        if (element.isJsonNull()) throw new NullPointerException("No such json element under " + path);

        if (element.isJsonObject()) return element.getAsJsonObject().size();
        if (element.isJsonArray()) return element.getAsJsonArray().size();
        if (element.isJsonNull()) return 0;

        throw new IllegalStateException("Can not retrieve the values of " + element.getClass().getSimpleName());
    }

    /**
     * Saves the json data to the specified path.
     *
     * @param path The path where the json data should be saved.
     */
    public void save(@NotNull Path path) {
        this.save(path, false);
    }

    /**
     * Saves the json data to the specified path.
     *
     * @param path   The path where the json data should be saved.
     * @param pretty Sets whether the output should be pretty formated or not.
     */
    public void save(@NotNull Path path, boolean pretty) {
        this.save(path, pretty ? PRETTY_GSON : GSON);
    }

    /**
     * Saves the json data to the specified path.
     *
     * @param path The path where the json data should be saved.
     * @param gson The {@link Gson} instance which is used to stringify the json.
     * @since 3.8.10
     */
    public void save(@NotNull Path path, @NotNull Gson gson) {
        try {
            synchronized (this) {
                Files.writeString(path, toString(gson), StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not save the json to the file system!", e);
        }
    }

    /**
     * Saves the json data to the specified file.
     *
     * @param file The file where the json data should be saved.
     */
    public void save(@NotNull File file) {
        save(file, false);
    }

    /**
     * Saves the json data to the specified file.
     *
     * @param file   The file where the json data should be saved.
     * @param pretty Sets whether the output should be pretty formated or not.
     */
    public void save(@NotNull File file, boolean pretty) {
        this.save(file, pretty ? PRETTY_GSON : GSON);
    }

    /**
     * Saves the json data to the specified file.
     *
     * @param file The file where the json data should be saved.
     * @param gson The {@link Gson} instance which is used to stringify the json.
     * @since 3.8.10
     */
    public void save(@NotNull File file, @NotNull Gson gson) {
        this.save(file.toPath(), gson);
    }

    /**
     * Returns the json data as a json string.
     *
     * @return The json data as a json string.
     */
    @Override
    public @NotNull String toString() {
        return this.toString(false);
    }

    /**
     * Returns the json data as a json string.
     *
     * @param pretty Sets whether the return should be pretty formated or not.
     * @return The json data as a json string.
     */
    public @NotNull String toString(boolean pretty) {
        return this.toString(pretty ? PRETTY_GSON : GSON);
    }

    /**
     * Returns the json data as a json string.
     *
     * @param gson The {@link Gson} instance which is used to stringify the json.
     * @return The json data as a json string.
     * @since 3.8.10
     */
    public @NotNull String toString(@NotNull Gson gson) {
        try {
            return gson.toJson(this.getObject());
        } catch (Exception e) {
            throw new RuntimeException("Could not stringify the json!", e);
        }
    }

    /**
     * Returns the underlying JsonElement representing the json data.
     *
     * @return The JsonElement representing the json data.
     */
    public @NotNull JsonElement getObject() {
        return object;
    }

    /**
     * Returns a new and empty Json Class
     *
     * @return The new Json class
     */
    public static @NotNull Json empty() {
        return new Json(JsonNull.INSTANCE);
    }

}
