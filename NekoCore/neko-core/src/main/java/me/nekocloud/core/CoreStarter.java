package me.nekocloud.core;

import lombok.SneakyThrows;
import me.nekocloud.core.logger.Logger;

public final class CoreStarter extends Logger {

    @SneakyThrows
    public static void main(final String[] args) {
        // Отче наш сущий на небесах!
        // Да святится имя Твоё;
        // Да будет воя Твоя и на земле, как на небе;
        // Хлеб наш насущный дай нам на сей день;
        // И прости нам долги наши, как и мы прощаем должникам нашим;
        // И не введи нас в искушение, но избавь нас от лукавого.
        // Ибо Твоё есть Царство и сила и слава во веки.
        // Аминь.
        nekoCore.start();

        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("user.timezone", "Europe/Moscow");

        new Logger(); //запуск SimpleTerminalConsole
    }
}
