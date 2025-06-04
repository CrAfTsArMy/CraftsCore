package de.craftsblock.craftscore.encryption.methods;

import de.craftsblock.craftscore.encryption.api.Encryptor;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

public class AES extends Encryptor {

    public String encrypt(String plaintext, String password) throws Exception {
        byte[] key = sha256(password);
        byte[] iv = secureRandomBytes(16);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec parameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, parameterSpec);

        byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        byte[] hmac = hmacSha256(concatenateArrays(ciphertext, iv), key);

        byte[] result = concatenateArrays(iv, hmac, ciphertext);
        return bytesToHex(result);
    }

    public String decrypt(String ivHashCiphertext, String password) throws Exception {
        byte[] ivHashCiphertextBytes = hexToBytes(ivHashCiphertext);
        byte[] iv = Arrays.copyOfRange(ivHashCiphertextBytes, 0, 16);
        byte[] hash = Arrays.copyOfRange(ivHashCiphertextBytes, 16, 48);
        byte[] ciphertext = Arrays.copyOfRange(ivHashCiphertextBytes, 48, ivHashCiphertextBytes.length);
        byte[] key = sha256(password);

        if (!MessageDigest.isEqual(hmacSha256(concatenateArrays(ciphertext, iv), key), hash)) {
            return null;
        }

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec parameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, parameterSpec);

        byte[] plaintext = cipher.doFinal(ciphertext);
        return new String(plaintext, StandardCharsets.UTF_8);
    }

    private byte[] sha256(String input) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    private byte[] secureRandomBytes(int length) {
        byte[] bytes = new byte[length];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(bytes);
        return bytes;
    }

    private byte[] hmacSha256(byte[] input, byte[] key) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKeySpec);
        return mac.doFinal(input);
    }

    private byte[] concatenateArrays(byte[]... arrays) {
        int length = 0;
        for (byte[] array : arrays) {
            length += array.length;
        }

        byte[] result = new byte[length];
        int destPos = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, result, destPos, array.length);
            destPos += array.length;
        }

        return result;
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            stringBuilder.append(String.format("%02x", b));
        }
        return stringBuilder.toString();
    }

    private byte[] hexToBytes(String hexString) {
        int length = hexString.length();
        byte[] byteArray = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            byteArray[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return byteArray;
    }

}
