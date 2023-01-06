package de.craftsblock.craftscore.api.config;

import com.google.gson.JsonObject;

import java.io.File;
import java.util.Collection;

public abstract class AbstractConfig {

    private JsonObject object;
    public AbstractConfig(JsonObject object) {
        this.object = object;
    }

    public abstract boolean contains(String path);

    public abstract String getString(String path);
    public abstract int getInt(String path);
    public abstract long getLong(String path);
    public abstract boolean getBoolean(String path);
    public abstract double getDouble(String path);

    public abstract Collection<String> getStringList(String path);
    public abstract Collection<Integer> getIntList(String path);
    public abstract Collection<Long> getLongList(String path);
    public abstract Collection<Boolean> getBoolList(String path);
    public abstract Collection<Double> getDoubleList(String path);

    public abstract void set(String path, Object data);
    public abstract void remove(String path);

    public abstract void save(File f);
    public abstract String asString();

    public final JsonObject getObject() {
        return object;
    }

    public static class UnsupportedDataTypeException extends RuntimeException {

        @Override
        public String toString() {
            return "The data type you specified is not supported by the Config!";
        }

    }

}
