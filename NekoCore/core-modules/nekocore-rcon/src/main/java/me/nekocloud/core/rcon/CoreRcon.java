package me.nekocloud.core.rcon;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import me.nekocloud.core.api.chat.ChatColor;
import me.nekocloud.core.api.module.CoreModule;
import me.nekocloud.core.api.module.CoreModuleInfo;
import me.nekocloud.core.rcon.server.RconServer;

import java.net.InetSocketAddress;

@Log4j2
@CoreModuleInfo(name = "CoreRcon", author = "challengere")
public class CoreRcon extends CoreModule {

    private RconServer server;

    @Override
    @SneakyThrows
    public void onEnable() {
        val address = new InetSocketAddress("51.178.156.252", 1877);
        server = new RconServer("qwerty123_ya_lublu_tvoyou_mamu");

        log.info("[CoreRcon] Запуск rcon на адресе: " + address);

        val channel = server.bind(address).awaitUninterruptibly().channel();
        if (!channel.isActive())
            log.warn(ChatColor.YELLOW + "[CoreRcon] Ошибка бинда порта rcon. Адрес уже используется?");
    }

    @Override
    public void onDisable() {
        server.shutdown();
    }
}
