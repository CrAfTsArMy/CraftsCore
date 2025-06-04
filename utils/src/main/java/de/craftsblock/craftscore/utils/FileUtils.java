package de.craftsblock.craftscore.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A utility class for file-related operations, such as retrieving the oldest files
 * from a folder based on their modification times.
 *
 * @author Philipp Maywald
 * @author CraftsBlock
 * @version 1.0.0
 * @since 3.7.67
 */
public class FileUtils {

    /**
     * Retrieves the oldest n files from a specified folder with a default search depth of 1.
     *
     * @param folder the {@link Path} of the folder to search within.
     * @param amount the maximum number of oldest files to retrieve.
     * @return a list of the {@link Path} objects representing the oldest files, sorted
     * in ascending order by modification date.
     * @throws RuntimeException if an {@link IOException} occurs while accessing the folder.
     */
    public static List<Path> getOldestNFiles(@NotNull Path folder, @Range(from = 0, to = Long.MAX_VALUE) long amount) {
        return getOldestNFiles(folder, amount, 1);
    }

    /**
     * Retrieves the oldest n files from a specified folder with a configurable search depth.
     *
     * @param folder ihe {@link Path} of the folder to search within.
     * @param amount the maximum number of oldest files to retrieve.
     * @param depth  the maximum depth of directory traversal; 0 means only the folder itself.
     * @return a list of {@link Path} objects representing the oldest files, sorted
     * in ascending order by modification date.
     * @throws RuntimeException if an {@link IOException} occurs while accessing the folder.
     */
    public static List<Path> getOldestNFiles(@NotNull Path folder, @Range(from = 0, to = Long.MAX_VALUE) long amount, @Range(from = 0, to = Long.MAX_VALUE) int depth) {
        try (Stream<Path> paths = Files.walk(folder, depth)) {
            return paths.filter(Files::isRegularFile)
                    .sorted(Comparator.comparingLong(p -> {
                        try {
                            return Files.getLastModifiedTime(p).toMillis();
                        } catch (IOException e) {
                            throw new UncheckedIOException("Failed to get last modified time for: " + p, e);
                        }
                    }))
                    .limit(amount)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to retrieve files from: " + folder, e);
        }
    }

}
