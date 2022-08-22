package de.craftsblock.craftscore.api.moduls;

import de.craftsblock.craftscore.utils.Touch;

public abstract class AbstractModul extends Touch.TouchAble {

    public AbstractModul(Class<?> from) {
        super(from);
    }

    public abstract void onEnable();
    public abstract void onDisable();

    public abstract void onTrigger(Object... args);

}
