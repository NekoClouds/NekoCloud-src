package me.nekocloud.api.exeption;

public class NpcErrorTypeException extends IllegalArgumentException {

    public NpcErrorTypeException(String message) {
        super(message);
    }
}
