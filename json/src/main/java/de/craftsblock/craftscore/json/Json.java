package de.craftsblock.craftscore.json;

import com.google.gson.*;

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
 * @version 2.1.0
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
    private static GsonBuilder newGsonBuilder() {
        return new GsonBuilder().serializeNulls().disableHtmlEscaping();
    }

    private JsonElement object;

    /**
     * Constructs a new Json object with the given JsonElement.
     *
     * @param object The underlying JsonElement to work with.
     */
    public Json(JsonElement object) {
        this.object = object;
    }

    /**
     * Checks if the json data contains a value at the specified path.
     *
     * @param path The path to the value in the json data.
     * @return true if the value exists at the path, false otherwise.
     */
    public boolean contains(String path) {
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
    public Json remove(String path) {
        String[] args = path.split("(?<!\\\\)\\.");
        JsonElement target = getParentOfPath(path, false);

        if (target == null) return this;

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
    public <T> T deserialize(String path, Class<T> classOfT) {
        if (contains(path)) return GSON.fromJson(get(path), classOfT);
        return null;
    }

    /**
     * Serializes the provided data and sets it at the specified path in the json data.
     *
     * @param path The path to set the serialized data in the json data.
     * @param data The data to be serialized and set at the path.
     * @return The Json object itself after setting the data at the path.
     */
    public <T> Json serialize(String path, T data) {
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
    private JsonElement getParentOfPath(String path, boolean initIfMissing) {
        String[] segments = path.split("(?<!\\\\)\\.");
        JsonElement current = getObject();
        StringBuilder processedPath = new StringBuilder();

        for (int i = 0; i < segments.length - 1; i++) {
            String key = segments[i].replace("\\.", ".");
            boolean isArray = key.startsWith("$");

            if (current == null || current.isJsonNull())
                if (initIfMissing)
                    current = initPath(processedPath.toString(), isArray);
                else return null;

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
    public JsonElement get(String path) {
        String[] args = path.split("(?<!\\\\)\\.");
        if (args.length == 0) return getObject();

        JsonElement destination = getParentOfPath(path, false);
        if (destination == null || destination.isJsonNull()) return destination;

        String arg = args[args.length - 1].replace("\\.", ".");
        if (destination.isJsonObject()) return destination.getAsJsonObject().get(arg);
        if (destination.isJsonArray()) return destination.getAsJsonArray().get(argumentToIndex(arg, destination.getAsJsonArray(), false));

        return destination;
    }

    /**
     * Retrieves the JsonElement at the specified path in the json data.
     * If no data is associated with the path, a fallback data value is returned.
     *
     * @param path     The path to the JsonElement in the json data.
     * @param fallback The fallback data to return if no data is associated with the path
     * @return The JsonElement at the given path, or null if the path does not exist.
     */
    public JsonElement getOrDefault(String path, JsonElement fallback) {
        JsonElement element = get(path);
        return element != null ? element : fallback;
    }

    /**
     * Retrieves the JsonElement at the specified path in the json data and returns it as a new instance of {@link Json}.
     *
     * @param path The path to the data in the json data.
     * @return The {@link Json} at the given path, or null if the path does not exist.
     */
    public Json getJson(String path) {
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
    public Json getJson(String path, Json fallback) {
        JsonElement element = get(path);
        return element != null ? JsonParser.parse(element) : fallback;
    }

    /**
     * Retrieves the String value at the specified path in the json data.
     *
     * @param path The path to the String value in the json data.
     * @return The String value at the given path, or an empty string if the path does not exist or the value is not a string.
     */
    public String getString(String path) {
        JsonElement element = get(path);
        if (element != null && element.isJsonPrimitive()) return element.getAsString();
        return "";
    }

    /**
     * Retrieves the byte value at the specified path in the json data.
     *
     * @param path The path to the byte value in the json data.
     * @return The byte value at the given path, or -1 if the path does not exist or the value is not a byte.
     */
    public byte getByte(String path) {
        JsonElement element = get(path);
        if (element != null && element.isJsonPrimitive()) return element.getAsByte();
        return -1;
    }

    /**
     * Retrieves the integer value at the specified path in the json data.
     *
     * @param path The path to the integer value in the json data.
     * @return The integer value at the given path, or -1 if the path does not exist or the value is not an integer.
     */
    public int getInt(String path) {
        JsonElement element = get(path);
        if (element != null && element.isJsonPrimitive()) return element.getAsInt();
        return -1;
    }

    /**
     * Retrieves the long value at the specified path in the json data.
     *
     * @param path The path to the long value in the json data.
     * @return The long value at the given path, or -1 if the path does not exist or the value is not a long.
     */
    public long getLong(String path) {
        JsonElement element = get(path);
        if (element != null && element.isJsonPrimitive()) return element.getAsLong();
        return -1;
    }

    /**
     * Retrieves the boolean value at the specified path in the json data.
     *
     * @param path The path to the boolean value in the json data.
     * @return The boolean value at the given path, or false if the path does not exist or the value is not a boolean.
     */
    public boolean getBoolean(String path) {
        JsonElement element = get(path);
        if (element != null && element.isJsonPrimitive()) return element.getAsBoolean();
        return false;
    }

    /**
     * Retrieves the double value at the specified path in the json data.
     *
     * @param path The path to the double value in the json data.
     * @return The double value at the given path, or -1 if the path does not exist or the value is not a double.
     */
    public double getDouble(String path) {
        JsonElement element = get(path);
        if (element != null && element.isJsonPrimitive()) return element.getAsDouble();
        return -1;
    }

    /**
     * Retrieves the float value at the specified path in the json data.
     *
     * @param path The path to the float value in the json data.
     * @return The float value at the given path, or -1 if the path does not exist or the value is not a float.
     */
    public float getFloat(String path) {
        JsonElement element = get(path);
        if (element != null && element.isJsonPrimitive()) return element.getAsFloat();
        return -1;
    }

    /**
     * Retrieves a list of {@link JsonElement} at the specified path in the json data.
     *
     * @param path The path to the list in the json data.
     * @return A Collection of {@link JsonElement} at the given path, or an empty list if the path does not exist.
     */
    public Collection<JsonElement> getList(String path) {
        JsonElement element = get(path);
        if (element != null && element.isJsonArray())
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
    private Stream<JsonPrimitive> getJsonPrimitiveStream(String path) {
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
    public Collection<Json> getJsonList(String path) {
        return getList(path).stream().map(JsonParser::parse).toList();
    }

    /**
     * Retrieves a list of strings at the specified path in the json data.
     *
     * @param path The path to the list of strings in the json data.
     * @return A Collection of strings at the given path, or an empty list if the path does not exist or the value is not a json array of strings.
     */
    public Collection<String> getStringList(String path) {
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
    public <T extends Number> Collection<T> getNumberList(String path, Class<T> type) {
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
    public Collection<Byte> getByteList(String path) {
        return getIntList(path).stream().map(Integer::byteValue).toList();
    }

    /**
     * Retrieves a list of integers at the specified path in the json data.
     *
     * @param path The path to the list of integers in the json data.
     * @return A Collection of integers at the given path, or an empty list if the path does not exist or the value is not a json array of integers.
     */
    public Collection<Integer> getIntList(String path) {
        return getNumberList(path, int.class);
    }

    /**
     * Retrieves a list of longs at the specified path in the json data.
     *
     * @param path The path to the list of longs in the json data.
     * @return A Collection of longs at the given path, or an empty list if the path does not exist or the value is not a json array of longs.
     */
    public Collection<Long> getLongList(String path) {
        return getNumberList(path, long.class);
    }

    /**
     * Retrieves a list of doubles at the specified path in the json data.
     *
     * @param path The path to the list of doubles in the json data.
     * @return A Collection of doubles at the given path, or an empty list if the path does not exist or the value is not a json array of doubles.
     */
    public Collection<Double> getDoubleList(String path) {
        return getNumberList(path, double.class);
    }

    /**
     * Retrieves a list of floats at the specified path in the json data.
     *
     * @param path The path to the list of floats in the json data.
     * @return A Collection of floats at the given path, or an empty list if the path does not exist or the value is not a json array of floats.
     */
    public Collection<Float> getFloatList(String path) {
        return getNumberList(path, float.class);
    }

    /**
     * Retrieves a list of booleans at the specified path in the json data.
     *
     * @param path The path to the list of booleans in the json data.
     * @return A Collection of booleans at the given path, or an empty list if the path does not exist or the value is not a json array of booleans.
     */
    public Collection<Boolean> getBooleanList(String path) {
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
    public Json set(String path, Object data) {
        synchronized (this) {
            if (data instanceof JsonElement) setJson(path, (JsonElement) data);
            else if (data instanceof Json) setJson(path, ((Json) data).getObject());
            else if (data instanceof String) setString(path, (String) data);
            else if (data instanceof Number) setNumber(path, (Number) data);
            else if (data instanceof Boolean) setBoolean(path, (boolean) data);
            else if (data instanceof Collection<?>) setList(path, (Collection<?>) data);
            else if (data instanceof Object[]) setList(path, Arrays.asList((Object[]) data));
            else setString(path, data.toString());
            return this;
        }
    }

    /**
     * Moves a json object if it exists to a desired output path.
     *
     * @param source The path from witch is copied from and afterward deleted
     * @param target The desired output path (where the object is moved to)
     * @since 3.8.4-SNAPSHOT
     */
    public Json moveTo(String source, String target) {
        copyTo(source, target);
        return remove(source);
    }

    /**
     * Copys a json object if it exists to a desired output path.
     *
     * @param source The path from witch is copied from
     * @param target The desired output path (where the object is copied to)
     * @since 3.8.4-SNAPSHOT
     */
    public Json copyTo(String source, String target) {
        if (!contains(source)) return this;
        return set(target, get(source));
    }

    /**
     * Sets a json element at the specified path in the json data.
     *
     * @param path The path where the string value should be set in the json data.
     * @param data The string data to be set at the given path.
     */
    private void setJson(String path, JsonElement data) {
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
    private int argumentToIndex(String arg, JsonArray array, boolean allowNew) {
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
    private JsonElement initPath(String path, boolean isArray) {
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
    private JsonElement getOrCreate(JsonObject obj, String property, boolean isArray) {
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
    private JsonElement getOrCreate(JsonArray array, int index, boolean isArray) {
        JsonElement element = index >= array.size() ? JsonNull.INSTANCE : array.get(index);

        if (element.isJsonNull()) {
            element = isArray ? new JsonArray() : new JsonObject();

            if (index == array.size()) array.add(element);
            else array.set(index, element);
        }

        return element;
    }

    /**
     * Sets a string value at the specified path in the json data.
     *
     * @param path The path where the string value should be set in the json data.
     * @param data The string data to be set at the given path.
     */
    private void setString(String path, String data) {
        setJson(path, new JsonPrimitive(data));
    }

    /**
     * Sets a number value at the specified path in the json data.
     *
     * @param path The path where the string value should be set in the json data.
     * @param data The string data to be set at the given path.
     */
    private void setNumber(String path, Number data) {
        setJson(path, new JsonPrimitive(data));
    }

    /**
     * Sets a boolean value at the specified path in the json data.
     *
     * @param path The path where the boolean value should be set in the json data.
     * @param data The boolean data to be set at the given path.
     */
    private void setBoolean(String path, boolean data) {
        setJson(path, new JsonPrimitive(data));
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
    private void setList(String path, Collection<?> collection) {
        assert collection != null;
        if (collection.isEmpty()) setEmptyList(path);
        for (Object o : collection) {
            if (o instanceof JsonElement) setJsonList(path, (Collection<JsonElement>) collection);
            else if (o instanceof Json) setJsonList(path, ((Collection<Json>) collection).stream().map(Json::getObject).toList());
            else if (o instanceof String) setStringList(path, (Collection<String>) collection);
            else if (o instanceof Number) setNumberList(path, (Collection<Number>) collection);
            else if (o instanceof Boolean) setBoolList(path, (Collection<Boolean>) collection);
            else setStringList(path, collection.stream().map(Object::toString).toList());

            break;
        }
    }

    /**
     * Sets an empty json array at the specified path in the json data.
     *
     * @param path The path where the empty json array should be set.
     */
    private void setEmptyList(String path) {
        setJson(path, new JsonArray());
    }

    /**
     * Sets a collection of {@link JsonElement} at the specified path in the json data.
     *
     * @param path       The path where the string collection should be set in the json data.
     * @param collection The collection of {@link JsonElement} to be set at the given path.
     */
    private void setJsonList(String path, Collection<JsonElement> collection) {
        JsonArray array = new JsonArray();
        for (JsonElement item : collection) array.add(item);
        setJson(path, array);
    }

    /**
     * Sets a collection of string values at the specified path in the json data.
     *
     * @param path       The path where the string collection should be set in the json data.
     * @param collection The collection of string values to be set at the given path.
     */
    private void setStringList(String path, Collection<String> collection) {
        JsonArray array = new JsonArray();
        for (String item : collection) array.add(item);
        setJson(path, array);
    }


    /**
     * Sets a collection of number values at the specified path in the json data.
     *
     * @param path       The path where the number collection should be set in the json data.
     * @param collection The collection of number values to be set at the given path.
     */
    private void setNumberList(String path, Collection<Number> collection) {
        JsonArray array = new JsonArray();
        for (Number item : collection) array.add(item);
        setJson(path, array);
    }

    /**
     * Sets a collection of boolean values at the specified path in the json data.
     *
     * @param path       The path where the boolean collection should be set in the json data.
     * @param collection The collection of boolean values to be set at the given path.
     */
    private void setBoolList(String path, Collection<Boolean> collection) {
        JsonArray array = new JsonArray();
        for (boolean item : collection) array.add(item);
        setJson(path, array);
    }

    /**
     * Returns a set of all keys stored in the stored json object.
     *
     * @return The set containing all the keys.
     */
    public Set<String> keySet() {
        return keySet("");
    }

    /**
     * Returns a set of all keys stored in the json data under the specific path.
     *
     * @param path The path for which the keys should be loaded.
     * @return The set containing all the keys.
     */
    public Set<String> keySet(String path) {
        JsonElement element = get(path);
        if (element == null) throw new NullPointerException("No such json element under " + path);
        if (!element.isJsonObject()) throw new IllegalStateException("Can not retrieve the keys of " + element.getClass().getSimpleName());
        return element.getAsJsonObject().keySet();
    }

    /**
     * Returns a set of all values stored in the stored json object.
     *
     * @return The set containing all the values.
     */
    public Set<JsonElement> values() {
        return values("");
    }

    /**
     * Returns a set of all values stored in the json data under the specific path.
     *
     * @param path The path for which the values should be loaded.
     * @return The set containing all the values.
     */
    public Set<JsonElement> values(String path) {
        JsonElement element = get(path);
        if (element == null) throw new NullPointerException("No such json element under " + path);

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
    public int size(String path) {
        JsonElement element = get(path);
        if (element == null) throw new NullPointerException("No such json element under " + path);

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
    public void save(Path path) {
        save(path, false);
    }

    /**
     * Saves the json data to the specified path.
     *
     * @param path   The path where the json data should be saved.
     * @param pretty Sets whether the output should be pretty formated or not.
     */
    public void save(Path path, boolean pretty) {
        try {
            synchronized (this) {
                Files.writeString(path, toString(pretty), StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Saves the json data to the specified file.
     *
     * @param file The file where the json data should be saved.
     */
    public void save(File file) {
        save(file, false);
    }

    /**
     * Saves the json data to the specified file.
     *
     * @param file   The file where the json data should be saved.
     * @param pretty Sets whether the output should be pretty formated or not.
     */
    public void save(File file, boolean pretty) {
        this.save(file.toPath(), pretty);
    }

    /**
     * Returns the json data as a json string.
     *
     * @return The json data as a json string.
     */
    @Override
    public String toString() {
        return toString(false);
    }

    /**
     * Returns the json data as a json string.
     *
     * @param pretty Sets whether the return should be pretty formated or not.
     * @return The json data as a json string.
     */
    public String toString(boolean pretty) {
        try {
            return (pretty ? PRETTY_GSON : GSON).toJson(getObject());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the underlying JsonElement representing the json data.
     *
     * @return The JsonElement representing the json data.
     */
    public JsonElement getObject() {
        return object;
    }

    /**
     * Returns a new and empty Json Class
     *
     * @return The new Json class
     */
    public static Json empty() {
        return new Json(JsonNull.INSTANCE);
    }

}
