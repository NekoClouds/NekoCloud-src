package me.nekocloud.box.type;

import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.util.Head;
import me.nekocloud.api.util.Rarity;
import me.nekocloud.base.gamer.constans.KeyType;
import me.nekocloud.base.locale.CommonWords;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.StringUtil;
import me.nekocloud.box.api.ItemBox;

public class KeysBox extends ItemBox {

    private final int keys;
    private final KeyType keyType;

    public KeysBox(int keys, Rarity rarity, KeyType keyType) {
        super(Head.getHeadByValue(keyType.getHeadValue()), rarity);
        this.keys = keys;
        this.keyType = keyType;
    }

    @Override
    public String getName(Language lang) {
        return lang.getMessage("BOX_KEYS", StringUtil.getNumberFormat(keys));
    }

    @Override
    public void onApply(BukkitGamer gamer) {
        gamer.changeKeys(keyType, keys);
    }

    @Override
    public void onMessage(BukkitGamer gamer) {
        gamer.sendActionBar("§d+" + keys + " "
                + CommonWords.KEYS_1.convert(keys, gamer.getLanguage()));
    }
}
