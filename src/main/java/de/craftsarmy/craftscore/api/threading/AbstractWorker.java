package de.craftsarmy.craftscore.api.threading;

import de.craftsarmy.craftscore.Core;
import de.craftsarmy.craftscore.buildin.threading.Worker;
import de.craftsarmy.craftscore.utils.Touch;

public abstract class AbstractWorker {

    public abstract void shutdown();
    public abstract void submit(Class<? extends Worker.Task> clazz);
    public abstract void repeat(Class<? extends Worker.Task> clazz);
    public abstract void pause(Class<? extends Worker.Task> clazz);

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
