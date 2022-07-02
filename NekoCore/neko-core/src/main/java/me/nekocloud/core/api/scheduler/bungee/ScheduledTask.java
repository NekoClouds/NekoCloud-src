package me.nekocloud.core.api.scheduler.bungee;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.nekocloud.core.api.module.CoreModule;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public class ScheduledTask implements Runnable {

    private final TaskScheduler sched;
    private final int id;
    private final CoreModule owner;
    private final Runnable task;
    private final long delay;
    private final long period;
    private final AtomicBoolean running;

    protected ScheduledTask(TaskScheduler scheduler, int id, CoreModule owner, Runnable task, long delay, long period, TimeUnit unit) {
        this.running = new AtomicBoolean(true);
        this.sched = scheduler;
        this.id = ((id == 0) ? scheduler.nextTaskId() : id);
        this.owner = owner;
        this.task = task;
        this.delay = unit.toMillis(delay);
        this.period = unit.toMillis(period);
    }

    public void cancel() {
        if (this.running.getAndSet(false)) {
            this.sched.cancel0(this);
        }
    }

    @Override
    public void run() {
        if (this.delay > 0L) {
            try {
                Thread.sleep(this.delay);
            }
            catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        while (this.running.get()) {
            try {
                this.task.run();
            }
            catch (Throwable t) {
                Logger.getGlobal().log(Level.SEVERE, String.format("Task %s encountered an exception", this), t);
            }
            if (this.period <= 0L) {
                break;
            }
            try {
                Thread.sleep(this.period);
            }
            catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        this.cancel();
    }

    public static ScheduledTaskBuilder builder() {
        return new ScheduledTaskBuilder();
    }

    @Override
    public String toString() {
        return "ScheduledTask(sched=" + this.getSched() + ", id=" + this.getId() + ", owner=" + this.getOwner() + ", task=" + this.getTask() + ", delay=" + this.getDelay() + ", period=" + this.getPeriod() + ", running=" + this.getRunning() + ")";
    }

    @NoArgsConstructor
    public static class ScheduledTaskBuilder {

        private TaskScheduler scheduler;
        private int id;
        private CoreModule owner;
        private Runnable task;
        private long delay;
        private long period;
        private TimeUnit unit;

        public ScheduledTaskBuilder scheduler(TaskScheduler scheduler) {
            this.scheduler = scheduler;
            return this;
        }

        public ScheduledTaskBuilder id(int id) {
            this.id = id;
            return this;
        }

        public ScheduledTaskBuilder owner(CoreModule owner) {
            this.owner = owner;
            return this;
        }

        public ScheduledTaskBuilder task(Runnable task) {
            this.task = task;
            return this;
        }

        public ScheduledTaskBuilder delay(long delay) {
            this.delay = delay;
            return this;
        }

        public ScheduledTaskBuilder period(long period) {
            this.period = period;
            return this;
        }

        public ScheduledTaskBuilder unit(TimeUnit unit) {
            this.unit = unit;
            return this;
        }

        public ScheduledTask build() {
            return new ScheduledTask(this.scheduler, this.id, this.owner, this.task, this.delay, this.period, this.unit);
        }

        @Override
        public String toString() {
            return "ScheduledTask.ScheduledTaskBuilder(scheduler=" + this.scheduler + ", id=" + this.id + ", owner=" + this.owner + ", task=" + this.task + ", delay=" + this.delay + ", period=" + this.period + ", unit=" + this.unit + ")";
        }
    }
}
