package me.nekocloud.core.api.scheduler.bungee;

import lombok.Getter;

import javax.annotation.Nullable;
import java.util.concurrent.ThreadFactory;

public class GroupedThreadFactory implements ThreadFactory {

    @Getter
    private final ThreadGroup group;

    public GroupedThreadFactory(String name) {
        this.group = new CoreGroup(name);
    }

    @Override
    public Thread newThread(@Nullable Runnable r) {
        return new Thread(this.group, r);
    }

    public static class CoreGroup extends ThreadGroup {

        private CoreGroup(String name) {
            super(name);
        }
    }
}
