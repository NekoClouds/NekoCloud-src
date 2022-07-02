package me.nekocloud.packetlib.libraries.usableItem;

import me.nekocloud.api.usableitem.ClickAction;
import me.nekocloud.api.usableitem.UsableAPI;
import me.nekocloud.api.usableitem.UsableItem;
import me.nekocloud.nekoapi.loader.NekoAPI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UsableAPIImpl implements UsableAPI {

    private final NekoAPI nekoAPI;

    private UsablesManager manager;

    public UsableAPIImpl(NekoAPI nekoAPI) {
        this.nekoAPI = nekoAPI;
    }

    @Override
    public UsableItem createUsableItem(ItemStack itemStack, Player owner, ClickAction clickAction) {
        return create(itemStack, owner, clickAction);
    }

    @Override
    public UsableItem createUsableItem(ItemStack itemStack, ClickAction clickAction) {
        return create(itemStack, null, clickAction);
    }

    @Override
    public void removeItem(UsableItem item) {
        item.remove();
    }

    @Override
    public List<UsableItem> getUsableItems() {
        if (manager == null) {
            return Collections.emptyList();
        }

        return new ArrayList<>(manager.getUsableItems().values());
    }


    private UsableItem create(ItemStack itemStack, Player owner, ClickAction clickAction) {
        if (manager == null) {
            manager = new UsablesManager(nekoAPI);
        }

        return new CraftUsableItem(itemStack, owner, clickAction, manager);
    }
}
