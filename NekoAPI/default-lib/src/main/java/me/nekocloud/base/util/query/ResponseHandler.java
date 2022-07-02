package me.nekocloud.base.util.query;

public interface ResponseHandler<R, O> {

    R handleResponse(O o);
}
