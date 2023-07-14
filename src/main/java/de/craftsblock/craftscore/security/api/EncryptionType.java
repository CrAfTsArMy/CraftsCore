package de.craftsblock.craftscore.security.api;

import de.craftsblock.craftscore.security.methods.AES;

import java.lang.reflect.InvocationTargetException;

public enum EncryptionType {

    AES(AES.class);

    private final Class<? extends Encryptor> clazz;

    EncryptionType(Class<? extends Encryptor> clazz) {
        this.clazz = clazz;
    }

    public Encryptor getInstance() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return (Encryptor) clazz.getDeclaredConstructor().newInstance();
    }

}
