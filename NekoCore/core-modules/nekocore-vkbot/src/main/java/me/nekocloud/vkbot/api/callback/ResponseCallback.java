package me.nekocloud.vkbot.api.callback;

public interface ResponseCallback {

    ResponseCallback EMPTY_CALLBACK = new ResponseCallback() {
        @Override
        public void onResponse(String result) {

        }

        @Override
        public void onException(Exception exception) {
            exception.printStackTrace();
        }
    };

    void onResponse(String result);

    void onException(Exception exception);

}
