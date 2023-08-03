package de.craftsblock.craftscore.utils.files;

import de.craftsblock.craftscore.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * The FileEncoder class provides methods to encode the content of a file to Base64.
 *
 * @author CraftsBlock
 * @version 1.0
 * @since 2.10-SNAPSHOT
 * @deprecated This class and its methods have been deprecated since version "3.7.24-SNAPSHOT" and should not be used.
 * There is no specific alternative implementation provided in this version.
 * Developers are encouraged to avoid using these deprecated elements and switch to recommended replacements.
 */
@Deprecated(since = "3.7.24-SNAPSHOT", forRemoval = true)
public final class FileEncoder {

    /**
     * Encodes the content of the input file to Base64.
     *
     * @param in The input file to be encoded.
     * @return A Base64-encoded string representing the content of the input file.
     * @throws IOException If an I/O error occurs during the encoding process.
     * @deprecated This method has been deprecated since version "3.7.24-SNAPSHOT" and should not be used.
     * There is no specific alternative implementation provided in this version.
     * Developers are encouraged to avoid using this deprecated method and switch to recommended replacements.
     */
    @Deprecated(since = "3.7.24-SNAPSHOT", forRemoval = true)
    public String encode(File in) throws IOException {
        FileInputStream inputStream = new FileInputStream(in);
        return encode(Utils.readAllBytes(inputStream));
    }

    /**
     * Encodes the given byte array to Base64.
     *
     * @param data The byte array to be encoded.
     * @return A Base64-encoded string representing the input byte array.
     * @deprecated This method has been deprecated since version "3.7.24-SNAPSHOT" and should not be used.
     * There is no specific alternative implementation provided in this version.
     * Developers are encouraged to avoid using this deprecated method and switch to recommended replacements.
     */
    @Deprecated(since = "3.7.24-SNAPSHOT", forRemoval = true)
    public String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

}
