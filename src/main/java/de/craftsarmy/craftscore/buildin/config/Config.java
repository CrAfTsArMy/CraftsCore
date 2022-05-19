package de.craftsarmy.craftscore.buildin.config;

import com.google.gson.*;
import de.craftsarmy.craftscore.api.config.AbstractConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

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
    public String getString(String path) {
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
