package me.nekocloud.ask.inventory;

import me.nekocloud.ask.QuestionManager;
import me.nekocloud.ask.type.Question;
import me.nekocloud.ask.type.QuestionCategory;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.chat.ChatColor;
import me.nekocloud.core.api.inventory.impl.CorePaginatedInventory;
import me.nekocloud.core.api.inventory.impl.CoreSimpleInventory;
import me.nekocloud.core.api.inventory.itemstack.Material;
import me.nekocloud.core.api.inventory.itemstack.builder.ItemBuilder;
import me.nekocloud.core.api.inventory.markup.BaseInventorySimpleMarkup;
import me.nekocloud.core.api.utility.NumberUtil;
import me.nekocloud.core.api.connection.player.CorePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class AskInventory extends CoreSimpleInventory {

    public AskInventory() {
        super(5, "Вопросы");
    }

    @Override
    public void drawInventory(@NotNull CorePlayer player) {
        for (QuestionCategory questionCategory : QuestionCategory.QUESTION_CATEGORIES)
            drawCategoryItem(player, questionCategory);
    }

    protected void drawCategoryItem(@NotNull CorePlayer player, @NotNull QuestionCategory questionCategory) {
        addItem(questionCategory.getInventorySlot(), ItemBuilder.newBuilder(Material.SKULL_ITEM)
                .setDurability(3)
                .setPlayerSkull(questionCategory.getSkullTexture())

                .setDisplayName(ChatColor.GREEN + questionCategory.getCategoryName())
                .addLore("", "§7Вопросов в категории " + QuestionManager.INSTANCE.getActiveQuestions(questionCategory).size(),
                        "§7Нажмите, чтобы открыть список вопросов", "§7из данной категории")

                .build(), (inventory, inventoryClickEvent) ->
                (new QuestionCategoryInventory(questionCategory, this)).openInventory(player));
    }

    protected static class QuestionCategoryInventory extends CorePaginatedInventory {

        private final QuestionCategory questionCategory;
        private final CoreSimpleInventory previousInventory;

        protected QuestionCategoryInventory(@NotNull QuestionCategory questionCategory, @NotNull CoreSimpleInventory previousInventory) {
            super(5, questionCategory.getCategoryName());

            this.questionCategory = questionCategory;
            this.previousInventory = previousInventory;
        }

        public void drawInventory(CorePlayer player) {
            Collection<Question> activeQuestionCollection = QuestionManager.INSTANCE.getActiveQuestions(this.questionCategory);

            setInventoryMarkup(new BaseInventorySimpleMarkup(inventoryRows));

            getInventoryMarkup().addHorizontalRow(2, 1);
            getInventoryMarkup().addHorizontalRow(3, 1);
            getInventoryMarkup().addHorizontalRow(4, 1);

            addItem(5, ItemBuilder.newBuilder(Material.SIGN)
                    .setDisplayName("§aОбщая информация")
                    .addLore("§7Всего активных вопросов: §e" + activeQuestionCollection.size())
                    .build());

            int questionCounter = 0;
            for (Question question : activeQuestionCollection) {

                CorePlayer offlinePlayer = NekoCore.getInstance().getOfflinePlayer(question.getPlayerName());

                addItemToMarkup(ItemBuilder.newBuilder(Material.SKULL_ITEM)
                        .setDurability(3)
                        .setPlayerSkull(question.getPlayerName())
                        .setDisplayName("§7Вопрос #" + questionCounter)
                        .addLore("",
                                "§7Автор " + offlinePlayer.getDisplayName(),
                                "",
                                "§7Вопрос: §f" + question.getPlayerQuestion(),
                                "§7Категория: §e" + question.getQuestionCategory().getCategoryName(),
                                "", "§7Был задан:",
                                " §7" + NumberUtil.getTime(System.currentTimeMillis() - question.getQuestionDate()) + " назад",
                                "",
                                "§eНажмите ЛКМ, чтобы ответить на вопрос!",
                                "§eНажмите ПКМ, чтобы удалить вопрос!")
                        .build(), (inventory, inventoryClickEvent) -> {

                    switch (inventoryClickEvent.getMouseAction()) {

                        case RIGHT:
                            QuestionManager.INSTANCE.removeQuestion(question);
                            for (CorePlayer staffOnline : NekoCore.getInstance().getOnlinePlayers())
                                staffOnline.sendMessage("§d§lNeko§f§lCloud §8| " + player.getDisplayName() + " §fудалил вопрос игрока " + offlinePlayer.getDisplayName() + " §fпо категории: §e" + question.getQuestionCategory().getCategoryName());
                            offlinePlayer.sendMessage("§d§lNeko§f§lCloud §8| " + player.getDisplayName() + " §fудалил ваш вопрос по категории §e" + question.getQuestionCategory().getCategoryName());
                            player.sendMessage("" + offlinePlayer.getDisplayName() + "" + question.getQuestionCategory().getCategoryName());
                            break;

                        case LEFT:
                            player.closeInventory();
                            player.sendMessage("§d§lNeko§f§lCloud §8| §fДля того, чтобы ответить на вопрос " + offlinePlayer.getDisplayName(), " §fВам необходимо написать команду §e/ans " + offlinePlayer.getName() + " " + question.getQuestionCategory().name().toLowerCase() + " <ответ>");
                            break;
                    }
                });
                questionCounter++;
            }

            if (questionCounter == 0)
                addItem(23, ItemBuilder.newBuilder(Material.GLASS_BOTTLE)
                        .setDisplayName("Упс, ничего не найдено :c")
                        .addLore("§7Попробуйте зайти позже")
                        .build());

            addItem(41, ItemBuilder.newBuilder(Material.SKULL_ITEM)
                    .setDurability(3)
                    .setPlayerSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzYyNTkwMmIzODllZDZjMTQ3NTc0ZTQyMmRhOGY4ZjM2MWM4ZWI1N2U3NjMxNjc2YTcyNzc3ZTdiMWQifX19")
                    .setDisplayName("§cВернуться назад")
                    .addLore("§7Нажмите, чтобы вернуться назад")
                    .build(), (inventory, mouseAction) -> this.previousInventory.openInventory(player));
        }
    }
}
