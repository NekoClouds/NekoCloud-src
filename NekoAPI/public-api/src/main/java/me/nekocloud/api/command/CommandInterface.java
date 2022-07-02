package me.nekocloud.api.command;

import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.api.player.GamerManager;

public interface CommandInterface {

    /**
     * Выполнение команды
     * @param gamerEntity - объект того, кто исполняет
     * @param command - то, что ввели
     * @param args - аргументы(кроме самой команды)
     */
    void execute(GamerEntity gamerEntity, String command, String[] args);

    /**
     * Получить интерфейс для регистрации команд
     *
     * @return интерфейс
     */
    CommandsAPI COMMANDS_API = NekoCloud.getCommandsAPI();

    /**
     * ПОлучить интерфейс для работы с геймерами
     */
    GamerManager GAMER_MANAGER = NekoCloud.getGamerManager();


}
