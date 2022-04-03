package de.craftsarmy.craftscore.api.config;

import com.google.gson.JsonObject;

import java.io.File;

public abstract class AbstractConfig {

    private JsonObject object;
    public AbstractConfig(JsonObject object) {
        this.object = object;
    }

    public abstract String getString(String path);
    public abstract int getInt(String path);
    public abstract long getLong(String path);
    public abstract boolean getBoolean(String path);
    public abstract double getDouble(String path);

    public abstract void set(String path, Object data);
    public abstract void save(File f);

    public final JsonObject getObject() {
        return object;
    }
    public void setObject(JsonObject object) {
        this.object = object;
    }
}
