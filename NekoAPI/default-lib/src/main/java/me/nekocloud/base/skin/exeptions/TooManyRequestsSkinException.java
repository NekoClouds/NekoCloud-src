package me.nekocloud.base.skin.exeptions;

public class TooManyRequestsSkinException extends SkinRequestException {

    public TooManyRequestsSkinException() {
        super("Превышен лимит запросов");
    }
}
