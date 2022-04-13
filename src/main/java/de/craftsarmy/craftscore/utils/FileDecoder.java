package de.craftsarmy.craftscore.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

public final class FileDecoder {

    public static File decode(File in, File out) throws IOException {
        FileInputStream inputStream = new FileInputStream(in);
        return decode(Utils.readAllBytes(inputStream), out);
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
