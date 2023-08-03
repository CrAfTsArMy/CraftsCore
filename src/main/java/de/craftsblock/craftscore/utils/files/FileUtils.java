package de.craftsblock.craftscore.utils.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

/**
 * The FileUtils class provides utility methods for file operations.
 *
 * @author CraftsBlock
 * @version 1.0
 * @since 3.5.0-SNAPSHOT
 * @deprecated This class and its methods have been deprecated since version "3.7.24-SNAPSHOT" and should not be used.
 * There is no specific alternative implementation provided in this version.
 * Developers are encouraged to avoid using these deprecated elements.
 */
@Deprecated(since = "3.7.24-SNAPSHOT", forRemoval = true)
public class FileUtils {

    /**
     * Recursively deletes a file or directory.
     *
     * @param f The file or directory to be deleted.
     * @throws IOException If an I/O error occurs during the deletion process.
     * @deprecated This method has been deprecated since version "3.7.24-SNAPSHOT" and should not be used.
     * There is no specific alternative implementation provided in this version.
     * Developers are encouraged to avoid using this deprecated method.
     */
    @Deprecated(since = "3.7.24-SNAPSHOT", forRemoval = true)
    public static void delete(File f) throws IOException {
        if (f.isDirectory())
            for (File c : Objects.requireNonNull(f.listFiles()))
                delete(c);
        if (!f.delete())
            throw new FileNotFoundException("Failed to delete file: " + f);
    }

    /**
     * Recursively copies the content of a file or directory to a target location.
     *
     * @param from The source file or directory to be copied.
     * @param to   The destination file or directory where the content will be copied.
     * @throws IOException If an I/O error occurs during the copy process.
     * @deprecated This method has been deprecated since version "3.7.24-SNAPSHOT" and should not be used.
     * There is no specific alternative implementation provided in this version.
     * Developers are encouraged to avoid using this deprecated method.
     */
    @Deprecated(since = "3.7.24-SNAPSHOT", forRemoval = true)
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
