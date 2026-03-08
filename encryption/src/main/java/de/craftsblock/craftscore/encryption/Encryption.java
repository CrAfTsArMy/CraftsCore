package de.craftsblock.craftscore.encryption;

import de.craftsblock.craftscore.encryption.api.EncryptionType;
import de.craftsblock.craftscore.encryption.api.Encryptor;
import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.Repeatable;
import java.lang.reflect.InvocationTargetException;

@Deprecated
@ApiStatus.ScheduledForRemoval
public class Encryption {

    public static Encryptor getInstance(EncryptionType type) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return type.getInstance();
    }

}
