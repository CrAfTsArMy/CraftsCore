package de.craftsblock.craftscore.json;

import com.google.gson.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * The Json class represents a JSON object and provides methods to work with JSON data.
 *
 * @author CraftsBlock
 * @version 2.0
 * @see JsonParser
 * @since 3.6#16-SNAPSHOT
 */
public final class Json {

    private final JsonObject object;
    private final Gson gson;

    /**
     * Constructs a new Json object with the given JsonObject.
     *
     * @param object The underlying JsonObject to work with.
     */
    public Json(JsonObject object) {
        this.object = object;
        this.gson = new Gson();
    }

    /**
     * Checks if the JSON data contains a value at the specified path.
     *
     * @param path The path to the value in the JSON data.
     * @return true if the value exists at the path, false otherwise.
     */
    public boolean contains(String path) {
        String[] args = path.split("\\.");
        JsonObject temp = getObject();
        for (int i = 0; i < args.length - 1; i++) {
            String s = args[i].replace("&dot;", ".");

            if (!temp.has(s)) return false;
            if (i == args.length - 1) return true;

            JsonElement element = temp.get(s);
            if (element.isJsonObject()) temp = element.getAsJsonObject();
            else return false;
        }
        String object = args[args.length - 1].replace("&dot;", ".");
        return temp.has(object);
    }

    /**
     * Removes a value from the JSON data at the specified path.
     *
     * @param path The path to the value to remove from the JSON data.
     * @return The Json object itself after removing the value.
     */
    public Json remove(String path) {
        String[] args = path.split("\\.");
        JsonObject temp = getObject();
        for (int i = 0; i < args.length - 1; i++) {
            String s = args[i].replace("&dot;", ".");
            if (!temp.has(s)) temp.add(s, new JsonObject());
            JsonElement element = temp.get(s);
            if (!element.isJsonObject()) {
                temp.remove(s);
                temp.add(s, new JsonObject());
            }
            temp = element.getAsJsonObject();
        }
        String object = args[args.length - 1].replace("&dot;", ".");
        temp.remove(object);
        return this;
    }

    /**
     * Deserializes the JSON data at the specified path to an object of the specified class.
     *
     * @param path     The path to the JSON data in the JSON object.
     * @param classOfT The class to deserialize the JSON data into.
     * @param <T>      The type of the deserialized object.
     * @return The deserialized object of the specified class, or null if the path does not exist.
     */
    public <T> T deserialize(String path, Class<T> classOfT) {
        path = path.replace("\\.", "&dot;");
        String[] args = path.split("\\.");
        JsonObject temp = getObject();
        for (int i = 0; i < args.length - 1; i++) {
            String s = args[i].replace("&dot;", ".");
            if (temp.has(s)) temp = temp.getAsJsonObject(s);
            else return null;
        }
        String object = args[args.length - 1].replace("&dot;", ".");
        if (temp.has(object)) return gson.fromJson(temp.get(object), classOfT);
        return null;
    }

    /**
     * Serializes the provided data and sets it at the specified path in the JSON data.
     *
     * @param path The path to set the serialized data in the JSON data.
     * @param data The data to be serialized and set at the path.
     * @return The Json object itself after setting the data at the path.
     */
    public <T> Json serialize(String path, T data) {
        Json json = JsonParser.parse(gson.toJson(data));
        if (json == null) return this;
        set(path, json.getObject());
        return this;
    }

    /**
     * Retrieves the JsonElement at the specified path in the JSON data.
     *
     * @param path The path to the JsonElement in the JSON data.
     * @return The JsonElement at the given path, or null if the path does not exist.
     */
    public JsonElement get(String path) {
        path = path.replace("\\.", "&dot;");
        String[] args = path.split("\\.");
        JsonObject temp = getObject();
        for (int i = 0; i < args.length - 1; i++) {
            String s = args[i].replace("&dot;", ".");
            if (temp.has(s)) temp = temp.getAsJsonObject(s);
            else return null;
        }
        String object = args[args.length - 1].replace("&dot;", ".");
        if (temp.has(object)) return temp.get(object);
        return null;
    }

    /**
     * Retrieves the JsonElement at the specified path in the JSON data.
     * If no data is associated with the path, a fallback data value is returned.
     *
     * @param path     The path to the JsonElement in the JSON data.
     * @param fallback The fallback data to return if no data is associated with the path
     * @return The JsonElement at the given path, or null if the path does not exist.
     */
    public JsonElement getOrDefault(String path, JsonElement fallback) {
        return get(path) != null ? get(path) : fallback;
    }

