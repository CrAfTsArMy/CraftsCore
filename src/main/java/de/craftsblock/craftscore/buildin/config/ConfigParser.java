package de.craftsblock.craftscore.buildin.config;

import com.google.gson.JsonParser;
import de.craftsblock.craftscore.api.config.AbstractConfigParser;
import de.craftsblock.craftscore.utils.Validator;

import java.io.*;

public final class ConfigParser extends AbstractConfigParser {

    /**
     * Reading a File and convert it to a {@link Config} object
     *
     * @param f Represents the {@link File} that the Parse should read
     * @return {@link Config}
     */
    @Override
    public Config parse(File f) {
        try {
            if (!f.getParentFile().exists())
                f.getParentFile().mkdirs();
            f.createNewFile();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            String json = bufferedReader.readLine();
            if (json != null && Validator.isJsonValid(json))
                return new Config(JsonParser.parseString(json).getAsJsonObject());
            else
                return new Config(JsonParser.parseString("{}").getAsJsonObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Reading a String and convert it to a {@link Config} object
     *
     * @param json Represents the {@link String} that the Parse should read
     * @return {@link Config}
     */
    @Override
    public Config parse(String json) {
        try {
            if (json != null && Validator.isJsonValid(json))
                return new Config(JsonParser.parseString(json).getAsJsonObject());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
