package de.craftsarmy.craftscore.api.threading;

import de.craftsarmy.craftscore.buildin.Worker;
import de.craftsarmy.craftscore.utils.Touch;

public abstract class AbstractWorker {

    private boolean debug = false;

    public AbstractWorker(boolean debug) {
        this.debug = debug;
    }

    public abstract void shutdown();
    public abstract void submit(Class<? extends Worker.Task> clazz);
    public abstract void repeat(Class<? extends Worker.Task> clazz);
    public abstract void pause(Class<? extends Worker.Task> clazz);

    public void callback(Class<?> clazz) {
        if (debug)
            System.out.println("[Worker]: Running Task: " + clazz.getName());
    }

    public final void setDebug(boolean debug) {
        this.debug = debug;
    }

    public final boolean isDebug() {
        return debug;
    }

    public abstract static class Task extends Touch.TouchAble {

        public Task(Class<?> from) {
            super(from);
        }

        public abstract void run();

    }

}