    /**
     * Retrieves the String value at the specified path in the JSON data.
     *
     * @param path The path to the String value in the JSON data.
     * @return The String value at the given path, or an empty string if the path does not exist or the value is not a string.
     */
    public String getString(String path) {
        JsonElement element = get(path);
        if (element != null && element.isJsonPrimitive()) return element.getAsString();
        return "";
    }

    /**
     * Retrieves the integer value at the specified path in the JSON data.
     *
     * @param path The path to the integer value in the JSON data.
     * @return The integer value at the given path, or -1 if the path does not exist or the value is not an integer.
     */
    public int getInt(String path) {
        JsonElement element = get(path);
        if (element != null && element.isJsonPrimitive()) return element.getAsInt();
        return -1;
    }

    /**
     * Retrieves the long value at the specified path in the JSON data.
     *
     * @param path The path to the long value in the JSON data.
     * @return The long value at the given path, or -1 if the path does not exist or the value is not a long.
     */
    public long getLong(String path) {
        JsonElement element = get(path);
        if (element != null && element.isJsonPrimitive()) return element.getAsLong();
        return -1;
    }

    /**
     * Retrieves the boolean value at the specified path in the JSON data.
     *
     * @param path The path to the boolean value in the JSON data.
     * @return The boolean value at the given path, or false if the path does not exist or the value is not a boolean.
     */
    public boolean getBoolean(String path) {
        JsonElement element = get(path);
        if (element != null && element.isJsonPrimitive()) return element.getAsBoolean();
        return false;
    }

    /**
     * Retrieves the double value at the specified path in the JSON data.
     *
     * @param path The path to the double value in the JSON data.
     * @return The double value at the given path, or -1 if the path does not exist or the value is not a double.
     */
    public double getDouble(String path) {
        JsonElement element = get(path);
        if (element != null && element.isJsonPrimitive()) return element.getAsDouble();
        return -1;
    }

    /**
     * Retrieves a list of strings at the specified path in the JSON data.
     *
     * @param path The path to the list of strings in the JSON data.
     * @return A Collection of strings at the given path, or an empty list if the path does not exist or the value is not a JSON array of strings.
     */
    public Collection<String> getStringList(String path) {
        JsonElement element = get(path);
        if (element != null && element.isJsonArray()) {
            Collection<String> list = new ArrayList<>();
            for (JsonElement arrayElement : element.getAsJsonArray())
                if (arrayElement.isJsonPrimitive()) list.add(arrayElement.getAsString());
            return list;
        }
        return new ArrayList<>();
    }

    /**
     * Retrieves a list of integers at the specified path in the JSON data.
     *
     * @param path The path to the list of integers in the JSON data.
     * @return A Collection of integers at the given path, or an empty list if the path does not exist or the value is not a JSON array of integers.
     */
    public Collection<Integer> getIntList(String path) {
        JsonElement element = get(path);
        if (element != null && element.isJsonArray()) {
            Collection<Integer> list = new ArrayList<>();
            for (JsonElement arrayElement : element.getAsJsonArray())
                if (arrayElement.isJsonPrimitive()) list.add(arrayElement.getAsInt());
            return list;
        }
        return new ArrayList<>();
    }

    /**
     * Retrieves a list of longs at the specified path in the JSON data.
     *
     * @param path The path to the list of longs in the JSON data.
     * @return A Collection of longs at the given path, or an empty list if the path does not exist or the value is not a JSON array of longs.
     */
    public Collection<Long> getLongList(String path) {
        JsonElement element = get(path);
        if (element != null && element.isJsonArray()) {
            Collection<Long> list = new ArrayList<>();
            for (JsonElement arrayElement : element.getAsJsonArray())
                if (arrayElement.isJsonPrimitive()) list.add(arrayElement.getAsLong());
            return list;
        }
        return new ArrayList<>();
    }

    /**
     * Retrieves a list of booleans at the specified path in the JSON data.
     *
     * @param path The path to the list of booleans in the JSON data.
     * @return A Collection of booleans at the given path, or an empty list if the path does not exist or the value is not a JSON array of booleans.
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
     * Retrieves a list of doubles at the specified path in the JSON data.
     *
     * @param path The path to the list of doubles in the JSON data.
     * @return A Collection of doubles at the given path, or an empty list if the path does not exist or the value is not a JSON array of doubles.
     */
    public Collection<Double> getDoubleList(String path) {
        JsonElement element = get(path);
        if (element != null && element.isJsonArray()) {
            Collection<Double> list = new ArrayList<>();
            for (JsonElement arrayElement : element.getAsJsonArray())
                if (arrayElement.isJsonPrimitive()) list.add(arrayElement.getAsDouble());
            return list;
        }
        return new ArrayList<>();
    }

