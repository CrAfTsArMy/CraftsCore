package de.craftsblock.craftscore.buildin.config;

import com.google.gson.*;
import de.craftsblock.craftscore.api.config.AbstractConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class Config extends AbstractConfig {

    public Config(JsonObject object) {
        super(object);
    }

    @Override
    public boolean contains(String path) {
        path = path.replace("\\.", "&dot;");
        String[] args = path.split("\\.");
        String object = args[args.length - 1].replace("\u0026dot;", ".");
        JsonObject temp = getObject();
        for (String s : args) {
            s = s.replace("\u0026dot;", ".");
            if (s.equals(object) && temp.has(object))
                return true;
            else if (temp.has(s))
                temp = temp.getAsJsonObject(s.replace("\u0026dot;", "."));
            else
                return false;
        }
        return false;
    }

    @Override
    public String getString(String path) {
        path = path.replace("\\.", "&dot;");
        String[] args = path.split("\\.");
        String object = args[args.length - 1].replace("\u0026dot;", ".");
        JsonObject temp = getObject();
        for (String s : args) {
            s = s.replace("\u0026dot;", ".");
            if (s.equals(object))
                return temp.get(object).getAsString();
            else
                temp = temp.getAsJsonObject(s);
        }
        return path;
    }

    @Override
    public int getInt(String path) {
        path = path.replace("\\.", "&dot;");
        String[] args = path.split("\\.");
        String object = args[args.length - 1].replace("\u0026dot;", ".");
        JsonObject temp = getObject();
        for (String s : args) {
            s = s.replace("\u0026dot;", ".");
            if (s.equals(object))
                return temp.get(object).getAsInt();
            else
                temp = temp.getAsJsonObject(s);
        }
        return -1;
    }

    @Override
    public long getLong(String path) {
        path = path.replace("\\.", "&dot;");
        String[] args = path.split("\\.");
        String object = args[args.length - 1].replace("\u0026dot;", ".");
        JsonObject temp = getObject();
        for (String s : args) {
            s = s.replace("\u0026dot;", ".");
            if (s.equals(object))
                return temp.get(object).getAsLong();
            else
                temp = temp.getAsJsonObject(s);
        }
        return -1;
    }

    @Override
    public boolean getBoolean(String path) {
        path = path.replace("\\.", "&dot;");
        String[] args = path.split("\\.");
        String object = args[args.length - 1].replace("\u0026dot;", ".");
        JsonObject temp = getObject();
        for (String s : args) {
            s = s.replace("\u0026dot;", ".");
            if (s.equals(object))
                return temp.get(object).getAsBoolean();
            else
                temp = temp.getAsJsonObject(s);
        }
        return false;
    }

    @Override
    public double getDouble(String path) {
        path = path.replace("\\.", "&dot;");
        String[] args = path.split("\\.");
        String object = args[args.length - 1].replace("\u0026dot;", ".");
        JsonObject temp = getObject();
        for (String s : args) {
            s = s.replace("\u0026dot;", ".");
            if (s.equals(object))
                return temp.get(object).getAsDouble();
            else
                temp = temp.getAsJsonObject(s);
        }
        return -1;
    }

    @Override
    public Collection<String> getStringList(String path) {
        path = path.replace("\\.", "&dot;");
        if (!contains(path))
            return new ConcurrentLinkedQueue<>();
        String[] args = path.split("\\.");
        String object = args[args.length - 1].replace("\u0026dot;", ".");
        JsonObject temp = getObject();
        for (String s : args) {
            s = s.replace("\u0026dot;", ".");
            if (s.equals(object))
                if (!temp.get(object).isJsonObject()) {
                    ConcurrentLinkedQueue<String> list = new ConcurrentLinkedQueue<>();
                    for (JsonElement element : temp.get(object).getAsJsonArray())
                        list.add(element.getAsString());
                    return list;
                } else {
                    throw new IllegalArgumentException(path + " is not from type \"JsonArray\"!");
                }
            else
                temp = temp.getAsJsonObject(s.replace("\u0026dot;", "."));
        }
        return new ConcurrentLinkedQueue<>();
    }

    @Override
    public Collection<Integer> getIntList(String path) {
        path = path.replace("\\.", "&dot;");
        if (!contains(path))
            return new ConcurrentLinkedQueue<>();
        String[] args = path.split("\\.");
        String object = args[args.length - 1].replace("\u0026dot;", ".");
        JsonObject temp = getObject();
        for (String s : args) {
            s = s.replace("\u0026dot;", ".");
            if (s.equals(object))
                if (!temp.get(object).isJsonObject()) {
                    ConcurrentLinkedQueue<Integer> list = new ConcurrentLinkedQueue<>();
                    for (JsonElement element : temp.get(object).getAsJsonArray())
                        list.add(element.getAsInt());
                    return list;
                } else {
                    throw new IllegalArgumentException(path + " is not from type \"JsonArray\"!");
                }
            else
                temp = temp.getAsJsonObject(s.replace("\u0026dot;", "."));
        }
        return new ConcurrentLinkedQueue<>();
    }

    @Override
    public Collection<Long> getLongList(String path) {
        path = path.replace("\\.", "&dot;");
        if (!contains(path))
            return new ConcurrentLinkedQueue<>();
        String[] args = path.split("\\.");
        String object = args[args.length - 1].replace("\u0026dot;", ".");
        JsonObject temp = getObject();
        for (String s : args) {
            s = s.replace("\u0026dot;", ".");
            if (s.equals(object))
                if (!temp.get(object).isJsonObject()) {
                    ConcurrentLinkedQueue<Long> list = new ConcurrentLinkedQueue<>();
                    for (JsonElement element : temp.get(object).getAsJsonArray())
                        list.add(element.getAsLong());
                    return list;
                } else {
                    throw new IllegalArgumentException(path + " is not from type \"JsonArray\"!");
                }
            else
                temp = temp.getAsJsonObject(s.replace("\u0026dot;", "."));
        }
        return new ConcurrentLinkedQueue<>();
    }

    @Override
    public Collection<Boolean> getBoolList(String path) {
        path = path.replace("\\.", "&dot;");
        if (!contains(path))
            return new ConcurrentLinkedQueue<>();
        String[] args = path.split("\\.");
        String object = args[args.length - 1].replace("\u0026dot;", ".");
        JsonObject temp = getObject();
        for (String s : args) {
            s = s.replace("\u0026dot;", ".");
            if (s.equals(object))
                if (!temp.get(object).isJsonObject()) {
                    ConcurrentLinkedQueue<Boolean> list = new ConcurrentLinkedQueue<>();
                    for (JsonElement element : temp.get(object).getAsJsonArray())
                        list.add(element.getAsBoolean());
                    return list;
                } else {
                    throw new IllegalArgumentException(path + " is not from type \"JsonArray\"!");
                }
            else
                temp = temp.getAsJsonObject(s);
        }
        return new ConcurrentLinkedQueue<>();
    }

    @Override
    public Collection<Double> getDoubleList(String path) {
        path = path.replace("\\.", "&dot;");
        if (!contains(path))
            return new ConcurrentLinkedQueue<>();
        String[] args = path.split("\\.");
        String object = args[args.length - 1].replace("\u0026dot;", ".");
        JsonObject temp = getObject();
        for (String s : args) {
            s = s.replace("\u0026dot;", ".");
            if (s.equals(object))
                if (!temp.get(object).isJsonObject()) {
                    ConcurrentLinkedQueue<Double> list = new ConcurrentLinkedQueue<>();
                    for (JsonElement element : temp.get(object).getAsJsonArray())
                        list.add(element.getAsDouble());
                    return list;
                } else {
                    throw new IllegalArgumentException(path + " is not from type \"JsonArray\"!");
                }
            else
                temp = temp.getAsJsonObject(s);
        }
        return new ConcurrentLinkedQueue<>();
    }

    @Override
    public void set(String path, Object data) {
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
        else
            throw new UnsupportedDataTypeException();

    }

    private void setString(String path, String data) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1].replace("\u0026dot;", ".");
        JsonObject temp = getObject();
        for (String s : args) {
            s = s.replace("\u0026dot;", ".");
            if (!object.equals(s)) {
                if (!temp.has(s))
                    temp.add(s, new JsonObject());
                if (!temp.get(s).isJsonObject()) {
                    temp.remove(s);
                    temp.add(s, new JsonObject());
                }
                temp = temp.getAsJsonObject(s);
            } else {
                temp.addProperty(object, data.replace("\u0026dot;", "."));
            }
        }
    }

    private void setInt(String path, int data) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1].replace("\u0026dot;", ".");
        JsonObject temp = getObject();
        for (String s : args) {
            s = s.replace("\u0026dot;", ".");
            if (!object.equals(s)) {
                if (!temp.has(s))
                    temp.add(s, new JsonObject());
                if (!temp.get(s).isJsonObject()) {
                    temp.remove(s);
                    temp.add(s, new JsonObject());
                }
                temp = temp.getAsJsonObject(s);
            } else {
                temp.addProperty(object, data);
            }
        }
    }

    private void setLong(String path, long data) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1].replace("\u0026dot;", ".");
        JsonObject temp = getObject();
        for (String s : args) {
            s = s.replace("\u0026dot;", ".");
            if (!object.equals(s)) {
                if (!temp.has(s))
                    temp.add(s, new JsonObject());
                if (!temp.get(s).isJsonObject()) {
                    temp.remove(s);
                    temp.add(s, new JsonObject());
                }
                temp = temp.getAsJsonObject(s);
            } else {
                temp.addProperty(object, data);
            }
        }
    }

    private void setBoolean(String path, boolean data) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1].replace("\u0026dot;", ".");
        JsonObject temp = getObject();
        for (String s : args) {
            s = s.replace("\u0026dot;", ".");
            if (!object.equals(s)) {
                if (!temp.has(s))
                    temp.add(s, new JsonObject());
                if (!temp.get(s).isJsonObject()) {
                    temp.remove(s);
                    temp.add(s, new JsonObject());
                }
                temp = temp.getAsJsonObject(s);
            } else {
                temp.addProperty(object, data);
            }
        }
    }

    private void setDouble(String path, double data) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1].replace("\u0026dot;", ".");
        JsonObject temp = getObject();
        for (String s : args) {
            if (!object.equals(s)) {
                if (!temp.has(s))
                    temp.add(s, new JsonObject());
                if (!temp.get(s.replace("\u0026dot;", ".")).isJsonObject()) {
                    temp.remove(s.replace("\u0026dot;", "."));
                    temp.add(s, new JsonObject());
                }
                temp = temp.getAsJsonObject(s.replace("\u0026dot;", "."));
            } else {
                temp.addProperty(object, data);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void setList(String path, Collection<?> collection) {
        assert collection != null;
        if (collection.isEmpty())
            setEmptyList(path, Collections.emptyList());
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

    private void setEmptyList(String path, Collection<?> collection) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1].replace("\u0026dot;", ".");
        JsonObject temp = getObject();
        for (String s : args) {
            if (!object.equals(s)) {
                if (!temp.has(s))
                    temp.add(s, new JsonObject());
                if (!temp.get(s.replace("\u0026dot;", ".")).isJsonObject()) {
                    temp.remove(s.replace("\u0026dot;", "."));
                    temp.add(s, new JsonObject());
                }
                temp = temp.getAsJsonObject(s.replace("\u0026dot;", "."));
            } else {
                if (temp.has(object))
                    temp.remove(object);
                temp.add(object, new JsonArray());
                for (Object o : collection)
                    temp.getAsJsonArray(object).add(o.toString());
            }
        }
    }

    private void setStringList(String path, Collection<String> collection) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1].replace("\u0026dot;", ".");
        JsonObject temp = getObject();
        for (String s : args) {
            if (!object.equals(s)) {
                if (!temp.has(s))
                    temp.add(s, new JsonObject());
                if (!temp.get(s.replace("\u0026dot;", ".")).isJsonObject()) {
                    temp.remove(s.replace("\u0026dot;", "."));
                    temp.add(s, new JsonObject());
                }
                temp = temp.getAsJsonObject(s.replace("\u0026dot;", "."));
            } else {
                if (temp.has(object))
                    temp.remove(object);
                temp.add(object, new JsonArray());
                for (Object o : collection)
                    temp.getAsJsonArray(object).add(o.toString());
            }
        }
    }

    private void setIntList(String path, Collection<Integer> collection) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1].replace("\u0026dot;", ".");
        JsonObject temp = getObject();
        for (String s : args) {
            if (!object.equals(s)) {
                if (!temp.has(s))
                    temp.add(s, new JsonObject());
                if (!temp.get(s.replace("\u0026dot;", ".")).isJsonObject()) {
                    temp.remove(s.replace("\u0026dot;", "."));
                    temp.add(s, new JsonObject());
                }
                temp = temp.getAsJsonObject(s.replace("\u0026dot;", "."));
            } else {
                if (temp.has(object))
                    temp.remove(object);
                temp.add(object, new JsonArray());
                for (Integer o : collection)
                    temp.getAsJsonArray(object).add(o);
            }
        }
    }

    private void setLongList(String path, Collection<Long> collection) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1].replace("\u0026dot;", ".");
        JsonObject temp = getObject();
        for (String s : args) {
            if (!object.equals(s)) {
                if (!temp.has(s))
                    temp.add(s, new JsonObject());
                if (!temp.get(s.replace("\u0026dot;", ".")).isJsonObject()) {
                    temp.remove(s.replace("\u0026dot;", "."));
                    temp.add(s, new JsonObject());
                }
                temp = temp.getAsJsonObject(s.replace("\u0026dot;", "."));
            } else {
                if (temp.has(object))
                    temp.remove(object);
                temp.add(object, new JsonArray());
                for (Long o : collection)
                    temp.getAsJsonArray(object).add(o);
            }
        }
    }

    private void setBoolList(String path, Collection<Boolean> collection) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1].replace("\u0026dot;", ".");
        JsonObject temp = getObject();
        for (String s : args) {
            if (!object.equals(s)) {
                if (!temp.has(s))
                    temp.add(s, new JsonObject());
                if (!temp.get(s.replace("\u0026dot;", ".")).isJsonObject()) {
                    temp.remove(s.replace("\u0026dot;", "."));
                    temp.add(s, new JsonObject());
                }
                temp = temp.getAsJsonObject(s.replace("\u0026dot;", "."));
            } else {
                if (temp.has(object))
                    temp.remove(object);
                temp.add(object, new JsonArray());
                for (Boolean o : collection)
                    temp.getAsJsonArray(object).add(o);
            }
        }
    }

    private void setDoubleList(String path, Collection<Double> collection) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1].replace("\u0026dot;", ".");
        JsonObject temp = getObject();
        for (String s : args) {
            if (!object.equals(s)) {
                if (!temp.has(s))
                    temp.add(s, new JsonObject());
                if (!temp.get(s.replace("\u0026dot;", ".")).isJsonObject()) {
                    temp.remove(s.replace("\u0026dot;", "."));
                    temp.add(s, new JsonObject());
                }
                temp = temp.getAsJsonObject(s.replace("\u0026dot;", "."));
            } else {
                if (temp.has(object))
                    temp.remove(object);
                temp.add(object, new JsonArray());
                for (Double o : collection)
                    temp.getAsJsonArray(object).add(o);
            }
        }
    }

    @Override
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

    @Override
    public String asString() {
        try {
            Gson gson = new GsonBuilder().create();
            return gson.toJson(getObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "{\"code\":500,\"message\":\"CraftsCore failed to build a string from the config data!\"}";
    }

}
