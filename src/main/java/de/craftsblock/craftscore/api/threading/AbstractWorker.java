package de.craftsblock.craftscore.api.threading;

import de.craftsblock.craftscore.core.Core;
import de.craftsblock.craftscore.utils.Touch;

public abstract class AbstractWorker {

    public abstract void shutdown();
    public abstract void submit(Class<? extends AbstractWorker.Task> clazz);
    public abstract void repeat(Class<? extends AbstractWorker.Task> clazz);
    public abstract void pause(Class<? extends AbstractWorker.Task> clazz);

    public void callback(Class<?> clazz) {
        if (Core.isDebug())
            System.out.println("[" + Thread.currentThread().getName() + "]: Running Task: " + clazz.getName());
    }

    public abstract static class Task extends Touch.TouchAble {

        public Task(Class<?> from) {
            super(from);
        }
        public abstract void run();

    }

}
