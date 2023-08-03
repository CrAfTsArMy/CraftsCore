package de.craftsblock.craftscore.utils.files;

import de.craftsblock.craftscore.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * The FileDecoder class provides methods to decode Base64-encoded data and write it to a file.
 *
 * @author CraftsBlock
 * @version 1.0
 * @since 2.10-SNAPSHOT
 * @deprecated This class and its methods have been deprecated since version "3.7.24-SNAPSHOT" and should not be used.
 * There is no specific alternative implementation provided in this version.
 * Developers are encouraged to avoid using these deprecated elements.
 */
@Deprecated(since = "3.7.24-SNAPSHOT", forRemoval = true)
public final class FileDecoder {

    /**
     * Decodes the content of the input file (Base64-encoded) and writes it to the output file.
     *
     * @param in  The input file to be decoded.
     * @param out The output file where the decoded data will be written.
     * @return The output file containing the decoded data.
     * @throws IOException If an I/O error occurs during the decoding or writing process.
     * @deprecated This method has been deprecated since version "3.7.24-SNAPSHOT" and should not be used.
     * There is no specific alternative implementation provided in this version.
     * Developers are encouraged to avoid using this deprecated method.
     */
    @Deprecated(since = "3.7.24-SNAPSHOT", forRemoval = true)
    public static File decode(File in, File out) throws IOException {
        FileInputStream inputStream = new FileInputStream(in);
        return decode(Utils.readAllBytes(inputStream), out);
    }

    /**
     * Decodes the given byte array (Base64-encoded data) and writes it to the output file.
     *
     * @param bytes The byte array containing the Base64-encoded data to be decoded.
     * @param out   The output file where the decoded data will be written.
     * @return The output file containing the decoded data.
     * @throws IOException If an I/O error occurs during the decoding or writing process.
     * @deprecated This method has been deprecated since version "3.7.24-SNAPSHOT" and should not be used.
     * There is no specific alternative implementation provided in this version.
     * Developers are encouraged to avoid using this deprecated method.
     */
    @Deprecated(since = "3.7.24-SNAPSHOT", forRemoval = true)
    public static File decode(byte[] bytes, File out) throws IOException {
        byte[] data = Base64.getDecoder().decode(bytes);
        FileOutputStream outputStream = new FileOutputStream(out);
        outputStream.write(data);
        outputStream.flush();
        outputStream.close();
        return out;
    }

}
