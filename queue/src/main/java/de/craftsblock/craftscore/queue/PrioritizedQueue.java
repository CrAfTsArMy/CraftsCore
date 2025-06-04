package de.craftsblock.craftscore.queue;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PrioritizedQueue extends Queue {

    private final ConcurrentHashMap<Priority, ConcurrentLinkedQueue<Runnable>> tasks = new ConcurrentHashMap<>();

    @Override
    public Runnable poll() {
        for (Priority priority : Priority.values()) {
            ConcurrentLinkedQueue<Runnable> taskQueue = tasks.get(priority);
            if (taskQueue != null && !taskQueue.isEmpty())
                return taskQueue.poll();
        }
        return null;
    }

    @Override
    public PrioritizedQueue submit(Runnable task) {
        return submit(Priority.NORMAL, task);
    }

    public PrioritizedQueue submit(Priority priority, Runnable task) {
        ConcurrentLinkedQueue<Runnable> runnables;
        if (tasks.containsKey(priority))
            runnables = tasks.get(priority);
        else runnables = new ConcurrentLinkedQueue<>();
        runnables.add(task);
        tasks.put(priority, runnables);
        return this;
    }

    @Override
    public boolean cancel(Runnable task) {
        for (Priority priority : Priority.values()) {
            ConcurrentLinkedQueue<Runnable> taskQueue = tasks.get(priority);
            if (taskQueue != null && !taskQueue.isEmpty())
                for(Runnable t : taskQueue) {
                    if(!t.equals(task))
                        continue;
                    taskQueue.remove(task);
                    return true;
                }
        }
        return false;
    }

    @Override
    public int size() {
        int size = 0;
        for (Priority priority : Priority.values()) {
            ConcurrentLinkedQueue<Runnable> runnable = tasks.get(priority);
            if (runnable != null)
                size += runnable.size();
        }
        return size;
    }

    public enum Priority {
        MONITOR,
        HIGHEST,
        HIGH,
        NORMAL,
        LOW,
        LOWEST
    }

}
