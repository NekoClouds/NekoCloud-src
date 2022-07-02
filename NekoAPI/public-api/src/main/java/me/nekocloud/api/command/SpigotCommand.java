package me.nekocloud.api.command;

import me.nekocloud.base.gamer.constans.Group;
import org.bukkit.command.Command;

import java.util.List;

public interface SpigotCommand {

    /**
     * имя команды
     * @return - имя
     */
    String getName();

    /**
     * получить команду баккита (правда хз зачем)
     * @return - команда
     *
     */
    Command getCommand();

    /**
     * получить алиасы все
     * @return - список алиассов
     */
    List<String> getAliases();

    /**
     * минимальный левел для юзания
     * @return - левел
     */
    @Deprecated
    int getLevel();
    Group getGroup();

    /**
     * назначить минимальную группу для выполнения команды
     * @param group - группа
     */
    void setGroup(Group group);
    @Deprecated
    void setMinimalGroup(int level);
    @Deprecated
    void setMinimalGroup(Group group);

    /**
     * назначить новый интерфейс управления
     * @param commandInterface - интерфейс
     */
    void setCommandInterface(CommandInterface commandInterface);
    void setCommandTabComplete(CommandTabComplete commandTabComplete);

    /**
     * можно выполнить только во время игры
     * @param onlyGame - да или нет(нет по умолчанию)
     */
    void setOnlyGame(boolean onlyGame);

    /**
     * можно выполнить только игроку
     * @param onlyPlayers - да или нет(нет по умолчанию)
     */
    void setOnlyPlayers(boolean onlyPlayers);

    void setOnlyConsole(boolean onlyConsole);
    /**
     * поставить кулдаун
     * @param second - секунд
     * @param type - тип кулдауна
     */
    void setCooldown(int second, String type);

    /**
     * узнать кулдаун
     * @return - кулдаун в секундах
     */
    int getSecondCooldown();

    /**
     * офнуть команду
     */
    void disable();
}
