package me.nekocloud.box.api;

import me.nekocloud.api.util.Rarity;
import me.nekocloud.base.gamer.constans.KeyType;

import java.util.List;

public interface ItemBoxManager {

    List<ItemBox> getItems(KeyType keyType);

    List<ItemBox> getItems(KeyType keyType, Rarity rarity);

    void addItemBox(KeyType keyType, ItemBox itemBox);

    void removeItemBoxes(KeyType keyType);
    void removeItemBox(ItemBox itemBox);
}
