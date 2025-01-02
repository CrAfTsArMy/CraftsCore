package de.craftsblock.craftscore.json;

import com.google.gson.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The Json class represents a json object and provides methods to work with json data.
 *
 * @author Philipp Maywald
 * @author CraftsBlock
 * @version 2.0.6
 * @see JsonParser
 * @since 3.6#16-SNAPSHOT
 */
public final class Json {

    private static final Gson GSON = new Gson();
    private static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();


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
        path = path.replace("\\.", "&dot;");
        String[] args = path.split("\\.");
        JsonElement temp = getObject();

        for (String arg : args) {
            String s = arg.replace("&dot;", ".");

            if (temp.isJsonArray()) {
                if (!s.startsWith("$"))
                    throw new IllegalStateException("The index of an array must start with an $ char!");

                JsonArray array = temp.getAsJsonArray();
                int index = argumentToIndex(arg, array, false);

                if (index >= array.size()) return false;
                temp = temp.getAsJsonArray().get(index);
            } else if (temp.isJsonObject()) {
                JsonObject curr = temp.getAsJsonObject();
                if (curr.has(s)) temp = curr.get(s);
                else return false;
            } else throw new IllegalStateException("The value for the key \"" + s + "\" is not a json object or array!");
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
        path = path.replace("\\.", "&dot;");
        String[] args = path.split("\\.");
        JsonElement temp = getObject();

        for (int i = 0; i < args.length - 1; i++) {
            String arg = args[i].replace("&dot;", ".");

            if (temp.isJsonArray()) {
                if (!arg.startsWith("$"))
                    throw new IllegalStateException("The index of an array must start with an $ char!");

                JsonArray array = temp.getAsJsonArray();
                int index = argumentToIndex(arg, array, false);

                if (index >= array.size()) return this;
                temp = temp.getAsJsonArray().get(index);
            } else if (temp.isJsonObject()) {
                JsonObject curr = temp.getAsJsonObject();
                if (curr.has(arg)) temp = curr.get(arg);
                else return this;
            } else throw new IllegalStateException("The value for the key \"" + arg + "\" is not a json object or array!");
        }

        String lastArg = args[args.length - 1].replace("&dot;", ".");
        if (temp.isJsonArray()) {
            JsonArray array = temp.getAsJsonArray();
            int index = argumentToIndex(lastArg, array, false);

            if (index >= array.size()) return this;
            array.remove(index);
        } else if (temp.isJsonObject())
            temp.getAsJsonObject().remove(lastArg);
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
        if (json == null) return this;
        set(path, json.getObject());
        return this;
    }

