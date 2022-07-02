package me.nekocloud.base.util.query;

public interface ThrowableResponseHandler<R, O, T extends Throwable> {

    R handleResponse(O o) throws T;
}
