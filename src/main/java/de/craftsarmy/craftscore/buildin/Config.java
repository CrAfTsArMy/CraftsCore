package de.craftsarmy.craftscore.buildin;

import com.google.gson.JsonObject;
import de.craftsarmy.craftscore.api.config.AbstractConfig;

import java.io.File;

public final class Config extends AbstractConfig {

    public Config(JsonObject object) {
        super(object);
    }

    @Override
    public String getString(String path) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1];
        JsonObject temp = getObject();
        for(String s : args)
            if(s.equals(object))
                return temp.get(object).getAsString();
            else
                temp  = temp.getAsJsonObject(s);
        return path;
    }

    @Override
    public int getInt(String path) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1];
        JsonObject temp = getObject();
        for(String s : args)
            if(s.equals(object))
                return temp.get(object).getAsInt();
            else
                temp  = temp.getAsJsonObject(s);
        return -1;
    }

    @Override
    public long getLong(String path) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1];
        JsonObject temp = getObject();
        for(String s : args)
            if(s.equals(object))
                return temp.get(object).getAsLong();
            else
                temp  = temp.getAsJsonObject(s);
        return -1;
    }

    @Override
    public boolean getBoolean(String path) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1];
        JsonObject temp = getObject();
        for(String s : args)
            if(s.equals(object))
                return temp.get(object).getAsBoolean();
            else
                temp  = temp.getAsJsonObject(s);
        return false;
    }

    @Override
    public double getDouble(String path) {
        String[] args = path.split("\\.");
        String object = args[args.length - 1];
        JsonObject temp = getObject();
        for(String s : args)
            if(s.equals(object))
                return temp.get(object).getAsDouble();
            else
                temp  = temp.getAsJsonObject(s);
        return -1;
    }

    @Override
    public void set(String path, Object data) {

    }

    @Override
    public void save(File f) {

    }

}
