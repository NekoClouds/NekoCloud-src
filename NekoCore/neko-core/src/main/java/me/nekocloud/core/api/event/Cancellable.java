package me.nekocloud.core.api.event;

public interface Cancellable {

    boolean isCancelled();

    void setCancelled(boolean cancel);
}

