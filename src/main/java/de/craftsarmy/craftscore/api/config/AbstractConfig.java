package de.craftsarmy.craftscore.api.config;

import com.google.gson.JsonObject;

import java.io.File;
import java.util.Collection;

public abstract class AbstractConfig {

    private JsonObject object;
    public AbstractConfig(JsonObject object) {
        this.object = object;
    }

    public abstract boolean contains(String path);

    public abstract String get(String path);
    public abstract int getInt(String path);
    public abstract long getLong(String path);
    public abstract boolean getBoolean(String path);
    public abstract double getDouble(String path);

    public abstract Collection<?> getList(String path);
    public abstract Collection<Integer> getIntList(String path);
    public abstract Collection<Long> getLongList(String path);
    public abstract Collection<Boolean> getBoolList(String path);
    public abstract Collection<Double> getDoubleList(String path);

    public abstract void set(String path, Object data);
    public abstract void setInt(String path, int data);
    public abstract void setLong(String path, long data);
    public abstract void setBoolean(String path, boolean data);
    public abstract void setDouble(String path, double data);

    public abstract void setList(String path, Collection<?> collection);
    public abstract void setIntList(String path, Collection<Integer> collection);
    public abstract void setLongList(String path, Collection<Long> collection);
    public abstract void setBoolList(String path, Collection<Boolean> collection);
    public abstract void setDoubleList(String path, Collection<Double> collection);

    public abstract void save(File f);
    public abstract String asString();

    public final JsonObject getObject() {
        return object;
    }
    public void setObject(JsonObject object) {
        this.object = object;
    }
}
