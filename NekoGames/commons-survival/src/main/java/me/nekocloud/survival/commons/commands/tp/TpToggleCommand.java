package me.nekocloud.survival.commons.commands.tp;

import me.nekocloud.survival.commons.api.User;
import me.nekocloud.survival.commons.commands.CommonsCommand;
import me.nekocloud.survival.commons.config.ConfigData;
import me.nekocloud.api.player.GamerEntity;

public class TpToggleCommand extends CommonsCommand {

    public TpToggleCommand(ConfigData configData) {
        super(configData, true, "tptoggle");
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        User user = USER_MANAGER.getUser(gamerEntity.getName());
        if (user == null) {
            return;
        }

        boolean enable = !user.isTpToggle();
        user.setTpToggle(enable);

        sendMessageLocale(gamerEntity, (enable ? "TPTOGGLE_ENABLE" : "TPTOGGLE_DISABLE"));
    }
}
