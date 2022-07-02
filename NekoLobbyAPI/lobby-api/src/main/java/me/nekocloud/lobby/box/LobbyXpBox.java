package me.nekocloud.lobby.box;

import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.util.Rarity;
import me.nekocloud.lobby.utils.LevelUtils;
import me.nekocloud.box.type.XpBox;

public class LobbyXpBox extends XpBox {

    public LobbyXpBox(int exp, Rarity rarity) {
        super(exp, rarity);
    }

    @Override
    public void onMessage(BukkitGamer gamer) {
        LevelUtils.setExpData(gamer);
        super.onMessage(gamer);
    }
}
