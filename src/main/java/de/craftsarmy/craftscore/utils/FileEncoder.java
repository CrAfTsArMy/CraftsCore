package de.craftsarmy.craftscore.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

public class FileEncoder {

    public String encode(File in) throws IOException {
        FileInputStream inputStream = new FileInputStream(in);
        return encode(inputStream.readAllBytes());
    }

    public String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

}
