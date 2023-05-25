package de.craftsblock.craftscore.queue;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class PrioritizedTimedQueue extends PrioritizedQueue {

    private final long time;
    private final ConcurrentLinkedQueue<TimedTaskMapping> taskMappings = new ConcurrentLinkedQueue<>();


    public PrioritizedTimedQueue(long time) {
        this(time, TimeUnit.MINUTES);
        taskMappings.clear();
    }

    public PrioritizedTimedQueue(long time, TimeUnit unit) {
        this.time = unit.toMillis(time);
    }

    @Override
    public Runnable poll() {
        for (TimedTaskMapping mapping : taskMappings)
            if (mapping.isMaturity()) {
                Runnable task = mapping.getTask();
                cancel(task);
                return task;
            }
        return super.poll();
    }

    @Override
    public PrioritizedQueue submit(Priority priority, Runnable task) {
        taskMappings.add(new TimedTaskMapping(time, task));
        return super.submit(priority, task);
    }

    private static class TimedTaskMapping {

        private final OffsetDateTime maturity;
        private final Runnable task;

        public TimedTaskMapping(long time, Runnable task) {
            this.maturity = OffsetDateTime.now().plus(time, ChronoUnit.MILLIS);
            this.task = task;
        }

        public boolean isMaturity() {
            return OffsetDateTime.now().isAfter(maturity);
        }

        public OffsetDateTime getMaturity() {
            return maturity;
        }

        public boolean equals(Runnable another) {
            return task.equals(another);
        }

        public Runnable getTask() {
            return task;
        }

    }

}
