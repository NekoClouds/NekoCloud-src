package me.nekocloud.core.api.module;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.command.CommandExecutor;
import me.nekocloud.core.api.event.Event;
import me.nekocloud.core.api.event.EventListener;
import me.nekocloud.core.io.packet.bungee.BungeeCommandRegister;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Getter
public class CoreModuleManagement {

    CoreModule coreModule;

    Collection<EventListener> eventListenerCollection                  = new ArrayList<>();
    Collection<CommandExecutor> commandCollection                      = new ArrayList<>();

    public void registerCommand(final @NotNull CommandExecutor commandExecutor) {
        commandCollection.add(commandExecutor);

        NekoCore.getInstance().getCommandManager().registerCommand(commandExecutor);
    }

    public void registerListener(final @NotNull EventListener eventListener) {
        eventListenerCollection.add(eventListener);

        NekoCore.getInstance().getEventManager().register(eventListener);
    }

    public void unregisterCommands() {
        val commandManager = NekoCore.getInstance().getCommandManager();

        for (val commandExecutor : commandCollection) {
            commandManager.getCommandMap().remove(commandExecutor.getCommand().toLowerCase());

            for (val bungee : NekoCore.getInstance().getBungeeServers())
                bungee.sendPacket(new BungeeCommandRegister(
                        commandExecutor.getCommand(),
                        commandExecutor.getAliases(),
                        BungeeCommandRegister.Action.UNREGISTER
                ));

            for (val alias : commandExecutor.getAliases())
                commandManager.getCommandMap().remove(alias.toLowerCase());

        }

        commandCollection.clear();
    }

    public void unregisterListeners() {
        val eventManager = NekoCore.getInstance().getEventManager();
        for (val eventListener : eventListenerCollection) {
            eventManager.unregister(eventListener);
        }

        eventListenerCollection.clear();
    }

    public <T extends Event> T callEvent(T event) {
        Preconditions.checkNotNull(event, "event");
        NekoCore.getInstance().getEventManager().post(event);
        event.postCall();
        return event;
    }
}
