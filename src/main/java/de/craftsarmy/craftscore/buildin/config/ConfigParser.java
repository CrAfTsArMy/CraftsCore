package de.craftsarmy.craftscore.buildin.config;

import com.google.gson.JsonParser;
import de.craftsarmy.craftscore.api.config.AbstractConfig;
import de.craftsarmy.craftscore.api.config.AbstractConfigParser;
import de.craftsarmy.craftscore.utils.Validator;
import org.jetbrains.annotations.Nullable;

import java.io.*;

public final class ConfigParser extends AbstractConfigParser {

    @Override
    public AbstractConfig parse(File f) {
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

    @Override
    public AbstractConfig parse(String json) {
        try {
            if (json != null && Validator.isJsonValid(json))
                return new Config(JsonParser.parseString(json).getAsJsonObject());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
