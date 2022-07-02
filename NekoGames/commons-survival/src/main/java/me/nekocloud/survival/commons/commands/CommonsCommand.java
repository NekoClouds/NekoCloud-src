package me.nekocloud.survival.commons.commands;

import me.nekocloud.survival.commons.api.CommonsSurvivalAPI;
import me.nekocloud.survival.commons.api.CommonsSurvivalGui;
import me.nekocloud.survival.commons.config.ConfigData;
import me.nekocloud.survival.commons.api.manager.UserManager;
import me.nekocloud.api.JSONMessageAPI;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.command.SpigotCommand;
import me.nekocloud.api.manager.GuiManager;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.locale.Language;

public abstract class CommonsCommand implements CommandInterface {
    protected static final GuiManager<CommonsSurvivalGui> GUI_MANAGER = CommonsSurvivalAPI.getGuiManager();
    protected static final JSONMessageAPI JSON_MESSAGE_API = NekoCloud.getJsonMessageAPI();;
    protected static final UserManager USER_MANAGER = CommonsSurvivalAPI.getUserManager();
    protected static final GamerManager GAMER_MANAGER = NekoCloud.getGamerManager();

    private final String cooldownType = getClass().getSimpleName();
    protected final ConfigData configData;
    protected final SpigotCommand spigotCommand;

    protected CommonsCommand(ConfigData configData, boolean playersOnly, String name, String... aliases) {
        this.configData = configData;
        spigotCommand = COMMANDS_API.register(name, this, aliases);
        spigotCommand.setOnlyPlayers(playersOnly);
    }

    public String getCooldownType() {
        return cooldownType;
    }

    protected void setMinimalGroup(int level) {
        setMinimalGroup(Group.getGroupByLevel(level));
    }

    protected void setMinimalGroup(Group group) {
        spigotCommand.setGroup(group);
    }

    protected void send(String key, GamerEntity gamerEntity, String displayName) {
        if (displayName == null)
            sendMessageLocale(gamerEntity, key);
        else
            sendMessageLocale(gamerEntity, key + "_TO", displayName);

    }

    public void sendMessage(GamerEntity gamerEntity, String message){
        gamerEntity.sendMessage(configData.getPrefix() + message);
    }

    public void sendMessageLocale(GamerEntity gamerEntity, String key, Object... objects){
        Language lang = gamerEntity.getLanguage();
        gamerEntity.sendMessage(configData.getPrefix() + lang.getMessage(key, objects));
    }
}
