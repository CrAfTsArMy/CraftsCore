package de.craftsblock.craftscore.queue;

import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.ConcurrentLinkedQueue;

@Deprecated
@ApiStatus.ScheduledForRemoval
public class Queue {

    private final ConcurrentLinkedQueue<Runnable> tasks = new ConcurrentLinkedQueue<>();

    public Runnable poll() {
        return tasks.poll();
    }

    public Queue submit(Runnable task) {
        tasks.add(task);
        return this;
    }

    public boolean cancel(Runnable task) {
        if(!tasks.contains(task))
            return false;
        tasks.remove(task);
        return true;
    }

    public int size() {
        return tasks.size();
    }

}
