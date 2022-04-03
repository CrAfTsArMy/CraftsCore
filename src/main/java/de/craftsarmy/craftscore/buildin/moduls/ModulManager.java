package de.craftsarmy.craftscore.buildin.moduls;

import de.craftsarmy.craftscore.Core;
import de.craftsarmy.craftscore.api.moduls.AbstractModul;
import de.craftsarmy.craftscore.api.moduls.AbstractModulManager;
import de.craftsarmy.craftscore.utils.Touch;

import java.util.concurrent.ConcurrentLinkedQueue;

public final class ModulManager extends AbstractModulManager {

    private final Touch<AbstractModul> touch = new Touch<>(this.getClass());
    private final ConcurrentLinkedQueue<Class<? extends AbstractModul>> moduls = new ConcurrentLinkedQueue<>();

    @Override
    public void enable(Class<? extends AbstractModul> clazz) {
        if (!moduls.contains(clazz))
            try {
                touch.touch(clazz).onEnable();
                moduls.add(clazz);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    @Override
    public void disable(Class<? extends AbstractModul> clazz) {
        if (moduls.contains(clazz))
            try {
                touch.touch(clazz).onDisable();
                moduls.remove(clazz);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    @Override
    public void trigger(Object... args) {
        for (Class<? extends AbstractModul> modul : moduls)
            try {
                touch.touch(modul).onTrigger(args);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public void callback(Class<?> clazz) {
        if (Core.isDebug())
            System.out.println("[" + Thread.currentThread().getName() + "]: Touch Callback from: " + clazz.getName());
    }

}
