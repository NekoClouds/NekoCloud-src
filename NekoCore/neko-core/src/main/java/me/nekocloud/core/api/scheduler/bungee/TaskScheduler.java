package me.nekocloud.core.api.scheduler.bungee;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.RequiredArgsConstructor;
import me.nekocloud.core.api.module.CoreModule;

import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class TaskScheduler {

    private final Object lock= new Object();
    private final AtomicInteger taskCounter = new AtomicInteger();
    private final Int2ObjectMap<ScheduledTask> tasks = Int2ObjectMaps.synchronize(new Int2ObjectOpenHashMap<>());
    private final Multimap<CoreModule, ScheduledTask> tasksByCorePlugin= HashMultimap.create();
    
    public void cancel(int id) {
        final ScheduledTask task = tasks.get(id);

        Preconditions.checkArgument(task != null, "No task with id %s", id);
        task.cancel();
    }

    void cancel0(ScheduledTask task) {
        synchronized (this.lock) {
            tasks.remove(task.getId());
            tasksByCorePlugin.values().remove(task);
        }
    }

    public int nextTaskId() {
        return taskCounter.incrementAndGet();
    }

    public void cancel(ScheduledTask task) {
        task.cancel();
    }

    public int cancel(CoreModule plugin) {
        int removed = 0;
        for (ScheduledTask task : new HashSet<>(tasksByCorePlugin.get(plugin))) {
            cancel(task);
            ++removed;
        }

        return removed;
    }

    public ScheduledTask runAsync(CoreModule owner, Runnable task) {
        return this.schedule(owner, task, 0L, TimeUnit.MILLISECONDS);
    }

    public ScheduledTask schedule(CoreModule owner, Runnable task, long delay, TimeUnit unit) {
        return this.schedule(owner, task, delay, 0L, unit);
    }

    public ScheduledTask schedule(CoreModule owner, Runnable task, long delay, long period, TimeUnit unit) {
        Preconditions.checkNotNull(owner, "owner");
        Preconditions.checkNotNull(task, "task");
        final ScheduledTask prepared = new ScheduledTask(this, this.nextTaskId(), owner, task, delay, period, unit);

        synchronized (lock) {
            this.tasks.put(prepared.getId(), prepared);
            this.tasksByCorePlugin.put(owner, prepared);
        }

        return this.schedule(owner.getCore().getExecutor(), prepared);
    }

    public ScheduledTask schedule(ExecutorService executorService, ScheduledTask task) {
        Preconditions.checkNotNull(executorService, "executor");
        Preconditions.checkNotNull(task, "task");

        executorService.execute(task);
        return task;
    }
}