package me.nekocloud.reports.inventory;

import lombok.val;
import me.nekocloud.base.gamer.constans.PurchaseType;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.core.api.inventory.impl.CorePaginatedInventory;
import me.nekocloud.core.api.inventory.itemstack.Material;
import me.nekocloud.core.api.inventory.itemstack.builder.ItemBuilder;
import me.nekocloud.core.api.inventory.markup.BaseInventorySimpleMarkup;
import me.nekocloud.core.api.schedulerT.CommonScheduler;
import me.nekocloud.reports.ReportManager;
import me.nekocloud.reports.type.Report;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ReportInventory extends CorePaginatedInventory {

    public ReportInventory() {
        super(5, "Жалобы");
    }

    @Override
    public void drawInventory(final @NotNull CorePlayer player) {
        int intruderCount = 0;
        int reportCount = 0;

        setInventoryMarkup(new BaseInventorySimpleMarkup(inventoryRows));

        getInventoryMarkup().addHorizontalRow(2, 1);
        getInventoryMarkup().addHorizontalRow(3, 1);
        getInventoryMarkup().addHorizontalRow(4, 1);

        for (String intruderName : ReportManager.INSTANCE.getReportMap().keySet()) {
            CorePlayer intruderPlayer = NekoCore.getInstance().getOfflinePlayer(intruderName);
            List<String> itemLore = new ArrayList<>();
            itemLore.add("");
            itemLore.add("§7Нарушитель " + intruderPlayer.getDisplayName());
            itemLore.add("");

            if (intruderPlayer.isOnline()) {
                itemLore.add("§7Текущий сервер: §f" + intruderPlayer.getBukkitServer().getName());

            } else {

                itemLore.add("§cДанный игрок не в сети!");
            }

            itemLore.add("§7Всего жалоб: " + ReportManager.INSTANCE.getReportsByIntruder(intruderName).size());
            itemLore.add("");
            itemLore.add("§7Текущие жалобы:");
            for (Report report : ReportManager.INSTANCE.getReportsByIntruder(intruderName)) {
                CorePlayer offlineOwnerPlayer = NekoCore.getInstance().getOfflinePlayer(report.getReportOwner());
                itemLore.add(" §8- " + offlineOwnerPlayer.getDisplayName() + "§7: " + offlineOwnerPlayer.getGroup().getSuffix() + report
                        .getReportReason() + "§8 " + (new Timestamp(report.getReportDate())).toGMTString() + ")");
                reportCount++;
            }
            itemLore.add("");
            itemLore.add("§eНажмите ЛКМ, чтобы телепортироваться на сервер нарушителя!");
            itemLore.add("§eНажмите ПКМ, чтобы отменить и удалить жалобу");
            itemLore.add("§eНажмите Q, чтобы забанить игрока");

            addItemToMarkup(ItemBuilder.newBuilder(Material.SKULL_ITEM)
                    .setDurability(3)
                    .setPlayerSkull(intruderName)

                    .setDisplayName("§eЖалоба #" + intruderCount)
                    .addLore(itemLore.toArray(new String[0]))

                    .build(), (CorePlayer1, inventoryClickEvent) -> {
                final BukkitServer bukkitServer;
                switch (inventoryClickEvent.getMouseAction()) {
                    case LEFT:

                        if (intruderPlayer.getName().equalsIgnoreCase(player.getName())) {
                            player.sendMessage("§§d§lЖАЛОБЫ §8| §cОшибка, вы не можете следить за собой!");
                            return;
                        }

                        if (!intruderPlayer.isOnline()) {
                            player.sendMessage("§d§lЖАЛОБЫ  §8| §cОшибка, Данный игрок не в сети!");
                            return;
                        }

                        bukkitServer = intruderPlayer.getBukkitServer();

                        player.closeInventory();
                        player.connectToServer(bukkitServer);

                        (new CommonScheduler("report-commands-" + RandomStringUtils.randomAlphanumeric(8)) {
                            public void run() {
                                bukkitServer.dispatchCommand("minecraft:tp " + player.getName() + " " + intruderName);
                                bukkitServer.dispatchCommand("vanish " + player.getName());
                            }
                        }).runLater(2L, TimeUnit.SECONDS);
                        for (CorePlayer onlineStaff : NekoCore.getInstance().getOnlinePlayers())
                            onlineStaff.sendMessage("§d§lМОДЕРАЦИЯ §8| " + player.getDisplayName() + " §fотправился следить за " + intruderPlayer.getDisplayName());
                        break;

                    case RIGHT:

                        if (intruderPlayer.getName().equalsIgnoreCase(player.getName())) {
                            player.sendMessage("§d§lЖАЛОБЫ  §8| §cОшибка, вы не можете удалить на себя жалобу!");
                            return;
                        }

                        ReportManager.INSTANCE.getReportMap().removeAll(intruderName.toLowerCase());

                        player.sendMessage("§d§lЖАЛОБЫ §8| §fЖалобы на игрока " + intruderPlayer.getDisplayName() + " §fбыли отменены");

                        for (Report report : ReportManager.INSTANCE.getReportsByIntruder(intruderName)) {
                            CorePlayer offlineOwnerPlayer = NekoCore.getInstance().getOfflinePlayer(report.getReportOwner());

                            offlineOwnerPlayer.sendMessage("§d§lЖАЛОБЫ §8| " + player.getDisplayName() + " §fрассмотрел Вашу жалобу на игрока" +
                                    intruderPlayer.getDisplayName() + " §fи признал его §cНЕВИНОВНЫМ");
                        }

                        for (CorePlayer onlineStaff : NekoCore.getInstance().getOnlinePlayers())
                            onlineStaff.sendMessage("§d§lЖАЛОБЫ §8| " + player.getDisplayName() + " §fотменил все жалобы на игрока " + intruderPlayer.getDisplayName());

                        player.closeInventory();
                        break;
                    case DROP:

                        if (intruderPlayer.getName().equalsIgnoreCase(player.getName())) {
                            player.sendMessage("§d§lЖАЛОБЫ §8| §cОшибка, вы не можете принять решение по своей жалобе!");
                            return;
                        }

                        bukkitServer = intruderPlayer.getBukkitServer();

                        (new CommonScheduler("report-commands-" + RandomStringUtils.randomAlphanumeric(8)) {
                            public void run() {
                                bukkitServer.dispatchCommand("ban " + intruderName + " использование читов");
                            }
                        }).runLater(2L, TimeUnit.SECONDS);

                        for (val onlineStaff : NekoCore.getInstance().getOnlinePlayers())
                            onlineStaff.sendMessage("§d§lЖАЛОБЫ §8| " + player.getDisplayName() + " §fзабанил " + intruderPlayer.getDisplayName() + " §7по причине §cчиты");

                        for (val report : ReportManager.INSTANCE.getReportsByIntruder(intruderName)) {
                            val offlineOwnerPlayer = NekoCore.getInstance().getOfflinePlayer(report.getReportOwner());

                            offlineOwnerPlayer.sendMessage("§d§lЖАЛОБЫ §8| " + player.getDisplayName() + " §fрассмотрел Вашу жалобу на игрока" +
                                    intruderPlayer.getDisplayName() + " §fи наказал его. Спасибо, что остались неравнодушны!");

                            offlineOwnerPlayer.sendMessage("§d§lЖАЛОБЫ §8| §fТы получил +1 Вирт за поданную жалобу!");
                            offlineOwnerPlayer.addMoney(PurchaseType.VIRTS, offlineOwnerPlayer.getMoney(PurchaseType.VIRTS) +1);
                        }
                        player.closeInventory();
                        ReportManager.INSTANCE.getReportMap().removeAll(intruderName.toLowerCase());
                        break;
                }
            });
            intruderCount++;
        }

        addItem(5, ItemBuilder.newBuilder(Material.SIGN)
                .setDisplayName("§6Общая информация")
                .addLore("§7Всего нарушителей: §e" + intruderCount, " §7Всего жалоб: §e" + reportCount)
                .build());

        if (intruderCount == 0 || reportCount == 0)
            addItem(23, ItemBuilder.newBuilder(Material.GLASS_BOTTLE)
                    .setDisplayName("§cУпс, все обосралось :c")
                    .addLore("§7Попробуйте зайти когда все не обосрется!")
                    .build());
    }

}
