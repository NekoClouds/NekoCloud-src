package me.nekocloud.survival.commons.commands;

import me.nekocloud.survival.commons.CommonsSurvival;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.gamer.constans.Group;

public class ReloadCommand extends CommonsCommand {

    private final CommonsSurvival commonsSurvival;

    public ReloadCommand(CommonsSurvival commonsSurvival) {
        super(CommonsSurvival.getConfigData(), false, "commons");
        this.commonsSurvival = commonsSurvival;
    }

    @Override
    public void execute(GamerEntity gamerEntity, String command, String[] strings) {
        sendMessage(gamerEntity, "Plugin CommonsSurvival by NekoCloud v" + commonsSurvival.getDescription().getVersion());
        if (strings.length == 1 && strings[0].equalsIgnoreCase("reload")) {
            if (!gamerEntity.isHuman() ||((BukkitGamer)gamerEntity).getGroup() == Group.ADMIN) {
                CommonsSurvival.getConfigData().load();
                gamerEntity.sendMessage("Конфиг плагина Alternate был перегружен!");
                gamerEntity.sendMessage("Если вы отключили или включили какую-то из параметров *System, то оно не будет работать");
                gamerEntity.sendMessage("Нужен полный рестарт сервера. Сожелею((");
            }
        }

        //todo сделать при релоаде отключение команд и норм конфиг
    }
}
