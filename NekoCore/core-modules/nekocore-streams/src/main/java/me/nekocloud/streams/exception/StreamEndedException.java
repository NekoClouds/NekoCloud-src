package me.nekocloud.streams.exception;

public class StreamEndedException extends StreamException {

    public StreamEndedException() {
        super("stream_ended");
    }
}
