package de.craftsblock.craftscore.queue;

import java.util.Collection;
import java.util.EnumMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PrioritizedQueue extends Queue {

    private final EnumMap<Priority, ConcurrentLinkedQueue<Runnable>> tasks = new EnumMap<>(Priority.class);

    @Override
    public Runnable poll() {
        for (ConcurrentLinkedQueue<Runnable> queue : tasks.values()) {
            if (queue.isEmpty()) continue;
            return queue.poll();
        }

        return null;
    }

    @Override
    public PrioritizedQueue submit(Runnable task) {
        return submit(Priority.NORMAL, task);
    }

    public PrioritizedQueue submit(Priority priority, Runnable task) {
        tasks.computeIfAbsent(priority, p -> new ConcurrentLinkedQueue<>())
                .add(task);

        return this;
    }

    @Override
    public boolean cancel(Runnable task) {
        tasks.values().forEach(runnables -> runnables.removeIf(task::equals));
        return false;
    }

    @Override
    public int size() {
        return tasks.values().stream().mapToInt(Collection::size).sum();
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
