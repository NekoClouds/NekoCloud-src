package me.nekocloud.myserver.inventory;

import lombok.val;
import me.nekocloud.core.api.chat.ChatColor;
import me.nekocloud.core.api.inventory.MouseAction;
import me.nekocloud.core.api.inventory.impl.CorePaginatedInventory;
import me.nekocloud.core.api.inventory.itemstack.Material;
import me.nekocloud.core.api.inventory.itemstack.builder.ItemBuilder;
import me.nekocloud.core.api.inventory.markup.BaseInventorySimpleMarkup;
import me.nekocloud.core.api.utility.DateUtil;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.myserver.type.PlayerMyServer;
import org.jetbrains.annotations.NotNull;

public class ServerManagementInventory extends CorePaginatedInventory {

    private final PlayerMyServer playerMyServer;

    public ServerManagementInventory(@NotNull PlayerMyServer playerMyServer) {
        super(6, playerMyServer.getServerName());

        this.playerMyServer = playerMyServer;
    }

    @Override
    public void drawInventory(CorePlayer player) {
        setInventoryMarkup(new BaseInventorySimpleMarkup(inventoryRows));

        getInventoryMarkup().addHorizontalRow(4, 2);
        getInventoryMarkup().addHorizontalRow(5, 2);

        // Черная рамка
        for (int i = 1; i <= 18; i++) {

            if (i == 10 || i == 18)
                continue;

            addItem(i, ItemBuilder.newBuilder(Material.STAINED_GLASS_PANE)
                    .setDurability(7)
                    .setDisplayName(ChatColor.RESET.toString())
                    .build());
        }

        for (int i = 46; i <= 54; i++) {
            addItem(i, ItemBuilder.newBuilder(Material.STAINED_GLASS_PANE)
                    .setDurability(7)
                    .setDisplayName(ChatColor.RESET.toString())
                    .build());
        }

        addItem(37, ItemBuilder.newBuilder(Material.STAINED_GLASS_PANE)
                .setDurability(7)
                .setDisplayName(ChatColor.RESET.toString())
                .build());

        addItem(45, ItemBuilder.newBuilder(Material.STAINED_GLASS_PANE)
                .setDurability(7)
                .setDisplayName(ChatColor.RESET.toString())
                .build());


        // Список игроков
        for (int slot : getInventoryMarkup().getMarkupList().toArray())
            addItem(slot, ItemBuilder.newBuilder(Material.BARRIER)
                    .setDisplayName("§cПустой игровой слот")
                    .build());

        for (val onlinePlayer : playerMyServer.getCoreServer().getOnlinePlayers()) {

            val itemBuilder = ItemBuilder.newBuilder(Material.SKULL_ITEM)
                    .setDurability(3)
                    .setPlayerSkull(onlinePlayer.getName());

            itemBuilder.setDisplayName(onlinePlayer.getDisplayName()
                    + (onlinePlayer.getName().equalsIgnoreCase(player.getName()) ? " §7(Вы)" : ""));

            itemBuilder.addLore("§7Статус: " + (playerMyServer.isLeader(onlinePlayer) ? "§cЛидер" : playerMyServer.isModer(onlinePlayer) ? "§bМодератор" : "§fИгрок"));
            itemBuilder.addLore("");
            itemBuilder.addLore("§7Игровой уровень: §b" + onlinePlayer.getLevelNetwork());
            itemBuilder.addLore("");
            itemBuilder.addLore("§7Версия клиента: §f" + onlinePlayer.getVersionName());
            itemBuilder.addLore("§7Игровой язык: §f" + onlinePlayer.getLanguage().getName());
            itemBuilder.addLore("");

            if (!playerMyServer.isLeader(onlinePlayer)) {

                if (playerMyServer.isModer(player)) {
                    itemBuilder.addLore("§e▸ Нажмите, чтобы кикнуть");

                } else if (playerMyServer.isLeader(player)) {

                    itemBuilder.addLore("§e▸ Нажмите ЛКМ, чтобы кикнуть");
                    itemBuilder.addLore("§e▸ Нажмите ПКМ, чтобы " + (playerMyServer.isModer(onlinePlayer) ? "снять модератора" : "назначить модератором"));
                }

            } else {

                itemBuilder.addLore("§cЛюбые действия с лидером");
                itemBuilder.addLore("§cсервера - запрещены");
            }

            addItemToMarkup(itemBuilder.build(), (inventory, event) -> {
                if (playerMyServer.isLeader(onlinePlayer)) {
                    return;
                }

                if (event.getMouseAction().equals(MouseAction.RIGHT) && playerMyServer.isLeader(player)) {

                    if (playerMyServer.isModer(onlinePlayer)) {
                        playerMyServer.removeModer(onlinePlayer);

                    } else {

                        playerMyServer.addModer(onlinePlayer);
                    }

                    updateInventory(player);
                    return;
                }

                if (onlinePlayer.getName().equalsIgnoreCase(player.getName())) {
                    player.sendMessage("§cОшибка, Вы не можете кикинуть самого себя!");
                    return;
                }

                onlinePlayer.connectToAnyServer(playerMyServer.getServerType().getCategory().getLobbyPrefix());
                onlinePlayer.sendMessage("§cВы были кикнуты администратором сервера " + playerMyServer.getServerName());

                updateInventory(player);
            });
        }

        // Управление сервером
        addItem(11, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                        .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmE4ZjZiMTMxZWY4NDdkOTE2MGU1MTZhNmY0NGJmYTkzMjU1NGQ0MGMxOGE4MTc5NmQ3NjZhNTQ4N2I5ZjcxMCJ9fX0=")
                        .setDurability(3)

                        .setDisplayName("§aПринудительно запустить игру")
                        .addLore("§7▸ Нажмите, чтобы запустить игру на арене!")

                        .build(),
                (player1, event) -> {
                    player.sendMessage("§6§lMyServer §8:: §fНа сервер была отправлена команда запуска игровой арены");
                    player.closeInventory();

                    playerMyServer.getCoreServer().dispatchCommand("start");
                });

        addItem(12, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                        .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmViNTg4YjIxYTZmOThhZDFmZjRlMDg1YzU1MmRjYjA1MGVmYzljYWI0MjdmNDYwNDhmMThmYzgwMzQ3NWY3In19fQ==")
                        .setDurability(3)

                        .setDisplayName("§cПринудительно остановить сервер")
                        .addLore("§7▸ Нажмите, чтобы выключить и удалить сервер!")

                        .build(),
                (player1, event) -> {

                    if (!playerMyServer.isLeader(player)) {
                        player.sendMessage("§cОшибка, у Вас недостаточно прав для выполнения данного процесса!");
                        return;
                    }

                    for (CorePlayer onlinePlayer : playerMyServer.getCoreServer().getOnlinePlayers())
                        onlinePlayer.connectToAnyServer(playerMyServer.getServerType().getCategory().getLobbyPrefix());

                    playerMyServer.shutdown();
                });


        addItem(5, ItemBuilder.newBuilder(Material.SIGN)

                .setDisplayName(ChatColor.GREEN + playerMyServer.getServerName())
                .addLore("")
                .addLore("§8Информация:")
                .addLore(" §7Был запущен §f" + DateUtil.formatTime(playerMyServer.getStartMillis(), DateUtil.DEFAULT_DATETIME_PATTERN))
                .addLore(" §7Категория: §e" + playerMyServer.getServerType().getCategory().getName())
                .addLore("")
                .addLore("§8Статистика:")
                .addLore(" §7Версия ядра: §a" + playerMyServer.getCoreServer().getVersionName())
                .addLore(" §7Онлайн: §a" + playerMyServer.getCoreServer().getOnlineCount())
                .addLore(" §7Модераторов: §a" + playerMyServer.getModeratorCollection().size())

                .build());


        addItem(16, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                        .setDurability(3)
                        .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWNiN2MyMWNjNDNkYzE3Njc4ZWU2ZjE2NTkxZmZhYWIxZjYzN2MzN2Y0ZjZiYmQ4Y2VhNDk3NDUxZDc2ZGI2ZCJ9fX0=")

                        .setDisplayName("§eТелепортировать на сервер Party")
                        .addLore("§7▸ Нажмите, чтобы переместить Party группу на данный сервер!")

                        .build(),
                (player1, event) -> player.sendMessage("soon..."));

        addItem(17, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                .setDurability(3)
                .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWRjZjI5NjI4NzhjYmY3ZTE0NWM5MmVmY2MxYzYzYmZkMjRkYzEyNmZjNTE5ZDA4YjE4MzE4ZTRiMWE0ZSJ9fX0=")

                .setDisplayName("§eСписок модераторов " + (playerMyServer.getModeratorCollection().isEmpty() ? "§c(пусто)" : "§7(" + playerMyServer.getModeratorCollection().size() + ")"))
                .addLore(playerMyServer.getModeratorCollection().stream()
                        .map(CorePlayer -> "§8▸ " + player.getDisplayName()).toArray(value -> new String[playerMyServer.getModeratorCollection().size()]))

                .build());
    }

}
