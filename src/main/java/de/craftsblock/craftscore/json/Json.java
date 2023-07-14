package de.craftsblock.craftscore.json;

import com.google.gson.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public final class Json {

    private JsonObject object;
    private Gson gson;

    public Json(JsonObject object) {
        this.object = object;
        this.gson = new Gson();
    }

    public boolean contains(String path) {
        String[] args = path.split("\\.");
        JsonObject temp = getObject();
        for (int i = 0; i < args.length - 1; i++) {
            String s = args[i].replace("&dot;", ".");
            if (temp.has(s)) temp = temp.getAsJsonObject(s);
            else return false;
        }
        String object = args[args.length - 1].replace("&dot;", ".");
        return temp.has(object);
    }

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

    public void serialize(String path, Object data) {
        set(path, gson.toJson(data));
    }

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

    public String getString(String path) {
        JsonElement element = get(path);
        if (element != null && element.isJsonPrimitive()) return element.getAsString();
        return "";
    }

    public int getInt(String path) {
        JsonElement element = get(path);
        if (element != null && element.isJsonPrimitive()) return element.getAsInt();
        return -1;
    }

    public long getLong(String path) {
        JsonElement element = get(path);
        if (element != null && element.isJsonPrimitive()) return element.getAsLong();
        return -1;
    }

    public boolean getBoolean(String path) {
        JsonElement element = get(path);
        if (element != null && element.isJsonPrimitive()) return element.getAsBoolean();
        return false;
    }

    public double getDouble(String path) {
        JsonElement element = get(path);
        if (element != null && element.isJsonPrimitive()) return element.getAsDouble();
        return -1;
    }

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


    public Json set(String path, Object data) {
        path = path.replace("\\.", "&dot;");
        if (data instanceof String)
            setString(path, (String) data);
        else if (data instanceof Integer)
            setInt(path, (int) data);
        else if (data instanceof Long)
            setLong(path, (long) data);
        else if (data instanceof Boolean)
            setBoolean(path, (boolean) data);
        else if (data instanceof Double)
            setDouble(path, (double) data);
        else if (data instanceof Collection<?>)
            setList(path, (Collection<?>) data);
        else if (data instanceof Object[])
            setList(path, Arrays.asList((Object[]) data));
        else
            throw new UnsupportedDataTypeException();
        return this;
    }

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

    @SuppressWarnings("unchecked")
    private void setList(String path, Collection<?> collection) {
        assert collection != null;
        if (collection.isEmpty())
            setEmptyList(path);
        for (Object o : collection)
            if (o instanceof String)
                setStringList(path, (Collection<String>) collection);
            else if (o instanceof Integer)
                setIntList(path, (Collection<Integer>) collection);
            else if (o instanceof Long)
                setLongList(path, (Collection<Long>) collection);
            else if (o instanceof Boolean)
                setBoolList(path, (Collection<Boolean>) collection);
            else if (o instanceof Double)
                setDoubleList(path, (Collection<Double>) collection);
    }

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

    public void save(File f) {
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

    public String asString() {
        try {
            Gson gson = new GsonBuilder().create();
            return gson.toJson(getObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "{\"code\":500,\"message\":\"CraftsCore failed to build a string from the config data!\"}";
    }

    public JsonObject getObject() {
        return object;
    }

    public static class UnsupportedDataTypeException extends RuntimeException {

        @Override
        public String toString() {
            return "The data type you specified is not supported yet!";
        }

    }

}
