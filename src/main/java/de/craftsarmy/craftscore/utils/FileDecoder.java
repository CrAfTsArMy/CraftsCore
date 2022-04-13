package de.craftsarmy.craftscore.utils;

import java.io.*;
import java.util.Base64;

public class FileDecoder {

    public static File decode(File in, File out) throws IOException {
        FileInputStream inputStream = new FileInputStream(in);
        return decode(inputStream.readAllBytes(), out);
    }

    public static File decode(byte[] bytes, File out) throws IOException {
        byte[] data = Base64.getDecoder().decode(bytes);
        FileOutputStream outputStream = new FileOutputStream(out);
        outputStream.write(data);
        outputStream.flush();
        outputStream.close();
        return out;
    }

}
