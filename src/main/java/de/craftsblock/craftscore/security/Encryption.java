package de.craftsblock.craftscore.security;

import de.craftsblock.craftscore.security.api.EncryptionType;
import de.craftsblock.craftscore.security.api.Encryptor;

import java.lang.reflect.InvocationTargetException;

public class Encryption {

    public static Encryptor getInstance(EncryptionType type) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return type.getInstance();
    }

}
