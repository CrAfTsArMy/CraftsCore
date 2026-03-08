package de.craftsblock.craftscore.encryption.api;

import de.craftsblock.craftscore.encryption.methods.AES;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.InvocationTargetException;

@Deprecated
@ApiStatus.ScheduledForRemoval
public enum EncryptionType {

    AES(AES.class);

    private final Class<? extends Encryptor> clazz;

    EncryptionType(Class<? extends Encryptor> clazz) {
        this.clazz = clazz;
    }

    public Encryptor getInstance() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return clazz.getDeclaredConstructor().newInstance();
    }

}
