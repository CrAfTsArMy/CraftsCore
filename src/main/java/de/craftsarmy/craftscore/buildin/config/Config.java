package de.craftsarmy.craftscore.buildin.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.craftsarmy.craftscore.api.config.AbstractConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class Config extends AbstractConfig {

    public Config(JsonObject object) {
        super(object);
    }

    @Override
    public boolean contains(String path) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1];
        JsonObject temp = getObject();
        for (String s : args)
            if (s.equals(object) && temp.has(object))
                return true;
            else if (temp.has(s))
                temp = temp.getAsJsonObject(s);
            else
                return false;
        return false;
    }

    @Override
    public String get(String path) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1];
        JsonObject temp = getObject();
        for (String s : args)
            if (s.equals(object))
                return temp.get(object).getAsString();
            else
                temp = temp.getAsJsonObject(s);
        return path;
    }

    @Override
    public int getInt(String path) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1];
        JsonObject temp = getObject();
        for (String s : args)
            if (s.equals(object))
                return temp.get(object).getAsInt();
            else
                temp = temp.getAsJsonObject(s);
        return -1;
    }

    @Override
    public long getLong(String path) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1];
        JsonObject temp = getObject();
        for (String s : args)
            if (s.equals(object))
                return temp.get(object).getAsLong();
            else
                temp = temp.getAsJsonObject(s);
        return -1;
    }

    @Override
    public boolean getBoolean(String path) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1];
        JsonObject temp = getObject();
        for (String s : args)
            if (s.equals(object))
                return temp.get(object).getAsBoolean();
            else
                temp = temp.getAsJsonObject(s);
        return false;
    }

    @Override
    public double getDouble(String path) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1];
        JsonObject temp = getObject();
        for (String s : args)
            if (s.equals(object))
                return temp.get(object).getAsDouble();
            else
                temp = temp.getAsJsonObject(s);
        return -1;
    }

    @Override
    public Collection<?> getList(String path) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1];
        JsonObject temp = getObject();
        for (String s : args)
            if (s.equals(object))
                if (!temp.get(object).isJsonArray()) {
                    ConcurrentLinkedQueue<String> list = new ConcurrentLinkedQueue<>();
                    for (JsonElement element : temp.get(object).getAsJsonArray())
                        list.add(element.getAsString());
                    return list;
                } else {
                    throw new IllegalArgumentException(path + " is not from type \"JsonArray\"!");
                }
            else
                temp = temp.getAsJsonObject(s);
        return new ConcurrentLinkedQueue<>();
    }

    @Override
    public Collection<Integer> getIntList(String path) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1];
        JsonObject temp = getObject();
        for (String s : args)
            if (s.equals(object))
                if (!temp.get(object).isJsonArray()) {
                    ConcurrentLinkedQueue<Integer> list = new ConcurrentLinkedQueue<>();
                    for (JsonElement element : temp.get(object).getAsJsonArray())
                        list.add(element.getAsInt());
                    return list;
                } else {
                    throw new IllegalArgumentException(path + " is not from type \"JsonArray\"!");
                }
            else
                temp = temp.getAsJsonObject(s);
        return new ConcurrentLinkedQueue<>();
    }

    @Override
    public Collection<Long> getLongList(String path) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1];
        JsonObject temp = getObject();
        for (String s : args)
            if (s.equals(object))
                if (!temp.get(object).isJsonArray()) {
                    ConcurrentLinkedQueue<Long> list = new ConcurrentLinkedQueue<>();
                    for (JsonElement element : temp.get(object).getAsJsonArray())
                        list.add(element.getAsLong());
                    return list;
                } else {
                    throw new IllegalArgumentException(path + " is not from type \"JsonArray\"!");
                }
            else
                temp = temp.getAsJsonObject(s);
        return new ConcurrentLinkedQueue<>();
    }

    @Override
    public Collection<Boolean> getBoolList(String path) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1];
        JsonObject temp = getObject();
        for (String s : args)
            if (s.equals(object))
                if (!temp.get(object).isJsonArray()) {
                    ConcurrentLinkedQueue<Boolean> list = new ConcurrentLinkedQueue<>();
                    for (JsonElement element : temp.get(object).getAsJsonArray())
                        list.add(element.getAsBoolean());
                    return list;
                } else {
                    throw new IllegalArgumentException(path + " is not from type \"JsonArray\"!");
                }
            else
                temp = temp.getAsJsonObject(s);
        return new ConcurrentLinkedQueue<>();
    }

    @Override
    public Collection<Double> getDoubleList(String path) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1];
        JsonObject temp = getObject();
        for (String s : args)
            if (s.equals(object))
                if (!temp.get(object).isJsonArray()) {
                    ConcurrentLinkedQueue<Double> list = new ConcurrentLinkedQueue<>();
                    for (JsonElement element : temp.get(object).getAsJsonArray())
                        list.add(element.getAsDouble());
                    return list;
                } else {
                    throw new IllegalArgumentException(path + " is not from type \"JsonArray\"!");
                }
            else
                temp = temp.getAsJsonObject(s);
        return new ConcurrentLinkedQueue<>();
    }

    @Override
    public void set(String path, Object data) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1];
        JsonObject temp = getObject();
        for (String s : args) {
            if (!object.equals(s)) {
                if (!temp.has(s))
                    temp.add(s, new JsonObject());
                if (!temp.get(s).isJsonObject()) {
                    temp.remove(s);
                    temp.add(s, new JsonObject());
                }
                temp = temp.getAsJsonObject(s);
            } else {
                temp.addProperty(object, data.toString());
            }
        }
    }

    @Override
    public void setInt(String path, int data) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1];
        JsonObject temp = getObject();
        for (String s : args) {
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

    @Override
    public void setLong(String path, long data) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1];
        JsonObject temp = getObject();
        for (String s : args) {
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

    @Override
    public void setBoolean(String path, boolean data) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1];
        JsonObject temp = getObject();
        for (String s : args) {
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

    @Override
    public void setDouble(String path, double data) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1];
        JsonObject temp = getObject();
        for (String s : args) {
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

    @Override
    public void setList(String path, Collection<?> collection) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1];
        JsonObject temp = getObject();
        for (String s : args) {
            if (!object.equals(s)) {
                if (!temp.has(s))
                    temp.add(s, new JsonObject());
                if (!temp.get(s).isJsonArray()) {
                    temp.remove(s);
                    temp.add(s, new JsonObject());
                }
                temp = temp.getAsJsonObject(s);
            } else
                for (Object o : collection)
                    temp.getAsJsonArray(object).add(o.toString());
        }
    }

    @Override
    public void setIntList(String path, Collection<Integer> collection) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1];
        JsonObject temp = getObject();
        for (String s : args) {
            if (!object.equals(s)) {
                if (!temp.has(s))
                    temp.add(s, new JsonObject());
                if (!temp.get(s).isJsonArray()) {
                    temp.remove(s);
                    temp.add(s, new JsonObject());
                }
                temp = temp.getAsJsonObject(s);
            } else
                for (Integer o : collection)
                    temp.getAsJsonArray(object).add(o);
        }
    }

    @Override
    public void setLongList(String path, Collection<Long> collection) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1];
        JsonObject temp = getObject();
        for (String s : args) {
            if (!object.equals(s)) {
                if (!temp.has(s))
                    temp.add(s, new JsonObject());
                if (!temp.get(s).isJsonArray()) {
                    temp.remove(s);
                    temp.add(s, new JsonObject());
                }
                temp = temp.getAsJsonObject(s);
            } else
                for (Long o : collection)
                    temp.getAsJsonArray(object).add(o);
        }
    }

    @Override
    public void setBoolList(String path, Collection<Boolean> collection) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1];
        JsonObject temp = getObject();
        for (String s : args) {
            if (!object.equals(s)) {
                if (!temp.has(s))
                    temp.add(s, new JsonObject());
                if (!temp.get(s).isJsonArray()) {
                    temp.remove(s);
                    temp.add(s, new JsonObject());
                }
                temp = temp.getAsJsonObject(s);
            } else
                for (Boolean o : collection)
                    temp.getAsJsonArray(object).add(o);
        }
    }

    @Override
    public void setDoubleList(String path, Collection<Double> collection) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1];
        JsonObject temp = getObject();
        for (String s : args) {
            if (!object.equals(s)) {
                if (!temp.has(s))
                    temp.add(s, new JsonObject());
                if (!temp.get(s).isJsonArray()) {
                    temp.remove(s);
                    temp.add(s, new JsonObject());
                }
                temp = temp.getAsJsonObject(s);
            } else
                for (Double o : collection)
                    temp.getAsJsonArray(object).add(o);
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
