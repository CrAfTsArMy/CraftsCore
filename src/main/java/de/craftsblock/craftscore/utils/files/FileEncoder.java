package de.craftsblock.craftscore.utils.files;

import de.craftsblock.craftscore.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

public final class FileEncoder {

    public String encode(File in) throws IOException {
        FileInputStream inputStream = new FileInputStream(in);
        return encode(Utils.readAllBytes(inputStream));
    }

    public String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

}
