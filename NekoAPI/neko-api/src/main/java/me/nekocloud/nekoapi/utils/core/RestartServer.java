package me.nekocloud.nekoapi.utils.core;

import java.text.SimpleDateFormat;
import java.util.Date;

//todo переписать
@SuppressWarnings("all")
public class RestartServer extends Thread {

    private final String time;

    public RestartServer(final String restart) {
        start();
        this.time = restart;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            if ((time + ":00").contains(getCurrentTimeStamp()))
                System.exit(0);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private String getCurrentTimeStamp() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }
}