    /**
     * Sets the value at the specified path in the JSON data.
     * If the path does not exist, it will be created.
     * If the given data is of a supported type (String, Integer, Long, Boolean, Double, Collection or Object[]),
     * the appropriate setter method will be called. Otherwise, the data will be converted to a string and set as a value.
     *
     * @param path The path where the value should be set in the JSON data.
     * @param data The data to be set at the given path.
     * @return The Json object with the updated value at the specified path.
     */
    public Json set(String path, Object data) {
        synchronized (this) {
            path = path.replace("\\.", "&dot;");
            if (data instanceof JsonElement) setJson(path, (JsonElement) data);
            else if (data instanceof String) setString(path, (String) data);
            else if (data instanceof Integer) setInt(path, (int) data);
            else if (data instanceof Long) setLong(path, (long) data);
            else if (data instanceof Boolean) setBoolean(path, (boolean) data);
            else if (data instanceof Double) setDouble(path, (double) data);
            else if (data instanceof Collection<?>) setList(path, (Collection<?>) data);
            else if (data instanceof Object[]) setList(path, Arrays.asList((Object[]) data));
            else setString(path, data.toString());
            return this;
        }
    }

    /**
     * Sets a json element at the specified path in the JSON data.
     *
     * @param path The path where the string value should be set in the JSON data.
     * @param data The string data to be set at the given path.
     */
    private void setJson(String path, JsonElement data) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1].replace("&dot;", ".");
        JsonObject temp = getObject();
        for (int i = 0; i < args.length - 1; i++) {
            String s = args[i].replace("&dot;", ".");
            if (!temp.has(s)) temp.add(s, new JsonObject());
            JsonElement element = temp.get(s);
            if (!element.isJsonObject()) {
                temp.remove(s);
                temp.add(s, new JsonObject());
            }
            temp = element.getAsJsonObject();
        }
        temp.add(object, data);
    }

    /**
     * Sets a string value at the specified path in the JSON data.
     *
     * @param path The path where the string value should be set in the JSON data.
     * @param data The string data to be set at the given path.
     */
    private void setString(String path, String data) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1].replace("&dot;", ".");
        JsonObject temp = getObject();
        for (int i = 0; i < args.length - 1; i++) {
            String s = args[i].replace("&dot;", ".");
            if (!temp.has(s)) temp.add(s, new JsonObject());
            JsonElement element = temp.get(s);
            if (!element.isJsonObject()) {
                temp.remove(s);
                temp.add(s, new JsonObject());
            }
            temp = element.getAsJsonObject();
        }
        temp.addProperty(object, data.replace("&dot;", "."));
    }

    /**
     * Sets an integer value at the specified path in the JSON data.
     *
     * @param path The path where the integer value should be set in the JSON data.
     * @param data The integer data to be set at the given path.
     */
    private void setInt(String path, int data) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1].replace("&dot;", ".");
        JsonObject temp = getObject();
        for (int i = 0; i < args.length - 1; i++) {
            String s = args[i].replace("&dot;", ".");
            if (!temp.has(s)) temp.add(s, new JsonObject());
            JsonElement element = temp.get(s);
            if (!element.isJsonObject()) {
                temp.remove(s);
                temp.add(s, new JsonObject());
            }
            temp = element.getAsJsonObject();
        }
        temp.addProperty(object, data);
    }

    /**
     * Sets a long value at the specified path in the JSON data.
     *
     * @param path The path where the long value should be set in the JSON data.
     * @param data The long data to be set at the given path.
     */
    private void setLong(String path, long data) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1].replace("&dot;", ".");
        JsonObject temp = getObject();
        for (int i = 0; i < args.length - 1; i++) {
            String s = args[i].replace("&dot;", ".");
            if (!temp.has(s)) temp.add(s, new JsonObject());
            JsonElement element = temp.get(s);
            if (!element.isJsonObject()) {
                temp.remove(s);
                temp.add(s, new JsonObject());
            }
            temp = element.getAsJsonObject();
        }
        temp.addProperty(object, data);
    }

    /**
     * Sets a boolean value at the specified path in the JSON data.
     *
     * @param path The path where the boolean value should be set in the JSON data.
     * @param data The boolean data to be set at the given path.
     */
    private void setBoolean(String path, boolean data) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1].replace("&dot;", ".");
        JsonObject temp = getObject();
        for (int i = 0; i < args.length - 1; i++) {
            String s = args[i].replace("&dot;", ".");
            if (!temp.has(s)) temp.add(s, new JsonObject());
            JsonElement element = temp.get(s);
            if (!element.isJsonObject()) {
                temp.remove(s);
                temp.add(s, new JsonObject());
            }
            temp = element.getAsJsonObject();
        }
        temp.addProperty(object, data);
    }

    /**
     * Sets a double value at the specified path in the JSON data.
     *
     * @param path The path where the double value should be set in the JSON data.
     * @param data The double data to be set at the given path.
     */
    private void setDouble(String path, double data) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1].replace("&dot;", ".");
        JsonObject temp = getObject();
        for (int i = 0; i < args.length - 1; i++) {
            String s = args[i].replace("&dot;", ".");
            if (!temp.has(s)) temp.add(s, new JsonObject());
            JsonElement element = temp.get(s);
            if (!element.isJsonObject()) {
                temp.remove(s);
                temp.add(s, new JsonObject());
            }
            temp = element.getAsJsonObject();
        }
        temp.addProperty(object, data);
    }

    /**
     * Sets a collection of values at the specified path in the JSON data.
     * If the collection is empty, an empty JSON array will be set at the path.
     * For each element in the collection, the appropriate setter method will be called based on its type.
     * If an element in the collection is not of a supported type (String, Integer, Long, Boolean, Double),
     * it will be converted to a string and added to the JSON array.
     *
     * @param path       The path where the collection should be set in the JSON data.
     * @param collection The collection of values to be set at the given path.
     */
    @SuppressWarnings("unchecked")
    private void setList(String path, Collection<?> collection) {
        assert collection != null;
        if (collection.isEmpty()) setEmptyList(path);
        for (Object o : collection) {
            if (o instanceof JsonElement) setJsonList(path, (Collection<JsonElement>) collection);
            else if (o instanceof String) setStringList(path, (Collection<String>) collection);
            else if (o instanceof Integer) setIntList(path, (Collection<Integer>) collection);
            else if (o instanceof Long) setLongList(path, (Collection<Long>) collection);
            else if (o instanceof Boolean) setBoolList(path, (Collection<Boolean>) collection);
            else if (o instanceof Double) setDoubleList(path, (Collection<Double>) collection);
            else setStringList(path, collection.stream().map(Object::toString).toList());
            break;
        }
    }

    /**
     * Sets an empty JSON array at the specified path in the JSON data.
     *
     * @param path The path where the empty JSON array should be set.
     */
    private void setEmptyList(String path) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1].replace("&dot;", ".");
        JsonObject temp = getObject();
        for (int i = 0; i < args.length - 1; i++) {
            String s = args[i].replace("&dot;", ".");
            if (!temp.has(s)) temp.add(s, new JsonObject());
            JsonElement element = temp.get(s);
            if (!element.isJsonObject()) {
                temp.remove(s);
                temp.add(s, new JsonObject());
            }
            temp = element.getAsJsonObject();
        }
        if (temp.has(object)) temp.remove(object);
        temp.add(object, new JsonArray());
    }

    private void setJsonList(String path, Collection<JsonElement> collection) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1].replace("&dot;", ".");
        JsonObject temp = getObject();
        for (int i = 0; i < args.length - 1; i++) {
            String s = args[i].replace("&dot;", ".");
            if (!temp.has(s)) temp.add(s, new JsonObject());
            JsonElement element = temp.get(s);
            if (!element.isJsonObject()) {
                temp.remove(s);
                temp.add(s, new JsonObject());
            }
            temp = element.getAsJsonObject();
        }
        if (temp.has(object)) temp.remove(object);
        temp.add(object, new JsonArray());
        for (JsonElement item : collection) temp.getAsJsonArray(object).add(item);
    }

    /**
     * Sets a collection of string values at the specified path in the JSON data.
     *
     * @param path       The path where the string collection should be set in the JSON data.
     * @param collection The collection of string values to be set at the given path.
     */
    private void setStringList(String path, Collection<String> collection) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1].replace("&dot;", ".");
        JsonObject temp = getObject();
        for (int i = 0; i < args.length - 1; i++) {
            String s = args[i].replace("&dot;", ".");
            if (!temp.has(s)) temp.add(s, new JsonObject());
            JsonElement element = temp.get(s);
            if (!element.isJsonObject()) {
                temp.remove(s);
                temp.add(s, new JsonObject());
            }
            temp = element.getAsJsonObject();
        }
        if (temp.has(object)) temp.remove(object);
        temp.add(object, new JsonArray());
        for (String item : collection) temp.getAsJsonArray(object).add(item);
    }

    /**
     * Sets a collection of integer values at the specified path in the JSON data.
     *
     * @param path       The path where the integer collection should be set in the JSON data.
     * @param collection The collection of integer values to be set at the given path.
     */
    private void setIntList(String path, Collection<Integer> collection) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1].replace("&dot;", ".");
        JsonObject temp = getObject();
        for (int i = 0; i < args.length - 1; i++) {
            String s = args[i].replace("&dot;", ".");
            if (!temp.has(s)) temp.add(s, new JsonObject());
            JsonElement element = temp.get(s);
            if (!element.isJsonObject()) {
                temp.remove(s);
                temp.add(s, new JsonObject());
            }
            temp = element.getAsJsonObject();
        }
        if (temp.has(object)) temp.remove(object);
        temp.add(object, new JsonArray());
        for (int item : collection) temp.getAsJsonArray(object).add(item);
    }

    /**
     * Sets a collection of long values at the specified path in the JSON data.
     *
     * @param path       The path where the long collection should be set in the JSON data.
     * @param collection The collection of long values to be set at the given path.
     */
    private void setLongList(String path, Collection<Long> collection) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1].replace("&dot;", ".");
        JsonObject temp = getObject();
        for (int i = 0; i < args.length - 1; i++) {
            String s = args[i].replace("&dot;", ".");
            if (!temp.has(s)) temp.add(s, new JsonObject());
            JsonElement element = temp.get(s);
            if (!element.isJsonObject()) {
                temp.remove(s);
                temp.add(s, new JsonObject());
            }
            temp = element.getAsJsonObject();
        }
        if (temp.has(object)) temp.remove(object);
        temp.add(object, new JsonArray());
        for (long item : collection) temp.getAsJsonArray(object).add(item);
    }

    /**
     * Sets a collection of boolean values at the specified path in the JSON data.
     *
     * @param path       The path where the boolean collection should be set in the JSON data.
     * @param collection The collection of boolean values to be set at the given path.
     */
    private void setBoolList(String path, Collection<Boolean> collection) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1].replace("&dot;", ".");
        JsonObject temp = getObject();
        for (int i = 0; i < args.length - 1; i++) {
            String s = args[i].replace("&dot;", ".");
            if (!temp.has(s)) temp.add(s, new JsonObject());
            JsonElement element = temp.get(s);
            if (!element.isJsonObject()) {
                temp.remove(s);
                temp.add(s, new JsonObject());
            }
            temp = element.getAsJsonObject();
        }
        if (temp.has(object)) temp.remove(object);
        temp.add(object, new JsonArray());
        for (boolean item : collection) temp.getAsJsonArray(object).add(item);
    }

    /**
     * Sets a collection of double values at the specified path in the JSON data.
     *
     * @param path       The path where the double collection should be set in the JSON data.
     * @param collection The collection of double values to be set at the given path.
     */
    private void setDoubleList(String path, Collection<Double> collection) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1].replace("&dot;", ".");
        JsonObject temp = getObject();
        for (int i = 0; i < args.length - 1; i++) {
            String s = args[i].replace("&dot;", ".");
            if (!temp.has(s)) temp.add(s, new JsonObject());
            JsonElement element = temp.get(s);
            if (!element.isJsonObject()) {
                temp.remove(s);
                temp.add(s, new JsonObject());
            }
            temp = element.getAsJsonObject();
        }
        if (temp.has(object)) temp.remove(object);
        temp.add(object, new JsonArray());
        for (double item : collection) temp.getAsJsonArray(object).add(item);
    }

    /**
     * Saves the JSON data to the specified file.
     *
     * @param f The file where the JSON data should be saved.
     */
    public void save(File f) {
        synchronized (this) {
            try {
                Gson gson = new GsonBuilder().create();
                String json = gson.toJson(getObject());
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(f));
                bufferedWriter.write(json);
                bufferedWriter.flush();
                bufferedWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns the JSON data as a JSON string.
     * If any error occurs during the process, it returns a default error JSON string.
     *
     * @return The JSON data as a JSON string.
     */
    public String asString() {
        try {
            Gson gson = new GsonBuilder().create();
            return gson.toJson(getObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "{\"code\":500,\"message\":\"CraftsCore failed to build a string from the json data!\"}";
    }

    /**
     * Returns the JSON data as a JSON string.
     * If any error occurs during the process, it returns a default error JSON string.
     *
     * @return The JSON data as a JSON string.
     */
    @Override
    public String toString() {
        return asString();
    }

    /**
     * Returns the underlying JsonObject representing the JSON data.
     *
     * @return The JsonObject representing the JSON data.
     */
    public JsonObject getObject() {
        return object;
    }

    /**
     * Returns a new and empty Json Class
     *
     * @return The new Json class
     */
    public static Json empty() {
        return new Json(new JsonObject());
    }

}
