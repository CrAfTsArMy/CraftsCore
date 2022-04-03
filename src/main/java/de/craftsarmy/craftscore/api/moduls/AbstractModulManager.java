package de.craftsarmy.craftscore.api.moduls;

public abstract class AbstractModulManager {

    public abstract void enable(Class<? extends AbstractModul> clazz);
    public abstract void disable(Class<? extends AbstractModul> clazz);

    public abstract void trigger(Object... args);

}
