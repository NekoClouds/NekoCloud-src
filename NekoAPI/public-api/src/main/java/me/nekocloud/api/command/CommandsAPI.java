package me.nekocloud.api.command;

import me.nekocloud.api.player.GamerEntity;
import org.bukkit.command.Command;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface CommandsAPI {
    /**
     * Регистрация новой команды
     * @param name название команды (без слеша)
     * @param aliases алиасы команды
     * @return SpigotCommand
     */
    SpigotCommand register(final String name, final CommandInterface commandInterface, final String... aliases);

    /**
     * получить отсортированный список который надо показывать при нажатии таб
     * @param seeList - начальный список
     * @param args - аргументы команды
     * @return - отсортированный
     */
    List<String> getCompleteString(final Collection<String> seeList, final String[] args);

    /**
     * отключить команды
     * @param command - команда
     */
    void disableCommand(final SpigotCommand command);
    void disableCommand(final String name);
    void disableCommand(final Command command);
    void disableCommands(final Plugin plugin);

    /**
     * получить бакитовскую команду
     * @param name - имя команды
     * @return - команда
     */
    Command getCommand(final String name);

    /**
     * получить все зареганные команды
     * @return - команды
     */
    Map<String, SpigotCommand> getCommands();

    /**
     * вывести сообщение недостаточно аргументов
     * @param gamerEntity кому
     * @param prefix ключ-префикс из локализации
     * @param key ключ локализации
     */
    void notEnoughArguments(final GamerEntity gamerEntity, final String prefix, final String key);

    void notEnoughArguments(final GamerEntity entity, final String label);
    /**
     * вывести сообщение игрок оффлайн
     * @param gamerEntity - кому
     * @param name - кто оффлайн
     */
    void playerOffline(final GamerEntity gamerEntity, final String name);

    /**
     * вывести сообщение игрок никогда не играл
     * @param gamerEntity - кому вывести
     * @param name - кто не играл
     */
    void playerNeverPlayed(final GamerEntity gamerEntity, final String name);

    /**
     * показать помощь по команде
     * @param gamerEntity - кому
     * @param command - команда
     * @param key - ключ локализации
     */
    void showHelp(final GamerEntity gamerEntity, final String command, final String key);
}
