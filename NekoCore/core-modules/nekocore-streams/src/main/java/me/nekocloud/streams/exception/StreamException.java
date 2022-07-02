package me.nekocloud.streams.exception;

import org.jetbrains.annotations.NotNull;

public class StreamException extends RuntimeException {

    public StreamException(@NotNull String message, Object... objects) {
        super(String.format(message, objects));
    }

}
