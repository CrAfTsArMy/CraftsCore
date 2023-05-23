package de.craftsblock.craftscore.json;

import de.craftsblock.craftscore.utils.Validator;

import java.io.*;

public final class JsonParser {

    /**
     * Reading a File and convert it to a {@link Json} object
     *
     * @param f Represents the {@link File} that the Parse should read
     * @return {@link Json}
     */
    public static Json parse(File f) {
        try {
            if (!f.getParentFile().exists())
                f.getParentFile().mkdirs();
            f.createNewFile();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            String json = bufferedReader.readLine();
            if (json != null && Validator.isJsonValid(json))
                return new Json(com.google.gson.JsonParser.parseString(json).getAsJsonObject());
            else
                return new Json(com.google.gson.JsonParser.parseString("{}").getAsJsonObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Reading a String and convert it to a {@link Json} object
     *
     * @param json Represents the {@link String} that the Parse should read
     * @return {@link Json}
     */
    public static Json parse(String json) {
        try {
            if (json != null && Validator.isJsonValid(json))
                return new Json(com.google.gson.JsonParser.parseString(json).getAsJsonObject());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