    /**
     * Retrieves the JsonElement at the specified path in the json data.
     *
     * @param path The path to the JsonElement in the json data.
     * @return The JsonElement at the given path, or null if the path does not exist.
     */
    public JsonElement get(String path) {
        path = path.replace("\\.", "&dot;");
        String[] args = path.split("\\.");
        JsonElement temp = getObject();

        if (!path.isEmpty() && !path.isBlank())
            for (String arg : args) {
                String s = arg.replace("&dot;", ".");

                if (temp.isJsonArray()) {
                    if (!s.startsWith("$"))
                        throw new IllegalStateException("The index of an array must start with an $ char!");

                    JsonArray array = temp.getAsJsonArray();
                    int index = argumentToIndex(arg, array, false);

                    temp = temp.getAsJsonArray().get(index);
                } else if (temp.isJsonObject()) {
                    JsonObject curr = temp.getAsJsonObject();
                    if (curr.has(s)) temp = curr.get(s);
                    else return null;
                } else throw new IllegalStateException("The value for the key \"" + s + "\" is not a json object or array!");
            }

        return temp;
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
     * Retrieves a list of {@link JsonElement} at the specified path in the json data.
     *
     * @param path The path to the list of strings in the json data.
     * @return A Collection of {@link JsonElement} at the given path, or an empty list if the path does not exist.
     */
    public Collection<JsonElement> getList(String path) {
        JsonElement element = get(path);
        if (element != null && element.isJsonArray())
            return new ArrayList<>(element.getAsJsonArray().asList());
        return new ArrayList<>();
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
        ArrayList<String> list = new ArrayList<>();

        for (JsonElement arrayElement : getList(path))
            if (arrayElement.isJsonPrimitive()) list.add(arrayElement.getAsString());

        return list;
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
        Collection<T> list = new ArrayList<>();

        for (JsonElement arrayElement : getList(path))
            if (arrayElement.isJsonPrimitive())
                try {
                    Number num = arrayElement.getAsNumber();
                    list.add((T) num.getClass().getDeclaredMethod(type + "Value").invoke(num));
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }

        return list;
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
     * Retrieves a list of booleans at the specified path in the json data.
     *
     * @param path The path to the list of booleans in the json data.
     * @return A Collection of booleans at the given path, or an empty list if the path does not exist or the value is not a json array of booleans.
     */
    public Collection<Boolean> getBooleanList(String path) {
        JsonElement element = get(path);
        if (element != null && element.isJsonArray()) {
            Collection<Boolean> list = new ArrayList<>();
            for (JsonElement arrayElement : element.getAsJsonArray())
                if (arrayElement.isJsonPrimitive()) list.add(arrayElement.getAsBoolean());
            return list;
        }
        return new ArrayList<>();
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
            path = path.replace("\\.", "&dot;");
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
     * Sets a json element at the specified path in the json data.
     *
     * @param path The path where the string value should be set in the json data.
     * @param data The string data to be set at the given path.
     */
    private void setJson(String path, JsonElement data) {
        String[] args = path.split("\\.");
        JsonElement destination = getObject();
        StringBuilder processedPath = new StringBuilder();

        for (int i = 0; i < args.length - 1; i++) {
            String arg = args[i].replace("&dot;", ".");
            boolean array = arg.startsWith("$");
            if (destination.isJsonNull())
                destination = initPath(processedPath.toString(), array);

            String nextArg = args[i + 1];
            if (array) {
                int index = argumentToIndex(arg, destination.getAsJsonArray(), true);
                destination = getOrCreate(destination.getAsJsonArray(), index, nextArg.startsWith("$"));
                processedPath.append("$").append(index);
            } else {
                destination = getOrCreate(destination.getAsJsonObject(), arg, nextArg.startsWith("$"));
                processedPath.append(arg);
            }

            if (i < args.length - 2) processedPath.append(".");
        }

        // Handling for the last element in the path
        JsonElement last;
        String lastArg = args[args.length - 1].replace("&dot;", ".");
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
            else if (arg.equalsIgnoreCase("$new") && allowNew) return array.size();
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
     * @throws IllegalStateException if the root element is neither a JSON object nor a JSON array
     */
    public int size() {
        return size("");
    }

    /**
     * Retrieves the size of the json object or array at the specified path.
     *
     * @param path the path within the json structure to the target element
     * @return the size of the json object or array located at the specified path
     * @throws NullPointerException  if there is no JSON element at the specified path
     * @throws IllegalStateException if the target element is neither a JSON object nor a JSON array
     */
    public int size(String path) {
        JsonElement element = get(path);
        if (element == null) throw new NullPointerException("No such json element under " + path);

        if (element.isJsonObject()) return element.getAsJsonObject().size();
        if (element.isJsonArray()) return element.getAsJsonArray().size();

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
            Files.writeString(path, toString(pretty), StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Saves the json data to the specified file.
     *
     * @param f The file where the json data should be saved.
     */
    public void save(File f) {
        save(f, false);
    }

    /**
     * Saves the json data to the specified file.
     *
     * @param f      The file where the json data should be saved.
     * @param pretty Sets whether the output should be pretty formated or not.
     */
    public void save(File f, boolean pretty) {
        synchronized (this) {
            try {
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(f));
                bufferedWriter.write(toString(pretty));
                bufferedWriter.flush();
                bufferedWriter.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
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
        synchronized (this) {
            try {
                return (pretty ? PRETTY_GSON : GSON).toJson(getObject());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
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
