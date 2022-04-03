package de.craftsarmy.craftscore.buildin.config;

import com.google.gson.JsonParser;
import de.craftsarmy.craftscore.api.config.AbstractConfig;
import de.craftsarmy.craftscore.api.config.AbstractConfigParser;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public final class ConfigParser extends AbstractConfigParser {

    @Override
    @Nullable
    public AbstractConfig parse(File f) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            return new Config(JsonParser.parseReader(bufferedReader).getAsJsonObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
