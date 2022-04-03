package de.craftsarmy.craftscore.buildin.threading;

import de.craftsarmy.craftscore.api.threading.AbstractWorker;
import de.craftsarmy.craftscore.utils.Touch;

import java.util.concurrent.ConcurrentLinkedQueue;

public final class Worker extends AbstractWorker {

    private static int count = 0;

    private final Thread thread;
    private final Touch<Task> touch;

    private final ConcurrentLinkedQueue<Class<? extends Task>> tasks = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Class<? extends Task>> repeatingTasks = new ConcurrentLinkedQueue<>();

    public Worker() {
        touch = new Touch<>(this.getClass());
        thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                // Only run tasks once
                for (Class<? extends Task> task : tasks)
                    try {
                        touch.touch(task).run();
                        tasks.remove(task);
                    } catch (Exception e) {
                        e.printStackTrace();
                        tasks.remove(task);
                    }

                // Perform repeating tasks
                for (Class<? extends Task> task : repeatingTasks)
                    try {
                        touch.touch(task).run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        });
        count += 1;
        thread.setName("Worker #" + count);
        thread.start();
    }

    @Override
    public void shutdown() {
        thread.interrupt();
    }

    @Override
    public void submit(Class<? extends Task> clazz) {
        if (!tasks.contains(clazz))
            tasks.add(clazz);
    }

    @Override
    public void repeat(Class<? extends Task> clazz) {
        if (!repeatingTasks.contains(clazz))
            repeatingTasks.add(clazz);
    }

    @Override
    public void pause(Class<? extends Task> clazz) {
        repeatingTasks.remove(clazz);
    }

}
