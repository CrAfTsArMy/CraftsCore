package de.craftsblock.craftscore.encryption.api;

public abstract class Encryptor {

    public abstract String encrypt(String plaintext, String key) throws Exception;
    public abstract String decrypt(String ciphertext, String key) throws Exception;

}
