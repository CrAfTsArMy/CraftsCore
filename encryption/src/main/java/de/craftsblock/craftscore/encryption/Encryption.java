package de.craftsblock.craftscore.encryption;

import de.craftsblock.craftscore.encryption.api.EncryptionType;
import de.craftsblock.craftscore.encryption.api.Encryptor;

import java.lang.reflect.InvocationTargetException;

public class Encryption {

    public static Encryptor getInstance(EncryptionType type) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return type.getInstance();
    }

}
