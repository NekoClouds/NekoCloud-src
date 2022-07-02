package me.nekocloud.core.io.compress.zlib;

public class NativeCodeException extends Exception
{

    public NativeCodeException(String message, int reason)
    {
        super( message + " : " + reason );
    }
}
