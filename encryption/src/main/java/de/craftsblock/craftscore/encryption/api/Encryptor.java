package de.craftsblock.craftscore.encryption.api;

import org.jetbrains.annotations.ApiStatus;

@Deprecated
@ApiStatus.ScheduledForRemoval
public abstract class Encryptor {

    public abstract String encrypt(String plaintext, String key) throws Exception;
    public abstract String decrypt(String ciphertext, String key) throws Exception;

}
