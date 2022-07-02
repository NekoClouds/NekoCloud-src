package me.nekocloud.box;

import me.nekocloud.api.util.Rarity;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.gamer.constans.KeyType;
import me.nekocloud.box.api.BoxAPI;
import me.nekocloud.box.api.ItemBoxManager;
import me.nekocloud.box.listener.BoxListener;
import me.nekocloud.box.type.GroupBox;
import me.nekocloud.box.type.KeysBox;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class BoxPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        new BoxListener(this);

        ItemBoxManager itemBoxManager = BoxAPI.getItemBoxManager();
        itemBoxManager.addItemBox(KeyType.GROUP_KEY, new GroupBox(Group.HEGENT, 4, Rarity.COMMON));
        itemBoxManager.addItemBox(KeyType.GROUP_KEY, new GroupBox(Group.AKIO, 3, Rarity.RARE));
        itemBoxManager.addItemBox(KeyType.GROUP_KEY, new GroupBox(Group.TRIVAL, 5, Rarity.EPIC));
        itemBoxManager.addItemBox(KeyType.GROUP_KEY, new GroupBox(Group.AXSIDE, 14, Rarity.LEGENDARY));
        itemBoxManager.addItemBox(KeyType.GROUP_KEY, new GroupBox(Group.NEKO, 5, Rarity.LEGENDARY));
        itemBoxManager.addItemBox(KeyType.GROUP_KEY, new KeysBox(2, Rarity.RARE, KeyType.GROUP_KEY));
        itemBoxManager.addItemBox(KeyType.GROUP_KEY, new KeysBox(3, Rarity.EPIC, KeyType.GROUP_KEY));
        itemBoxManager.addItemBox(KeyType.GROUP_KEY, new KeysBox(5, Rarity.LEGENDARY, KeyType.GROUP_KEY));
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }
}
