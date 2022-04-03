package de.craftsarmy.craftscore.api.moduls;

import de.craftsarmy.craftscore.utils.Touch;

public abstract class AbstractModul extends Touch.TouchAble {

    public AbstractModul(Class<?> from) {
        super(from);
    }

    public abstract void onEnable();
    public abstract void onDisable();

    public abstract void onTrigger(Object... args);

}
