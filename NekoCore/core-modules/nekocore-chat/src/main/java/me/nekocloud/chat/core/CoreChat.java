package me.nekocloud.chat.core;

import me.nekocloud.chat.core.command.DonateChatCommand;
import me.nekocloud.chat.core.command.StaffChatCommand;
import me.nekocloud.chat.core.command.pm.IgnoreCommand;
import me.nekocloud.chat.core.command.pm.PrivateMessageCommand;
import me.nekocloud.chat.core.command.pm.ReplyCommand;
import me.nekocloud.chat.core.manager.IgnoreManager;
import me.nekocloud.chat.packet.IgnoreListPacket;
import me.nekocloud.core.api.module.CoreModule;
import me.nekocloud.core.api.module.CoreModuleInfo;
import me.nekocloud.core.io.packet.PacketProtocol;

@CoreModuleInfo(name = "CoreChat", author = "_Novit_")
public class CoreChat extends CoreModule {

    private IgnoreManager ignoreManager;

    @Override
    protected void onEnable() {
        ignoreManager = new IgnoreManager();
        registerCommands();

        getCore().getTaskScheduler().runAsync(this, () ->
                getCore().getPlayerManager().getCorePlayerMap().valueCollection().forEach(
                        corePlayer -> corePlayer.addData("ignore_list",
                        ignoreManager.getIgnoreList(corePlayer.getPlayerID())))
        );

        PacketProtocol.BUKKIT.getPacketMapper().registerPacket(0x17, IgnoreListPacket.class);

        getManagement().registerListener(new PlayerListener(this, ignoreManager));
    }

    @Override
    protected void onDisable() {
    }

    void registerCommands() {
        getManagement().registerCommand(new DonateChatCommand());
        getManagement().registerCommand(new StaffChatCommand());

        getManagement().registerCommand(new IgnoreCommand(ignoreManager));
        getManagement().registerCommand(new PrivateMessageCommand());
        getManagement().registerCommand(new ReplyCommand());
    }

}
