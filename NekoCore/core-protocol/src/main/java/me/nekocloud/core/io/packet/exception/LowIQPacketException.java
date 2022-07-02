package me.nekocloud.core.io.packet.exception;

public class LowIQPacketException extends RuntimeException {

    public LowIQPacketException(String message) {
        super(message);
    }

    public LowIQPacketException(String message, Throwable cause) {
        super(message, cause);
    }
}
