package me.nekocloud.core.api.command;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface ICommandManager {

	Map<String, CommandExecutor> getCommandMap();

	/**
     * Выполнить команду от имени отправтеля
     *
     * @param commandSender - отправитель
     * @param command - команда
     */
	boolean dispatchCommand(final @NotNull CommandSender commandSender, @NotNull String command);

	/**
     * Зарегистрировать команду
	 *
     * @param commandExecutor - команда
     */
	void registerCommand(final @NotNull CommandExecutor commandExecutor);

	/**
     * Проверяет, зарегистрирована ли команда
	 *
     * @param commandName - имя команды
     */
	boolean commandIsExists(final @NotNull String commandName);

	/**
     * Получить команду по ее названию
     *
     * @param commandName - имя команды
     */
	CommandExecutor getCommand(final @NotNull String commandName);
}
