package de.craftsblock.craftscore.utils.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class FileUtils {

    public static void delete(File f) throws IOException {
        if (f.isDirectory())
            for (File c : Objects.requireNonNull(f.listFiles()))
                delete(c);
        if (!f.delete())
            throw new FileNotFoundException("Failed to delete file: " + f);
    }

    public static void copy(File from, File to) throws IOException {
        if (from.isDirectory()) {
            to.mkdirs();
            for (File c : Objects.requireNonNull(from.listFiles()))
                copy(c, new File(to, c.getName()));
        } else {
            Path copied = to.toPath();
            Path original = from.toPath();
            Files.copy(original, copied, StandardCopyOption.REPLACE_EXISTING);
        }
    }

}
