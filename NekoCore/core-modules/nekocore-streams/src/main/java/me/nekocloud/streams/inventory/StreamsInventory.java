package me.nekocloud.streams.inventory;

import gnu.trove.map.TIntObjectMap;
import me.nekocloud.core.api.chat.JsonChatMessage;
import me.nekocloud.core.api.chat.event.ClickEvent;
import me.nekocloud.core.api.chat.event.HoverEvent;
import me.nekocloud.core.api.inventory.BaseInventoryMarkup;
import me.nekocloud.core.api.inventory.impl.CorePaginatedInventory;
import me.nekocloud.core.api.inventory.itemstack.Material;
import me.nekocloud.core.api.inventory.itemstack.builder.ItemBuilder;
import me.nekocloud.core.api.inventory.markup.BaseInventoryBlockMarkup;
import me.nekocloud.core.api.utility.NumberUtil;
import me.nekocloud.core.common.NetworkManager;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.streams.StreamManager;
import me.nekocloud.streams.detail.AbstractStreamDetails;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

public class StreamsInventory extends CorePaginatedInventory {

    public StreamsInventory() {
        super(5, "Актуальные стримы");
    }

    @Override
    public void drawInventory(@NotNull CorePlayer player) {

        // Set inventory markup.
        BaseInventoryMarkup inventoryMarkup = new BaseInventoryBlockMarkup(inventoryRows);

        inventoryMarkup.addHorizontalRow(2, 2);
        inventoryMarkup.addHorizontalRow(3, 2);
        inventoryMarkup.addHorizontalRow(4, 2);

        setInventoryMarkup(inventoryMarkup);

        // Add streams items.
        TIntObjectMap<AbstractStreamDetails> streamDetailsMap = StreamManager.INSTANCE.getActiveStreams();

        AtomicInteger streamCounter = new AtomicInteger(1);
        streamDetailsMap.forEachEntry((playerID, streamDetails) -> {

            if (!streamDetails.isActual()) {
                return true;
            }

            String streamUrl = streamDetails.getStreamPlatform().makeBeautifulUrl(streamDetails);


            ItemBuilder itemBuilder = ItemBuilder.newBuilder(Material.SKULL_ITEM);
            itemBuilder.setDurability(3);

            itemBuilder.setDisplayName("§eСтрим #" + streamCounter.getAndIncrement());
            itemBuilder.setPlayerSkull(NetworkManager.INSTANCE.getPlayerName(playerID));

            itemBuilder.addLore("");
            itemBuilder.addLore("§7Ссылка: §e" + streamUrl);
            itemBuilder.addLore("");
            itemBuilder.addLore("§7Название: §f" + (streamDetails.getTitle().length() > 48 ? streamDetails.getTitle().substring(0, 48) + "..." : streamDetails.getTitle()));
            itemBuilder.addLore("§7Зрителей: §b" + NumberUtil.spaced(streamDetails.getViewers(), '.'));
            itemBuilder.addLore("");
            itemBuilder.addLore("§7Платформа: " + streamDetails.getStreamPlatform().getDisplayName());
            itemBuilder.addLore("");
            itemBuilder.addLore("§7Трансляция идет уже §f" + NumberUtil.getTime(System.currentTimeMillis() - streamDetails.getStartedAtServiceTime()));
            itemBuilder.addLore("");
            itemBuilder.addLore("§e▸ Нажмите, чтобы получить ссылку в чате");

            addItemToMarkup(itemBuilder.build(), (baseInventory, event) -> {

                JsonChatMessage jsonChatMessage = JsonChatMessage.create("§d§lNeko§f§lCloud §8| §fСсылка на стрим: §a" + streamUrl + "\n" +
                        " §e▸ §7Нажмите, чтобы перейти на трансляцию!");

                jsonChatMessage.addHover(HoverEvent.Action.SHOW_TEXT, "§aПерейти на трансляцию");
                jsonChatMessage.addClick(ClickEvent.Action.OPEN_URL, streamUrl);

                jsonChatMessage.sendMessage(player);
                player.closeInventory();
            });

            return true;
        });

        // Add not found item.
        if (streamCounter.get() <= 1) {
            addItem(23, ItemBuilder.newBuilder(Material.GLASS_BOTTLE)
                    .setDisplayName("§cНа сервере отсутствуют активные трансляции")
                    .addLore("§7В данный момент на сервере")
                    .addLore("§7Отсутствуют активные трансляции")
                    .build());
        }
    }

}
